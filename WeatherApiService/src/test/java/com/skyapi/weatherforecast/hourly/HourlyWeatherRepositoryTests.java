package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourly.HourlyWeatherRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class HourlyWeatherRepositoryTests {
    @Autowired private HourlyWeatherRepository repo;

    @Test
    public void testAdd() {
        String locationCode = "DELHI_IN";
        Location location = new Location().code(locationCode);
        HourlyWeather hourlyWeather = new HourlyWeather()
                .location(location)
                .hourOfDay(12)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather saved = repo.save(hourlyWeather);
        Assertions.assertThat(saved).isNotNull();
    }

    @Test
    public void testDelete() {
        Location location = new Location().code("DELHI_IN");
        HourlyWeatherId id = new HourlyWeatherId(12,location);


        repo.deleteById(id);
        Optional<HourlyWeather> hourlyWeather = repo.findById(id);

        Assertions.assertThat(hourlyWeather).isNotPresent();
    }

    @Test
    public void testFindByLocationCode() {
        String code = "MBMH_IN";
        List<HourlyWeather> listHourlyWeather = repo.findByLocationCodeAndHour(code, 9);

        listHourlyWeather.forEach(System.out::println);
    }

}
