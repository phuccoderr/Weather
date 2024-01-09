package com.skyapi.weatherforecast;

public class GeoLocationException extends Throwable {
    public GeoLocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeoLocationException(String message) {
        super(message);
    }
}
