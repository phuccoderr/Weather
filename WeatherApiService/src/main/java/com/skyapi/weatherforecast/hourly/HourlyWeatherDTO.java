package com.skyapi.weatherforecast.hourly;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public class HourlyWeatherDTO {
    @JsonProperty("house_of_day")
    private int hourOfDay;
    @Range(min = -50, max = 50, message = "Temperator must be in the range -50 to 50")
    private int temperature;
    @Range(min = 0, max = 100, message = "Precipitation must be in the range -50 to 50")
    private int precipitation;
    @NotBlank(message = "Status must not be empty")
    @Length(min = 3,max = 50, message = "Status must be in between 3-50 characters")
    private String status;

    public HourlyWeatherDTO() {
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(int precipitation) {
        this.precipitation = precipitation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HourlyWeatherDTO{" +
                "hourOfDay=" + hourOfDay +
                ", temperature=" + temperature +
                ", precipitation=" + precipitation +
                ", status='" + status + '\'' +
                '}';
    }
}
