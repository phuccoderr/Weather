package com.skyapi.weatherforecast;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class GeolocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
    private String DBPath = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
    private IP2Location ipLocator = new IP2Location();

    public GeolocationService() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(DBPath);
            byte[] data = inputStream.readAllBytes();
            ipLocator.Open(data);
            inputStream.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }
    }

    public Location getLocation(String ipAddress) throws GeoLocationException {
        try {
            IPResult ipResult = ipLocator.IPQuery(ipAddress);
            if (!"OK".equals(ipResult.getStatus())) {
                throw new GeoLocationException("GeoLocation fail with status: " + ipResult.getStatus());
            }

            LOGGER.info(ipResult.toString());

            return new Location(ipResult.getCity(),ipResult.getRegion(),ipResult.getCountryLong(),ipResult.getCountryShort());
        } catch (IOException e) {
            throw new GeoLocationException("Error querying IP Database",e);
        }

    }
}
