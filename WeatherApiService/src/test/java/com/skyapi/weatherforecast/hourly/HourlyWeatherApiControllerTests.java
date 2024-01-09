package com.skyapi.weatherforecast.hourly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;
import com.skyapi.weatherforecast.hourly.HourlyWeatherService;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebMvcTest(HourlyWeatherApiController.class)
public class HourlyWeatherApiControllerTests {
    private static final String END_POINT_PATH = "/v1/hourly";

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper mapper;

    @MockBean private HourlyWeatherService hourlyService;
    @MockBean private GeolocationService geolocationService;

    @Test
    public void testUpdateShouldReturn400BadRequestNoData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";

        List<HourlyWeatherDTO> listDTO = Collections.emptyList();

        String bodyContent = mapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0]", Is.is("Hourly forecast data cannot be empty")))
                .andDo(print());

    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";

        HourlyWeatherDTO forecastDTO1= new HourlyWeatherDTO();
        forecastDTO1.setHourOfDay(10);
        forecastDTO1.setPrecipitation(700);
        forecastDTO1.setTemperature(13);
        forecastDTO1.setStatus("Cloudy");

        HourlyWeatherDTO forecastDTO2= new HourlyWeatherDTO();
        forecastDTO2.setHourOfDay(11);
        forecastDTO2.setPrecipitation(60);
        forecastDTO2.setTemperature(15);
        forecastDTO2.setStatus("Sunny");

        List<HourlyWeatherDTO> listDTO = List.of(forecastDTO1,forecastDTO2);

        String bodyContent = mapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0]", Matchers.containsString("updateHourlyForecast.listDTO[0].precipitation: Precipitation must be in the range -50 to 50")))
                .andDo(print());

    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception{
        String locationCode = "ABC";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDTO forecastDTO1= new HourlyWeatherDTO();
        forecastDTO1.setHourOfDay(10);
        forecastDTO1.setPrecipitation(70);
        forecastDTO1.setTemperature(13);
        forecastDTO1.setStatus("Cloudy");

        List<HourlyWeatherDTO> listDTO = List.of(forecastDTO1);

        String bodyContent = mapper.writeValueAsString(listDTO);

        Mockito.when(hourlyService.updateByLocationCode(Mockito.eq(locationCode),Mockito.anyList()))
                .thenThrow(LocationNotFoundException.class);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDTO forecastDTO1= new HourlyWeatherDTO();
        forecastDTO1.setHourOfDay(10);
        forecastDTO1.setPrecipitation(70);
        forecastDTO1.setTemperature(13);
        forecastDTO1.setStatus("Cloudy");

        HourlyWeatherDTO forecastDTO2= new HourlyWeatherDTO();
        forecastDTO2.setHourOfDay(11);
        forecastDTO2.setPrecipitation(60);
        forecastDTO2.setTemperature(15);
        forecastDTO2.setStatus("Sunny");

        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(13)
                .precipitation(71)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(16)
                .precipitation(61)
                .status("Sunny");

        var hourlyForecast = List.of(forecast1,forecast2);


        List<HourlyWeatherDTO> listDTO = List.of(forecastDTO1,forecastDTO2);

        String bodyContent = mapper.writeValueAsString(listDTO);

        Mockito.when(hourlyService.updateByLocationCode(Mockito.eq(locationCode),Mockito.anyList()))
                        .thenReturn(hourlyForecast);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
                .andExpect(status().isOk())
                .andDo(print());

    }
}
