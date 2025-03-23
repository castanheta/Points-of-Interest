package com.example.pointsofinterest.mapper;

import com.example.pointsofinterest.dto.PointOfInterestCreateDto;
import com.example.pointsofinterest.model.PointOfInterest;
import org.springframework.stereotype.Component;

@Component
public class PointOfInterestMapper {
    public PointOfInterest toEntity(PointOfInterestCreateDto dto) {
        PointOfInterest poi = new PointOfInterest();
        poi.setName(dto.getName());
        poi.setLatitude(dto.getLatitude());
        poi.setLongitude(dto.getLongitude());
        poi.setMetadata(dto.getMetadata());
        poi.setRating(dto.getRating());
        poi.setThumbnail(dto.getThumbnail());
        return poi;
    }

    public PointOfInterestCreateDto toDto(PointOfInterest entity) {
        PointOfInterestCreateDto dto = new PointOfInterestCreateDto();
        dto.setName(entity.getName());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setMetadata(entity.getMetadata());
        dto.setRating(entity.getRating());
        dto.setThumbnail(entity.getThumbnail());
        return dto;
    }
}