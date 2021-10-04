package com.test;

import javax.sql.DataSource;

import com.test.start.AppTaskServiceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.test.dao")
public class TestApplication {

    /**
     * mybatis映射路径
     */
    @Value("${mapper.locations}")
    private Resource[] mapperLocations;

    /**
     * 构建指定bean name的mybatis SqlSessionFactory
     *
     * @param dataSource
     *            数据源
     * @return
     * @throws Exception
     */
    @Bean(name = "spring.sqlSessionFactory")
    public SqlSessionFactory buildSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(mapperLocations);
        return sessionFactory.getObject();
    }

    /**
     * 构建指定bean name的mybatis SqlSessionTemplate实例
     *
     * @param sqlSessionFactory
     * @return
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("spring.sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestApplication.class, args);
        AppTaskServiceFactory.get().startTask();
    }
}
