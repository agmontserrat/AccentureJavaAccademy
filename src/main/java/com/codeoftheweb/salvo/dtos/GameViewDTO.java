package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.GamePlayer;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class GameViewDTO {
    private long id;
    private Date created;
    private String gameState;
    private Set<GamePlayerDTO> gamePlayers = new HashSet<>();
    private Set<ShipDTO> ships = new HashSet<>();
    private Set<SalvoDTO> salvoes = new HashSet<>();
    private HitsDTO hits;

    public GameViewDTO(GamePlayer gamePlayer) {
        this.id = gamePlayer.getGame().getId();
        this.created = gamePlayer.getGame().getCreationDate();
        this.gameState = "PLACESHIPS";
        this.gamePlayers = gamePlayer.getGame().getGamePlayers().stream().map(GamePlayerDTO::new).collect(toSet());
        this.ships = gamePlayer.getShips().stream().map(ShipDTO::new).collect(toSet());
        this.salvoes = gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(SalvoDTO::new)).collect(toSet());
        this.hits = new HitsDTO();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public Set<GamePlayerDTO> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayerDTO> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<ShipDTO> getShips() {
        return ships;
    }

    public void setShips(Set<ShipDTO> ships) {
        this.ships = ships;
    }

    public Set<SalvoDTO> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<SalvoDTO> salvoes) {
        this.salvoes = salvoes;
    }

    public HitsDTO getHits() {
        return hits;
    }

    public void setHits(HitsDTO hits) {
        this.hits = hits;
    }
}
