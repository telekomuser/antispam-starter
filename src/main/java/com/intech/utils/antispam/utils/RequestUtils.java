package com.intech.utils.antispam.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class RequestUtils {

    private final static String LOCALHOST_IPV4 = "127.0.0.1";
    private final static String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    public static String getRemoteIp(){
        HttpServletRequest request = getCurrentHttpRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if(ObjectUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if(ObjectUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if(ObjectUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        if(!ObjectUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }


    private static HttpServletRequest getCurrentHttpRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes)requestAttributes).getRequest();
        }
        log.debug("Not called in the context of an HTTP request");
        return null;
    }
}
