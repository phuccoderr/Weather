package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/realtime")
public class RealtimeWeatherApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);
    private RealtimeWeatherService realtimeWeatherService;
    private GeolocationService locationService;
    private ModelMapper modelMapper;


    public RealtimeWeatherApiController(RealtimeWeatherService realtimeWeatherService,
                                        GeolocationService locationService,
                                        ModelMapper mapper) {
        this.realtimeWeatherService = realtimeWeatherService;
        this.locationService = locationService;
        this.modelMapper = mapper;
    }

    @GetMapping
    public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress = CommononUtility.getIPAddress(request);
        try {
            Location locationFromIP = locationService.getLocation(ipAddress);
            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);
            RealtimeWeatherDTO dto = entity2DTO(realtimeWeather);

            return ResponseEntity.ok(dto);

        } catch (GeoLocationException e) {
            LOGGER.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            LOGGER.error(e.getMessage(),e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealtimeByLocationCode(@PathVariable("locationCode") String locationCode) {
        try {
            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
            RealtimeWeatherDTO dto = entity2DTO(realtimeWeather);

            return ResponseEntity.ok(dto);
        } catch (LocationNotFoundException e) {
            LOGGER.error(e.getMessage(),e);
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealtimeByLocationCode(@PathVariable("locationCode") String locationCode,
                                                          @RequestBody @Valid RealtimeWeather realtimeWeatherInRequest) {
        realtimeWeatherInRequest.setLocationCode(locationCode);
        try {
            RealtimeWeather update = realtimeWeatherService.update(locationCode, realtimeWeatherInRequest);
            RealtimeWeatherDTO dto = entity2DTO(update);

            return ResponseEntity.ok(dto);
        } catch (LocationNotFoundException e) {
            LOGGER.error(e.getMessage(),e);
            return ResponseEntity.notFound().build();
        }

    }
    private RealtimeWeatherDTO entity2DTO(RealtimeWeather realtimeWeather) {
        return modelMapper.map(realtimeWeather,RealtimeWeatherDTO.class);
    }
}
