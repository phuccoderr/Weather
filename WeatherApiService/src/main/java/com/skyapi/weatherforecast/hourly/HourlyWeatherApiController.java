package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationService;
import com.skyapi.weatherforecast.realtime.CommononUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
@Validated
public class HourlyWeatherApiController {

    @Autowired private HourlyWeatherService hourlyService;
    @Autowired private GeolocationService geolocationService;
    @Autowired private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> listHourlyWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress = CommononUtility.getIPAddress(request);

        int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
        try {
            Location locationFromIP = geolocationService.getLocation(ipAddress);

            List<HourlyWeather> listHourlyWeathers = hourlyService.getByLocation(locationFromIP, currentHour);

            if (listHourlyWeathers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(entity2DTO(listHourlyWeathers));
        } catch (GeoLocationException e) {
           return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyWeatherByIPAddress(@PathVariable("locationCode") String locationCode,
                                                          HttpServletRequest request) {
        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
            List<HourlyWeather> listHourlyWeathers = hourlyService.getByLocationByCode(locationCode,currentHour);

            if (listHourlyWeathers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(entity2DTO(listHourlyWeathers));
        }  catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }   catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateHourlyForecast(@PathVariable("locationCode") String locationCode,
                                                  @RequestBody @Valid List<HourlyWeatherDTO> listDTO) throws BadRequestException {
        if (listDTO.isEmpty()) {
            throw new BadRequestException("Hourly forecast data cannot be empty");
        }
        listDTO.forEach(System.out::println);
        List<HourlyWeather> hourlyWeathers = listDTO2ListEntity(listDTO);

        hourlyWeathers.forEach(System.out::println);

        try {
            List<HourlyWeather> updatedHourlyWeathers = hourlyService.updateByLocationCode(locationCode, hourlyWeathers);
            return  ResponseEntity.ok(lístEntity2DTO(updatedHourlyWeathers));
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
//
//        try {
//            List<HourlyWeather> updatedHourly = hourlyService.updateByLocationCode(locationCode, listDTO);
//
//            return ResponseEntity.ok(listDTO2ListEntity(updatedHourly));
//        } catch (LocationNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
    }

    private HourlyWeatherListDTO lístEntity2DTO(List<HourlyWeather> hourlyWeathers) {
        Location location = hourlyWeathers.get(0).getId().getLocation();

        HourlyWeatherListDTO hourlyWeatherListDTO = new HourlyWeatherListDTO();
        hourlyWeatherListDTO.setLocation(location.toString());
        hourlyWeathers.forEach(
                entity -> {
                    hourlyWeatherListDTO.addHourly(modelMapper.map(entity,HourlyWeatherDTO.class));
                }
        );
        return hourlyWeatherListDTO;
    }

    private List<HourlyWeather> listDTO2ListEntity(List<HourlyWeatherDTO> listDTO) {
        List<HourlyWeather> listEntity = new ArrayList<>();
        listDTO.forEach(
                dto -> {
                    listEntity.add(modelMapper.map(dto,HourlyWeather.class));
                }
        );
        return listEntity;
    }

    public HourlyWeatherListDTO entity2DTO(List<HourlyWeather> hourlyWeathers) {
        Location location = hourlyWeathers.get(0).getId().getLocation();

        HourlyWeatherListDTO resultDTO = new HourlyWeatherListDTO();
        resultDTO.setLocation(location.toString());

        hourlyWeathers.forEach(hourlyWeather -> {
            HourlyWeatherDTO map = modelMapper.map(hourlyWeather, HourlyWeatherDTO.class);
            resultDTO.addHourly(map);
        });
        return modelMapper.map(resultDTO,HourlyWeatherListDTO.class);

    }
}
