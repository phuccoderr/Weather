package com.skyapi.weatherforecast.realtime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

@WebMvcTest(RealtimeWeatherApiController.class)
public class RealtimeWeatherApiControllerTests {
    private static final String END_POINT_PATH = "/v1/realtime";
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockBean RealtimeWeatherService realtimeWeatherService;
    @MockBean
    GeolocationService locationService;

    @Test
    public void testGetShouldReturn400BadRequest() throws Exception, GeoLocationException {
        Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(GeoLocationException.class);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetShouldReturn404NotFound() throws Exception, GeoLocationException, LocationNotFoundException {
        Location location = new Location();
        Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
        Mockito.when(realtimeWeatherService.getByLocation(location)).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetShouldReturn200OK() throws Exception, GeoLocationException, LocationNotFoundException {
        Location location = new Location();
        location.setCode("SFCA_USA");
        location.setCityName("San Fransisco");
        location.setRegionName("California");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(12);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(5);

        realtimeWeather.setLocation(location);
        location.setRealtimeWeather(realtimeWeather);

        Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
        Mockito.when(realtimeWeatherService.getByLocation(location)).thenReturn(realtimeWeather);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPutShouldReturn400BadRequest() throws LocationNotFoundException, Exception {
        String code = "ABC";

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(30);
        realtimeWeather.setHumidity(132);
        realtimeWeather.setPrecipitation(188);
        realtimeWeather.setStatus("Cl");
        realtimeWeather.setWindSpeed(500);

        String body = mapper.writeValueAsString(realtimeWeather);

        mockMvc.perform(put(END_POINT_PATH + "/" + code).contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPutShouldReturn404NotFound() throws LocationNotFoundException, Exception {
        String code = "ABC";

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(30);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(50);

        Mockito.when(realtimeWeatherService.update(code,realtimeWeather)).thenThrow(LocationNotFoundException.class);

        String body = mapper.writeValueAsString(realtimeWeather);

        mockMvc.perform(put(END_POINT_PATH + "/" + code).contentType("application/json").content(body))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPutShouldReturn200OK() throws LocationNotFoundException, Exception {
        String code = "SFCA_USA";

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(30);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(50);

        Location location = new Location();
        location.setCode(code);
        location.setCityName("San Fransisco");
        location.setRegionName("California");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        realtimeWeather.setLocation(location);
        location.setRealtimeWeather(realtimeWeather);

        Mockito.when(realtimeWeatherService.update(code,realtimeWeather)).thenReturn(realtimeWeather);

        String body = mapper.writeValueAsString(realtimeWeather);

        mockMvc.perform(put(END_POINT_PATH + "/" + code).contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
