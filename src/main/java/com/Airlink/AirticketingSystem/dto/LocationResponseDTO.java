package com.Airlink.AirticketingSystem.dto;

import com.Airlink.AirticketingSystem.model.enums.LocationType;

public class LocationResponseDTO {
    private Long id;
    private String name;
    private String code;
    private LocationType type;
    private Long parentId;
    private String parentName;
    
    // Hierarchy fields
    private String provinceName;
    private String districtName;
    private String sectorName;
    private String cellName;
    private String villageName;
    private String fullHierarchy;

    public LocationResponseDTO() {}

    public LocationResponseDTO(Long id, String name, String code, LocationType type) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocationType getType() { return type; }
    public void setType(LocationType type) { this.type = type; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    // Hierarchy getters and setters
    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }

    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }

    public String getSectorName() { return sectorName; }
    public void setSectorName(String sectorName) { this.sectorName = sectorName; }

    public String getCellName() { return cellName; }
    public void setCellName(String cellName) { this.cellName = cellName; }

    public String getVillageName() { return villageName; }
    public void setVillageName(String villageName) { this.villageName = villageName; }

    public String getFullHierarchy() { return fullHierarchy; }
    public void setFullHierarchy(String fullHierarchy) { this.fullHierarchy = fullHierarchy; }
}

