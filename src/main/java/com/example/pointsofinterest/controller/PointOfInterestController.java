package com.example.pointsofinterest.controller;

import com.example.pointsofinterest.dto.PointOfInterestCreateDto;
import com.example.pointsofinterest.model.PointOfInterest;
import com.example.pointsofinterest.service.PointOfInterestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/points-of-interest")
public class PointOfInterestController {
    private final PointOfInterestService service;

    @Autowired
    public PointOfInterestController(PointOfInterestService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PointOfInterest>> findNearbyPointsOfInterest(
        @RequestParam double lat,
        @RequestParam double lon,
        @RequestParam(required = false, defaultValue = "10") double range
    ) {
        List<PointOfInterest> pois = service.findNearbyPointsOfInterest(lat, lon, range);
        return ResponseEntity.ok(pois);
    }

    @PostMapping
    public ResponseEntity<PointOfInterest> createPointOfInterest(
        @Valid @RequestBody PointOfInterestCreateDto poi
    ) {
        PointOfInterest createdPoi = service.createPointOfInterest(poi);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPoi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointOfInterest> getPointOfInterestById(@PathVariable String id) {
        return service.findPointOfInterestById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointOfInterest> updatePointOfInterest(
        @PathVariable String id,
        @Valid @RequestBody PointOfInterest poi
    ) {
        return service.updatePointOfInterest(id, poi)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePointOfInterest(@PathVariable String id) {
        boolean deleted = service.deletePointOfInterest(id);
        return deleted 
            ? ResponseEntity.noContent().build() 
            : ResponseEntity.notFound().build();
    }
}