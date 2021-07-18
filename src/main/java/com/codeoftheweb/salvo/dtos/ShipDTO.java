package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Ship;

import java.util.List;

public class ShipDTO {
    private String type;
    private List<String> locations;

    public ShipDTO(Ship ship) {
        this.type = ship.getType();
        this.locations = ship.getShipLocations();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> shipLocations) {
        this.locations = shipLocations;
    }

}
