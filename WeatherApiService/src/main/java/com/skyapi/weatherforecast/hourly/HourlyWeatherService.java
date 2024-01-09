package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import com.skyapi.weatherforecast.location.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HourlyWeatherService {

    @Autowired private HourlyWeatherRepository hourlyRepo;
    @Autowired private LocationRepository locationRepo;

    public List<HourlyWeather> getByLocation(Location location,int current) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepo.findByCountryCodeAndCityName(countryCode, cityName);

        if (locationInDB == null) {
            throw new LocationNotFoundException("No location found with given country code and city name");
        }
        return hourlyRepo.findByLocationCodeAndHour(locationInDB.getCode(),current);

    }

    public List<HourlyWeather> getByLocationByCode(String locationCode,int current) throws LocationNotFoundException {
        Location location = locationRepo.findByCode(locationCode);
        if (location == null ) {
            throw new LocationNotFoundException("No location found with given country code and city name");
        }
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();
        Location locationInDB = locationRepo.findByCountryCodeAndCityName(countryCode, cityName);


        return hourlyRepo.findByLocationCodeAndHour(locationInDB.getCode(),current);
    }

    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyWeathers) throws LocationNotFoundException {
        Location location = locationRepo.findByCode(locationCode);
        if (location == null ) {
            throw new LocationNotFoundException("No location found with code:" + locationCode);
        }

        for(HourlyWeather item : hourlyWeathers) {
            item.getId().setLocation(location);
        }

        List<HourlyWeather> listHourlyWeatherInDB = location.getListHourlyWeathers();
        List<HourlyWeather> listHourlyWeatherToRemoved = new ArrayList<>();

        for (HourlyWeather item : listHourlyWeatherInDB) {
            if (!hourlyWeathers.contains(item)) {
                listHourlyWeatherToRemoved.add(item.getShallowCopy());
            }
        }

        for (HourlyWeather item : listHourlyWeatherToRemoved) {
            listHourlyWeatherInDB.remove(item);
        }

        return (List<HourlyWeather>) hourlyRepo.saveAll(hourlyWeathers);
    }
}
