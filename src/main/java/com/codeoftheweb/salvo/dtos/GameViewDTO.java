package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.utility.GameState;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class GameViewDTO {
    private long id;
    private Date created;
    private GameState gameState;
    private Set<GamePlayerDTO> gamePlayers = new HashSet<>();
    private Set<ShipDTO> ships = new HashSet<>();
    private Set<SalvoDTO> salvoes = new HashSet<>();
    private HitsDTO hits;

    public GameViewDTO() {
    }

    public GameViewDTO(long id, Date created, GameState gameState, Set<GamePlayerDTO> gamePlayers, Set<ShipDTO> ships, Set<SalvoDTO> salvoes, HitsDTO hits) {
        this.id = id;
        this.created = created;
        this.gameState = gameState;
        this.gamePlayers = gamePlayers;
        this.ships = ships;
        this.salvoes = salvoes;
        this.hits = hits;
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

    public Set<GamePlayerDTO> getGamePlayers() {
        return gamePlayers;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
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
