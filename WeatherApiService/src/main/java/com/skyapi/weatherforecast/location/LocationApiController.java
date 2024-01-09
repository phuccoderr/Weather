package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

    private LocationService service;
    @Autowired private ModelMapper modelMapper;

    public LocationApiController(LocationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> addLocation(@RequestBody @Valid Location location) {
        Location addedLocation = service.add(location);
        URI uri = URI.create("/v1/locations/" + addedLocation.getCode());

        return ResponseEntity.created(uri).body(entity2DTO(addedLocation));
    }

    @GetMapping
    public ResponseEntity<?> listLocation() {
        List<Location> locations = service.list();

        if (locations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lístEntity2listDTO(locations));
    }

    private List<LocationDTO> lístEntity2listDTO(List<Location> listLocations) {
        List<LocationDTO> listDTOLocation = new ArrayList<>();

        listLocations.forEach(location -> {
            listDTOLocation.add(modelMapper.map(location, LocationDTO.class));
        });

        return listDTOLocation;
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getLocation(@PathVariable("code") String code) {
        Location location = service.get(code);
        if (location == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(location);
    }

    @PutMapping
    public ResponseEntity<?> updateLocation(@RequestBody @Valid Location location) {
        try {
            Location update = service.update(location);
            return ResponseEntity.ok(entity2DTO(update));
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

    private LocationDTO entity2DTO(Location update) {
        return modelMapper.map(update, LocationDTO.class);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteLocation(@PathVariable("code") String code) {
        try {
            service.delete(code);
            return ResponseEntity.noContent().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
