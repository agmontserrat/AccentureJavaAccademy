package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String type;

    @ElementCollection
    @Column(name="shipLocations")
    private List<String> shipLocations;

    @ManyToOne
    @JoinColumn(name = "gamePlayer_id")
    public GamePlayer gamePlayer;

    public Ship() {
    }

    public Ship(String type, List<String> locations, GamePlayer gamePlayer) {
        this.type = type;
        this.shipLocations = locations;
        this.gamePlayer = gamePlayer;
    }

    public String getType() {
        return type;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setShipLocations(List<String> locations) {
        this.shipLocations = locations;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
}
