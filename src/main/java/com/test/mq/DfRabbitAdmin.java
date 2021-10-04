package com.test.mq; /**
 * Copyright(C) 2018 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.CacheMode;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @author CaiYH
 * @desc 自定义的rabbitAdmin
 * @since 2018年7月10日 下午7:24:58
 */
public class DfRabbitAdmin extends RabbitAdmin implements BeanNameAware {
    private static final String ANONYMOUS_QUEUE_PATTERN = "^[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}$";

    private static final String DELAYED_MESSAGE_EXCHANGE = "x-delayed-message";

    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    // 初始化方法加锁
    private final Object lifecycleMonitor = new Object();

    private final ConnectionFactory connectionFactory;

    private volatile ConfigurableApplicationContext applicationContext;

    /**
     * 自定义rabbitTemplate，重写getRabbitTemplate方法
     **/
    private RabbitTemplate dfRabbitTemplate;

    private String qualifierName;

    private List<MyQueue> myQueues = new ArrayList<>();

    DfRabbitAdmin(ConnectionFactory connectionFactory) {
        super(connectionFactory);

        this.dfRabbitTemplate = new DfRabbitTemplate(connectionFactory);
        this.connectionFactory = connectionFactory;
    }

    @Override
    public RabbitTemplate getRabbitTemplate() {
        return this.dfRabbitTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        synchronized (this.lifecycleMonitor) {
            if (this.connectionFactory instanceof CachingConnectionFactory
                    && ((CachingConnectionFactory) this.connectionFactory).getCacheMode() == CacheMode.CONNECTION) {
                this.logger.warn("RabbitAdmin auto declaration is not supported with CacheMode.CONNECTION");
                return;
            }
            this.connectionFactory.addConnectionListener(new ConnectionListener() {
                // Prevent stack overflow...
                private final AtomicBoolean initializing = new AtomicBoolean(false);

                @Override
                public void onCreate(Connection connection) {
                    if (!initializing.compareAndSet(false, true)) {
                        return;
                    }
                    try {
                        initialize();
                    } finally {
                        initializing.compareAndSet(true, false);
                    }
                }

                @Override
                public void onClose(Connection connection) {
                }
            });
        }
    }

    /**
     * Declares all the exchanges, queues and bindings in the enclosing application context, if any.
     * It should be safe (but unnecessary) to call this method more than once.
     */
    @Override
    public void initialize() {

        if (this.applicationContext == null) {
            this.logger.debug("no ApplicationContext has been set, cannot auto-declare Exchanges, Queues, and Bindings");
            return;
        }
        initializeMyBusiness();
        this.logger.debug("Initializing declarations");

        Collection<Exchange> contextExchanges = new LinkedList<Exchange>(this.applicationContext.getBeansOfType(Exchange.class).values());
        Collection<Queue> contextQueues = new LinkedList<Queue>(this.applicationContext.getBeansOfType(Queue.class).values());
        Collection<Binding> contextBindings = new LinkedList<Binding>(this.applicationContext.getBeansOfType(Binding.class).values());

        @SuppressWarnings("rawtypes")
        Collection<Collection> collections = this.applicationContext.getBeansOfType(Collection.class, false, false).values();
        for (Collection<?> collection : collections) {
            if (!collection.isEmpty() && collection.iterator().next() instanceof Declarable) {
                for (Object declarable : collection) {
                    if (declarable instanceof Exchange) {
                        contextExchanges.add((Exchange) declarable);
                    } else if (declarable instanceof Queue) {
                        contextQueues.add((Queue) declarable);
                    } else if (declarable instanceof Binding) {
                        contextBindings.add((Binding) declarable);
                    }
                }
            }
        }

        final Collection<Exchange> exchanges = filterDeclarableRelations(contextExchanges);
        final Collection<Queue> queues = filterDeclarableRelations(contextQueues);
        final Collection<Binding> bindings = filterDeclarableRelations(contextBindings);

        for (Exchange exchange : exchanges) {
            if ((!exchange.isDurable() || exchange.isAutoDelete()) && this.logger.isInfoEnabled()) {
                this.logger.info("Auto-declaring a non-durable or auto-delete Exchange (" + exchange.getName() + ") durable:" + exchange.isDurable()
                        + ", auto-delete:" + exchange.isAutoDelete() + ". "
                        + "It will be deleted by the broker if it shuts down, and can be redeclared by closing and " + "reopening the connection.");
            }
        }

        for (Queue queue : queues) {
            if ((!queue.isDurable() || queue.isAutoDelete() || queue.isExclusive()) && this.logger.isInfoEnabled()) {
                this.logger.info("Auto-declaring a non-durable, auto-delete, or exclusive Queue (" + queue.getName() + ") durable:" + queue.isDurable()
                        + ", auto-delete:" + queue.isAutoDelete() + ", exclusive:" + queue.isExclusive() + ". "
                        + "It will be redeclared if the broker stops and is restarted while the connection factory is "
                        + "alive, but all messages will be lost.");
            }
        }

        this.dfRabbitTemplate.execute(channel -> {
            tryDeclareExchanges(channel, exchanges.toArray(new Exchange[exchanges.size()]));
            tryDeclareQueues(channel, queues.toArray(new Queue[queues.size()]));
            tryDeclareBindings(channel, bindings.toArray(new Binding[bindings.size()]));
            return null;
        });
        this.logger.debug("Declarations finished");

    }

