package com.codeoftheweb.salvo.dtos;

import java.util.Date;
import java.util.Set;

public class GameDTO {
    private long id;
    private Date created;
    private Set<GamePlayerDTO> gamePlayers;
    private Set<ScoreDTO> scores;

    public GameDTO(){

    }

    public GameDTO(long id, Date created, Set<GamePlayerDTO> gamePlayers, Set<ScoreDTO> scores) {
        this.id = id;
        this.created = created;
        this.gamePlayers = gamePlayers;
        this.scores = scores;
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

    public void setGamePlayers(Set<GamePlayerDTO> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<ScoreDTO> getScores() {
        return scores;
    }

    public void setScores(Set<ScoreDTO> scores) {
        this.scores = scores;
    }
}
