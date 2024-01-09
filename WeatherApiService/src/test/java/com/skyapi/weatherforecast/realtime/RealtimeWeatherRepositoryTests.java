package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.RealtimeWeather;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RealtimeWeatherRepositoryTests {
    @Autowired private RealtimeWeatherRepository repo;

    @Test
    public void testUpdate() {
        String location = "NYC_USA";
        RealtimeWeather realtimeWeather = repo.findById(location).get();
        realtimeWeather.setTemperature(-2);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setPrecipitation(42);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(12);
        realtimeWeather.setLastUpdated(new Date());

        RealtimeWeather save = repo.save(realtimeWeather);
        Assertions.assertThat(save.getHumidity()).isEqualTo(32);
    }

    @Test
    public void testFindByCountryCodeAndCityNotFound() {
        String code = "JP";
        String city = "Tokyo";
        RealtimeWeather realtimeWeather = repo.findByCountryCodeAndCity(code,city);

        Assertions.assertThat(realtimeWeather).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityOK() {
        String code = "US";
        String city = "New York City";
        RealtimeWeather realtimeWeather = repo.findByCountryCodeAndCity(code,city);

        Assertions.assertThat(realtimeWeather).isNotNull();
    }
}
