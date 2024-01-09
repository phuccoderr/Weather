package com.skyapi.weatherforecast;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class IP2LocationTests {
    private String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";

    @Test
    public void testValidIP() throws IOException {
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "abc";

        IPResult ipResult = ipLocator.IPQuery(ipAddress);
        Assertions.assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");

        System.out.println("Result is: " + ipResult);
    }

    @Test
    public void testValidIP1() throws IOException {
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "108.30.178.78"; //New York

        IPResult ipResult = ipLocator.IPQuery(ipAddress);
        Assertions.assertThat(ipResult.getStatus()).isEqualTo("OK");

        System.out.println("Result is: " + ipResult);
    }

    @Test
    public void testValidIP2() throws IOException {
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "103.48.198.141"; // Delhi

        IPResult ipResult = ipLocator.IPQuery(ipAddress);
        Assertions.assertThat(ipResult.getStatus()).isEqualTo("OK");

        System.out.println("Result is: " + ipResult);
    }

}
