package com.example.pointsofinterest.repository;

import com.example.pointsofinterest.model.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, String> {
    @Query("SELECT p FROM PointOfInterest p " +
           "WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * " +
           "cos(radians(p.longitude) - radians(:lon)) + " +
           "sin(radians(:lat)) * sin(radians(p.latitude)))) <= :range")
    List<PointOfInterest> findNearbyPointsOfInterest(
        @Param("lat") double latitude, 
        @Param("lon") double longitude, 
        @Param("range") double range
    );
}