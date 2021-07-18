package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GameDTO {
    private long id;
    private Date created;
    private Set<GamePlayerDTO> gamePlayers = new HashSet<>();
    private Set<ScoreDTO> scores = new HashSet<>();

    public GameDTO(Game game) {
        this.id = game.getId();
        this.created = game.getCreationDate();
        this.gamePlayers = game.getGamePlayers().stream().map(GamePlayerDTO::new).collect(Collectors.toSet());
        this.scores = game.getGamePlayers().stream().map(ScoreDTO::new).collect(Collectors.toSet());
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
