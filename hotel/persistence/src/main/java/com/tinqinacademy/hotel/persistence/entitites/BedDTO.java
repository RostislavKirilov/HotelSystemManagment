package com.tinqinacademy.hotel.persistence.entitites;
import java.util.UUID;


public class BedDTO {
    private UUID id;
    private String bedType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }
}
