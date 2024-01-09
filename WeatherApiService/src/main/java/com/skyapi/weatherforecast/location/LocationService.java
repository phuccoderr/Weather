package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocationService {
    @Autowired private LocationRepository repo;

    public LocationService(LocationRepository repo) {
        this.repo = repo;
    }

    public Location add(Location location) {
        return repo.save(location);
    }

    public List<Location> list() {
        return repo.findUnTrashed();
    }

    public Location get(String code) {
        return repo.findByCode(code);
    }

    public Location update(Location locationRequest) throws LocationNotFoundException {
        String code = locationRequest.getCode();

        Location locationInDB = repo.findByCode(code);

        if (locationInDB == null) {
            throw new LocationNotFoundException("No location found with the given code: " + code);
        }

        locationInDB.setCityName(locationRequest.getCityName());
        locationInDB.setRegionName(locationRequest.getRegionName());
        locationInDB.setCountryCode(locationRequest.getCountryCode());
        locationInDB.setCountryName(locationRequest.getCountryName());
        locationInDB.setEnabled(locationRequest.isEnabled());

        return repo.save(locationInDB);
    }

    public void delete(String code) throws LocationNotFoundException {
        Location location = repo.findById(code).get();
        if (location == null) {
            throw new LocationNotFoundException("No location found with the given code: " + code);
        }
        repo.trashByCode(code);
    }
}
