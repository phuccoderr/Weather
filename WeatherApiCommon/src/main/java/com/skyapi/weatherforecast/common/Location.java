package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "locations")
public class Location {

    @Column(nullable = false,unique = true,length = 12)
    @Id
    @NotNull(message = "Location code cannot be null")
    @Length(min = 3,max = 12, message = "Location code must have 3-12 characters")
    private String code;

    @Column(nullable = false,length = 128)
    @JsonProperty("city_name")
    @NotBlank(message = "City name cannot be left blank")
    private String cityName;

    @Column(nullable = false,length = 128)
    @JsonProperty("region_name")
    @NotNull
    private String regionName;

    @Column(nullable = false,length = 64)
    @JsonProperty("country_name")
    @NotBlank
    private String countryName;

    @Column(nullable = false,length = 2)
    @JsonProperty("country_code")
    @NotBlank
    private String countryCode;

    private boolean enabled;
    @JsonIgnore
    private boolean trashed;

    @OneToOne(mappedBy = "location",cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private RealtimeWeather realtimeWeather;

    @OneToMany(mappedBy = "id.location",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<HourlyWeather> listHourlyWeathers = new ArrayList<>();

    public Location() {
    }

    public Location(String cityName, String regionName, String countryName, String countryCode) {
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTrashed() {
        return trashed;
    }

    public void setTrashed(boolean trashed) {
        this.trashed = trashed;
    }

    public RealtimeWeather getRealtimeWeather() {
        return realtimeWeather;
    }

    public void setRealtimeWeather(RealtimeWeather realtimeWeather) {
        this.realtimeWeather = realtimeWeather;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(code, location.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return cityName + ", " + (regionName != null ? regionName : "") + ", ";
    }

    public List<HourlyWeather> getListHourlyWeathers() {
        return listHourlyWeathers;
    }

    public void setListHourlyWeathers(List<HourlyWeather> listHourlyWeathers) {
        this.listHourlyWeathers = listHourlyWeathers;
    }

    public Location code(String code) {
        setCode(code);
        return this;
    }
}
