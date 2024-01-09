package com.skyapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.List;

public class HourlyWeatherListDTO {
    private String location;

    private List<HourlyWeatherDTO> hourlyForecast = new ArrayList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<HourlyWeatherDTO> getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(List<HourlyWeatherDTO> hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public void addHourly(HourlyWeatherDTO hourlyForecast ) {
        this.hourlyForecast.add(hourlyForecast);
    }
}