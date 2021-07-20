package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Salvo;

import java.util.ArrayList;
import java.util.List;

public class SalvoDTO {
    private Integer turn;
    private long player;
    private List<String> locations = new ArrayList<>();

    public SalvoDTO(Salvo salvo){
        this.turn = salvo.getTurn();
        this.player = salvo.getGamePlayer().getPlayer().getId();
        this.locations = salvo.getSalvoLocations();
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public long getPlayer() {
        return player;
    }

    public void setPlayer(long player) {
        this.player = player;
    }
}
