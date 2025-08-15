package com.fatebug.base.utils;

import com.fatebug.base.core.constants.StringPool;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.function.Predicate;

/**
 * IP地址工具类
 * @author fatebug
 *
 */
public class IpUtil {

    private static final String[] IP_HEADER_NAMES = new String[]{"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    private static final Predicate<String> IP_PREDICATE = (ip) -> StringUtils.isBlank(ip) || StringPool.UNKNOWN.equalsIgnoreCase(ip);

    /**
     * 获取ip
     *
     * @return {String}
     */
    public static String getIP() {
        return getIP(ServletUtils.getRequest());
    }

    /**
     * 获取ip
     *
     * @param request HttpServletRequest
     * @return {String}
     */
    @Nullable
    public static String getIP(@Nullable HttpServletRequest request) {
        if (request == null) {
            return StringPool.EMPTY;
        }
        String ip = null;
        for (String ipHeader : IP_HEADER_NAMES) {
            ip = request.getHeader(ipHeader);
            if (!IP_PREDICATE.test(ip)) {
                break;
            }
        }
        if (IP_PREDICATE.test(ip)) {
            ip = request.getRemoteAddr();
        }
        return StringUtils.isBlank(ip) ? null : StringUtils.split(ip, StringPool.COMMA)[0];
    }

    /**
     * 私有化构造器
     */
    private IpUtil() {
    }

    /**
     * 获取真实IP地址
     * <p>使用getRealIP代替该方法</p>
     * @param request req
     * @return ip
     */
    @Deprecated
    public static String getClinetIpByReq(HttpServletRequest request) {
        // 获取客户端ip地址
        String clientIp = request.getHeader("x-forwarded-for");

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        /*
         * 对于获取到多ip的情况下，找到公网ip.
         */
        String sIP = null;
        if (clientIp != null && !clientIp.contains("unknown") && clientIp.indexOf(",") > 0) {
            String[] ipsz = clientIp.split(",");
            for (String anIpsz : ipsz) {
                if (!isInnerIP(anIpsz.trim())) {
                    sIP = anIpsz.trim();
                    break;
                }
            }
            /*
             * 如果多ip都是内网ip，则取第一个ip.
             */
            if (null == sIP) {
                sIP = ipsz[0].trim();
            }
        }
        if (sIP != null && sIP.contains("unknown")){
            sIP =sIP.replaceAll("unknown,", "");
            sIP = sIP.trim();
        }
        if ("".equals(sIP) || null == sIP){
            sIP = "127.0.0.1";
        }
        return sIP;
    }

    /**
     * 判断IP是否是内网地址
     * @param ipAddress ip地址
     * @return 是否是内网地址
     */
    public static boolean isInnerIP(String ipAddress) {
        boolean isInnerIp;
        long ipNum = getIpNum(ipAddress);
        /**
         私有IP：A类  10.0.0.0-10.255.255.255
         B类  172.16.0.0-172.31.255.255
         C类  192.168.0.0-192.168.255.255
         当然，还有127这个网段是环回地址
         **/
        long aBegin = getIpNum("10.0.0.0");
        long aEnd = getIpNum("10.255.255.255");

        long bBegin = getIpNum("172.16.0.0");
        long bEnd = getIpNum("172.31.255.255");

        long cBegin = getIpNum("192.168.0.0");
        long cEnd = getIpNum("192.168.255.255");
        isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
                || ipAddress.equals("127.0.0.1");
        return isInnerIp;
    }

    private static long getIpNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
    }

    private static boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }

    public static String getRealIP(HttpServletRequest request){
        // 获取客户端ip地址
        String clientIp = request.getHeader("x-forwarded-for");

        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        String[] clientIps = clientIp.split(",");
        if(clientIps.length <= 1) return clientIp.trim();

        // 判断是否来自CDN
        if(isComefromCDN(request)){
            if(clientIps.length>=2) return clientIps[clientIps.length-2].trim();
        }

        return clientIps[clientIps.length-1].trim();
    }

    private static boolean isComefromCDN(HttpServletRequest request) {
        String host = request.getHeader("host");
        return host.contains("www.189.cn") ||host.contains("shouji.189.cn") || host.contains(
                "image2.chinatelecom-ec.com") || host.contains(
                "image1.chinatelecom-ec.com");
    }

    public static final String LOCAL_HOST = "127.0.0.1";

    /**
     * 获取 服务器 hostname
     *
     * @return hostname
     */
    public static String getHostName() {
        String hostname;
        try {
            InetAddress address = InetAddress.getLocalHost();
            // force a best effort reverse DNS lookup
            hostname = address.getHostName();
            if (StringUtils.isEmpty(hostname)) {
                hostname = address.toString();
            }
        } catch (UnknownHostException ignore) {
            hostname = LOCAL_HOST;
        }
        return hostname;
    }
    /**
     * 获取 服务器 HostIp
     *
     * @return HostIp
     */
    public static String getHostIp() {
        String hostAddress;
        try {
            InetAddress address = IpUtil.getLocalHostLANAddress();
            // force a best effort reverse DNS lookup
            hostAddress = address.getHostAddress();
            if (StringUtils.isEmpty(hostAddress)) {
                hostAddress = address.toString();
            }
        } catch (UnknownHostException ignore) {
            hostAddress = LOCAL_HOST;
        }
        return hostAddress;
    }

    /**
     * https:// stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
     * 返回一个InetAddress对象，其中封装了最有可能是机器的LAN IP地址。
     * 此方法旨在用作JDK方法InetAddress.getLocalHost的替代品，因为该方法在Linux系统上不明确。
     * Linux系统以与常规LAN网络接口相同的方式枚举环回网络接口，但JDK InetAddress. getLocalHost方法没有指定在这种情况下用于选择返回地址的算法，并且通常会返回环回地址，这对网络通信无效。详情请点击此处。
     *
     * 此方法将扫描主机上所有网络接口上的所有IP地址，以确定最有可能是机器LAN地址的IP地址。
     * 如果机器有多个IP地址，如果机器有一个，
     * 此方法将首选站点本地IP地址（例如192.168.x. x或10.10.x. x，通常是IPv4）
     * （如果机器有不止一个，则将返回第一个站点本地地址），
     * 但如果机器没有站点本地地址，则此方法将仅返回找到的第一个非环回地址（IPv4或IPv6）。
     *
     * 如果此方法使用此选择算法找不到非环回地址，它将回退到调用并返回JDK方法InetAddress. getLocalHost的结果。
     * 投掷：
     * UnknownHostException–如果找不到机器的LAN地址。
     *
     * @throws UnknownHostException 如果找不到机器的LAN地址
     */
    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}
