package com.test.util;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 判断系统类型win/Linux 下获取ip
 * 
 * @since 2018年7月19日 下午2:01:12
 * @author zhangzhen
 *
 */
public class IpUtils {
    private final static Logger LOG = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取本地IP地址
     *
     * @throws SocketException
     */
    public static String getLocalIP() {
        if (isWindowsOS()) {

            String ipAddr = null;
            try {
                ipAddr = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                LOG.error("获取本机IP地址异常", e);
            }
            return ipAddr;
        } else {
            return getLinuxLocalIp();
        }
    }

    /**
     * 判断操作系统是否是Windows
     *
     * @return
     */
    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    /**
     * 获取本地Host名称
     */
    public static String getLocalHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     * @throws SocketException
     */
    private static String getLinuxLocalIp() {
        String ip = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress().toString();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            LOG.error("获取主机IP地址异常", e);
        }
        return ip;
    }

    /**
     * 获取当前网站IP
     *
     * @return IP
     */
    public static String getServerIP() {
        String serverIp = "";
        Enumeration<?> allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<?> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip instanceof Inet4Address && !"127.0.0.1".equals(ip.getHostAddress()) && ip.isSiteLocalAddress()) {
                        serverIp = ip.getHostAddress();
                        return serverIp;
                    }
                }
            }
        } catch (Exception ex) {
            return null;
        }
        return serverIp;
    }

}
