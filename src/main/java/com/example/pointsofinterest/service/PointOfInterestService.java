package com.example.pointsofinterest.service;

import com.example.pointsofinterest.dto.PointOfInterestCreateDto;
import com.example.pointsofinterest.mapper.PointOfInterestMapper;
import com.example.pointsofinterest.model.PointOfInterest;
import com.example.pointsofinterest.repository.PointOfInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PointOfInterestService {
    private final PointOfInterestRepository repository;
    
    @Autowired
    private PointOfInterestMapper mapper;

    @Autowired
    public PointOfInterestService(PointOfInterestRepository repository) {
        this.repository = repository;
    }

    public List<PointOfInterest> findNearbyPointsOfInterest(
        double latitude, 
        double longitude, 
        double range
    ) {
        // Default range to 10 km if not specified
        range = range > 0 ? range : 10;
        return repository.findNearbyPointsOfInterest(latitude, longitude, range);
    }

    public PointOfInterest createPointOfInterest(PointOfInterestCreateDto poi) {
        PointOfInterest newPoi= mapper.toEntity(poi);
        return repository.save(newPoi);
    }

    public Optional<PointOfInterest> findPointOfInterestById(String id) {
        return repository.findById(id);
    }

    public Optional<PointOfInterest> updatePointOfInterest(
        String id, 
        PointOfInterest updatedPoi
    ) {
        return repository.findById(id)
            .map(existingPoi -> {
                existingPoi.setName(updatedPoi.getName());
                existingPoi.setLatitude(updatedPoi.getLatitude());
                existingPoi.setLongitude(updatedPoi.getLongitude());
                existingPoi.setRating(updatedPoi.getRating());
                existingPoi.setThumbnail(updatedPoi.getThumbnail());
                return repository.save(existingPoi);
            });
    }

    public boolean deletePointOfInterest(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}