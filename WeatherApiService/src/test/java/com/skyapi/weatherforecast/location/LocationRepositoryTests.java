package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class LocationRepositoryTests {
    @Autowired private LocationRepository repo;

    @Test
    public void testAddSuccess() {
        Location location = new Location();
        location.setCode("MBMH_IN");
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        Location saved = repo.save(location);

        Assertions.assertThat(saved).isNotNull();
    }

    @Test
    public void testListSuccess() {
        List<Location> locations = repo.findUnTrashed();

        locations.forEach(System.out::println);
    }

    @Test
    public void testGetLocation() {
        Location locations = repo.findByCode("NYC_USA");
        System.out.println(locations);
        Assertions.assertThat(locations).isNotNull();
    }

    @Test
    public void testAddRealtimeWeatherData() {
        String locationCode = "NYC_USA";

        Location location = repo.findByCode(locationCode);

        RealtimeWeather realtimeWeather = location.getRealtimeWeather();

        if (realtimeWeather == null) {
            realtimeWeather = new RealtimeWeather();
            realtimeWeather.setLocation(location);
            location.setRealtimeWeather(realtimeWeather);
        }
        realtimeWeather.setTemperature(-1);
        realtimeWeather.setHumidity(30);
        realtimeWeather.setPrecipitation(40);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(15);
        realtimeWeather.setLastUpdated(new Date());

        Location save = repo.save(location);
        Assertions.assertThat(save.getRealtimeWeather().getLocationCode()).isEqualTo(locationCode);

    }

    @Test
    public void testAddHourlyWeatherData() {
        Location location = repo.findById("NYC_USA").get();

        List<HourlyWeather> listHourlyWeathers = location.getListHourlyWeathers();
        HourlyWeather forecast1 = new HourlyWeather().id(location,8)
                .temperature(20)
                .precipitation(60)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather().id(location,9)
                .temperature(20)
                .precipitation(60)
                .status("Cloudy");

        listHourlyWeathers.add(forecast1);
        listHourlyWeathers.add(forecast2);

        Location saved = repo.save(location);
        Assertions.assertThat(saved.getListHourlyWeathers()).isNotEmpty();

    }
}
