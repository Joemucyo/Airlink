package com.Airlink.AirticketingSystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Airlink.AirticketingSystem.model.Location;
import com.Airlink.AirticketingSystem.model.enums.LocationType;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByType(LocationType type);
    List<Location> findByParent(Location parent);
    List<Location> findByParent_Id(Long parentId);
    boolean existsByCode(String code);
    Page<Location> findAllByType(LocationType type, Pageable pageable);
    List<Location> findByCode(String code);
    long countByType(LocationType type);
    
    // Search methods
    List<Location> findByNameContainingIgnoreCase(String name);
    List<Location> findByNameAndType(String name, LocationType type);
    List<Location> findByCodeAndType(String code, LocationType type);
}
