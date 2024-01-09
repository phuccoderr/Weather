package com.skyapi.weatherforecast.location;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {

    private static final String END_POINT_PATH = "/v1/locations";
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean LocationService service;

    @Test
    public void testAddShouldReturn400BadRequest() throws Exception {
        Location location = new Location();

        String bodyContent = mapper.writeValueAsString(location);
        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAddShouldReturn201Created() throws Exception {
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");
        location.setEnabled(true);

        Mockito.when(service.add(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", Is.is("NYC_USA")))
                .andExpect(header().string("Location","/v1/locations/NYC_USA"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testValidateRequestBodyLocationCode() throws Exception {
        Location location = new Location();
        location.setCode(null);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");
        location.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error[0]", Is.is("Location code cannot be null")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testListShouldReturn204NoContent() throws Exception {
        Mockito.when(service.list()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetShouldReturn405MethodNotAllowed() throws Exception {
        String requestURI = END_POINT_PATH + "/ABCDEF";

        mockMvc.perform(post(requestURI))
                .andExpect(status().isMethodNotAllowed())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetShouldReturn404NotFound() throws Exception {
        String requestURI = END_POINT_PATH + "/DELHI_IN";

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetShouldReturn200OK() throws Exception {
        String code = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + code;

        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");
        location.setEnabled(true);

        Mockito.when(service.get(code)).thenReturn(location);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code",Is.is(code)))
                .andExpect(jsonPath("$.city_name",Is.is("New York City")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPutShouldReturn404NotFound() throws LocationNotFoundException, Exception {
        String code = "ABC";

        Location location = new Location();
        location.setCode(code);
        location.setCityName("New Delhi");
        location.setRegionName("New York");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        Mockito.when(service.update(location)).thenThrow(new LocationNotFoundException("No location found"));

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPutShouldReturn400BadRequest() throws LocationNotFoundException, Exception {
        String code = "NYC_USA";

        Location location = new Location();
        location.setCode(code);
        location.setCityName("New Delhi");
        location.setRegionName("New York");
        location.setCountryCode(null);
        location.setCountryName(null);
        location.setEnabled(true);

        Mockito.when(service.update(location)).thenThrow(new LocationNotFoundException("No location found"));

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPutShouldReturn200Ok() throws LocationNotFoundException, Exception {
        String code = "DELHI_IN";

        Location location = new Location();
        location.setCode(code);
        location.setCityName("New Delhi");
        location.setRegionName("New York");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        Mockito.when(service.update(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDeleteShouldReturn404NotFound() throws LocationNotFoundException, Exception {
        String code = "ABC";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doThrow(LocationNotFoundException.class).when(service).delete(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void testDeleteShouldReturn204NoContent() throws Exception, LocationNotFoundException {
        String code = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doNothing().when(service).delete(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print());

    }

}
