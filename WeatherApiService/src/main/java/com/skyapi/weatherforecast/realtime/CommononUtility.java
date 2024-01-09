package com.skyapi.weatherforecast.realtime;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommononUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommononUtility.class);

    public static String getIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARED-FOR");
        System.out.println(ip);
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        LOGGER.info("Client's IP Address: " + ip);

        return ip;
    }

}
