package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RealtimeWeatherService {
    @Autowired private RealtimeWeatherRepository realtimeRepo;
    @Autowired private LocationRepository locationRepo;

    public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
        System.out.println("code: " + location.getCountryCode());
        System.out.println("country: " + location.getCityName());
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        RealtimeWeather realtimeWeather = realtimeRepo.findByCountryCodeAndCity(countryCode, cityName);
        if (realtimeWeather == null) {
            throw new LocationNotFoundException("No Location Found with the given country code and city name!");
        }
        return realtimeWeather;
    }

    public RealtimeWeather getByLocationCode(String locationCode) throws LocationNotFoundException {
        RealtimeWeather realtimeWeather = realtimeRepo.findByLocationCode(locationCode);

        if (realtimeWeather == null) {
            throw new LocationNotFoundException("No Location Found with the given location code!");
        }

        return realtimeWeather;

    }

    public RealtimeWeather update(String locationCode,RealtimeWeather realtimeWeather) throws LocationNotFoundException {
        Location location = locationRepo.findByCode(locationCode);
        if (location == null) {
            throw new LocationNotFoundException("No Location Found with the given location code!");
        }
        realtimeWeather.setLocation(location);
        realtimeWeather.setLastUpdated(new Date());

        if (location.getRealtimeWeather() == null) {
            location.setRealtimeWeather(realtimeWeather);
            Location updatedLocation = locationRepo.save(location);
            return updatedLocation.getRealtimeWeather();
        }

        return realtimeRepo.save(realtimeWeather);

    }


}