    /**
     * 过滤本mq对应的队列，交换机，绑定关系
     */
    private <T extends Declarable> Collection<T> filterDeclarableRelations(Collection<T> declarables) {
        Collection<T> filtered = new ArrayList<T>();
        for (T declarable : declarables) {
            Collection<?> adminsWithWhichToDeclare = declarable.getDeclaringAdmins();
            // 增加一层判断，判断是当前admin的队列管理范围才注册
            if (declarable.shouldDeclare() && (adminsWithWhichToDeclare.isEmpty() || adminsWithWhichToDeclare.contains(this)) && checkMySetting(declarable)) {
                filtered.add(declarable);
            }
        }
        return filtered;
    }

    // private methods for declaring Exchanges, Queues, and Bindings on a Channel
    private void tryDeclareExchanges(final Channel channel, final Exchange... exchanges) throws IOException {
        for (final Exchange exchange : exchanges) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("declaring Exchange '" + exchange.getName() + "'");
            }

            if (!isDeclaringDefaultExchange(exchange)) {
                try {
                    if (exchange.isDelayed()) {
                        Map<String, Object> arguments = exchange.getArguments();
                        if (arguments == null) {
                            arguments = new HashMap<String, Object>();
                        } else {
                            arguments = new HashMap<String, Object>(arguments);
                        }
                        arguments.put("x-delayed-type", exchange.getType());
                        channel.exchangeDeclare(exchange.getName(), DELAYED_MESSAGE_EXCHANGE, exchange.isDurable(), exchange.isAutoDelete(),
                                exchange.isInternal(), arguments);
                    } else {
                        channel.exchangeDeclare(exchange.getName(), exchange.getType(), exchange.isDurable(), exchange.isAutoDelete(), exchange.isInternal(),
                                exchange.getArguments());
                    }
                } catch (IOException e) {
                    this.logger.error("declaring Exchange '" + exchange.getName() + "'");
                    e.printStackTrace();
                }
            }
        }
    }

    private DeclareOk[] tryDeclareQueues(final Channel channel, final Queue... queues) throws IOException {
        List<DeclareOk> declareOks = new ArrayList<>(queues.length);
        for (int i = 0; i < queues.length; i++) {
            Queue queue = queues[i];
            if (!queue.getName().startsWith("amq.")) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("declaring Queue '" + queue.getName() + "'");
                }
                try {
                    try {
                        DeclareOk declareOk = channel.queueDeclare(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(),
                                queue.getArguments());
                        declareOks.add(declareOk);
                    } catch (IllegalArgumentException e) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.error("Exception while declaring queue: '" + queue.getName() + "'");
                        }
                        try {
                            if (channel instanceof ChannelProxy) {
                                ((ChannelProxy) channel).getTargetChannel().close();
                            }
                        } catch (TimeoutException e1) {
                        }
                        throw new IOException(e);
                    }
                } catch (IOException e) {
                    this.logger.error("Exception while declaring queue: '" + queue.getName() + "'");
                }
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug(queue.getName() + ": Queue with name that starts with 'amq.' cannot be declared.");
            }
        }
        return declareOks.toArray(new DeclareOk[declareOks.size()]);
    }

    private void tryDeclareBindings(final Channel channel, final Binding... bindings) throws IOException {
        for (Binding binding : bindings) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Binding destination [" + binding.getDestination() + " (" + binding.getDestinationType() + ")] to exchange ["
                        + binding.getExchange() + "] with routing key [" + binding.getRoutingKey() + "]");
            }

            try {
                if (binding.isDestinationQueue()) {
                    if (!isDeclaringImplicitQueueBinding(binding)) {
                        channel.queueBind(binding.getDestination(), binding.getExchange(), binding.getRoutingKey(), binding.getArguments());
                    }
                } else {
                    channel.exchangeBind(binding.getDestination(), binding.getExchange(), binding.getRoutingKey(), binding.getArguments());
                }
            } catch (IOException e) {
                this.logger.error("Exception while declaring queue: '" + binding.getRoutingKey() + "'");
            }
        }
    }

    private boolean isDeclaringDefaultExchange(Exchange exchange) {
        if (isDefaultExchange(exchange.getName())) {
            this.logger.debug("Default exchange is pre-declared by server.");
            return true;
        }
        return false;
    }

    private boolean isDefaultExchange(String exchangeName) {
        return DEFAULT_EXCHANGE_NAME.equals(exchangeName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    private boolean isDeclaringImplicitQueueBinding(Binding binding) {
        if (isImplicitQueueBinding(binding)) {
            this.logger.debug("The default exchange is implicitly bound to every queue," + " with a routing key equal to the queue name.");
            return true;
        }
        return false;
    }

    private boolean isImplicitQueueBinding(Binding binding) {
        return isDefaultExchange(binding.getExchange()) && binding.getDestination().equals(binding.getRoutingKey());
    }

    /**
     * 解决注解方式服务生命队列注册的admin问题，初始化当前admin,需要初始化的queue，exchange,binding信息
     */
    @SuppressWarnings("rawtypes")
    private void initializeMyBusiness() {
        // 先获取消费者的class列表
        Map<String, ? extends AbstractDFRabbitListener> rabbitListenerMap = applicationContext.getBeansOfType(AbstractDFRabbitListener.class);

        // 遍历class，筛选出和当前admin相关的消费者
        Set<String> listenerNames = rabbitListenerMap.keySet();
        for (String listenerName : listenerNames) {
            AnnotatedBeanDefinition bd = null;
            BeanDefinition definition = this.applicationContext.getBeanFactory().getBeanDefinition(listenerName);
            if (definition instanceof AnnotatedBeanDefinition) {
                bd = (AnnotatedBeanDefinition) definition;
            } else {
                continue;
            }
            AnnotationMetadataReadingVisitor metadata = null;
            AnnotationMetadata am = bd.getMetadata();
            if (!(am instanceof AnnotationMetadataReadingVisitor)) {
                return;
            }
            // 获取和当admin先关消费者的queue,exchange,bindingkey列表
            // 仅上述队列进行初始化创建
            metadata = (AnnotationMetadataReadingVisitor) bd.getMetadata();
            String annotationName = RabbitListener.class.getName();
            Set<MethodMetadata> set = metadata.getAnnotatedMethods(RabbitListener.class.getName());
            if (null != set && !set.isEmpty()) {
                for (MethodMetadata mm : set) {
                    getMyQueueSetting(mm.getAllAnnotationAttributes(annotationName));
                }
            }
        }
    }

    /**
     * 解析出当前加的admin需要去注册的队列信息
     */
    @SuppressWarnings("unchecked")
    private void getMyQueueSetting(MultiValueMap<String, Object> attributes) {
        String valueStr = "value";
        if (attributes == null) {
            return;
        }
        MyQueue myQueue = new MyQueue();
        String adminBeanName = "";
        if (attributes.containsKey("admin")) {
            List<Object> admins = attributes.get("admin");
            if (admins != null && !admins.isEmpty()) {
                adminBeanName = admins.get(0).toString();
            }
        }
        // 判断当前队列是否注册到本admin连接
        if (!StringUtils.equals(adminBeanName, qualifierName)) {
            return;
        }
        // 解析队列配置信息
        if (!attributes.containsKey("bindings")) {
            return;
        }

        List<Object> attrLists = attributes.get("bindings");
        if (attrLists == null || attrLists.isEmpty()) {
            return;
        }
        LinkedHashMap<String, Object>[] bindingMapArray = (LinkedHashMap<String, Object>[]) attrLists.get(0);
        if (bindingMapArray == null || bindingMapArray.length == 0) {
            return;
        }
        LinkedHashMap<String, Object> bindingMaps = bindingMapArray[0];
        if (bindingMaps == null) {
            return;
        }
        // 解析exchange
        if (bindingMaps.containsKey("exchange")) {
            LinkedHashMap<String, Object> exchangeMap = (LinkedHashMap<String, Object>) bindingMaps.get("exchange");
            if (exchangeMap != null && exchangeMap.containsKey(valueStr)) {
                myQueue.setExchange(getSomeElValueFromEnv((String) exchangeMap.get(valueStr)));
            }
        }
        // 解析queue
        if (bindingMaps.containsKey(valueStr)) {
            LinkedHashMap<String, Object> queueMap = (LinkedHashMap<String, Object>) bindingMaps.get(valueStr);
            if (queueMap != null && queueMap.containsKey(valueStr)) {
                String queue = getSomeElValueFromEnv((String) queueMap.get(valueStr));

                if (StringUtils.isBlank(queue)) {
                    myQueue.setAnonymous(true);
                    myQueue.setQueuePattern(ANONYMOUS_QUEUE_PATTERN);
                } else {
                    myQueue.setQueue(queue);
                    myQueue.setAnonymous(false);
                }
            }
        }
        // 解析routeKey
        if (bindingMaps.containsKey("key")) {
            myQueue.setRoutingKey(getSomeElValueFromEnv((String) bindingMaps.get("key")));
        }
        myQueues.add(myQueue);
    }

    private boolean checkMySetting(Declarable declarable) {
        boolean checkFlag = false;

        for (MyQueue myQueues : myQueues) {
            if (declarable instanceof Exchange) {
                Exchange e = (Exchange) declarable;
                if (myQueues.getExchange().equals(e.getName())) {
                    checkFlag = true;
                }
            }
            if (declarable instanceof Queue) {
                Queue q = (Queue) declarable;
                if (myQueues.isAnonymous()) {
                    checkFlag = Pattern.matches(myQueues.getQueuePattern(), q.getName());
                } else {
                    checkFlag = myQueues.getQueue().equals(q.getName());
                }
            }
            if (declarable instanceof Binding) {
                Binding binding = (Binding) declarable;
                if (myQueues.getRoutingKey().equals(binding.getRoutingKey()) && myQueues.getExchange().equals(binding.getExchange())) {
                    checkFlag = true;
                }
            }
            if (checkFlag) {
                return checkFlag;
            }
        }
        return checkFlag;
    }

    /**
     * 感知当前admin的bean名称，设置成属性用于队列创建筛选
     *
     * @param currentBeanName
     */
    @Override
    public void setBeanName(String currentBeanName) {

        if (currentBeanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
            currentBeanName = currentBeanName.replaceFirst(SCOPED_TARGET_NAME_PREFIX, "");
        }
        this.qualifierName = currentBeanName;
    }

    /**
     * 使用SpringEL解析器对队列名进行完善 如 "${user}，早上好",替换为对应的完成字符串
     */
    private String getSomeElValueFromEnv(String template) {

        String result = template;
        try {
            // 处理${value}格式的属性替换
            PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");
            result = propertyPlaceholderHelper.replacePlaceholders(template, new PropertyPlaceholderHelper.PlaceholderResolver() {
                @Override
                public String resolvePlaceholder(String placeholderName) {
                    String value = applicationContext.getEnvironment().getProperty(placeholderName);
                    return value;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
