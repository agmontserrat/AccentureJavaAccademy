package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Score;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerDTO {
    private long id;
    private String userName;
    private String password;
    private Set<GamePlayerDTO> gamePlayers = new HashSet<>();
    private Set<ScoreDTO> scores = new HashSet<>();

    public PlayerDTO(Player player){
        this.id = player.getId();
        this.userName = player.getUserName();
        this.password = player.getPassword();
        this.gamePlayers = player.getGamePlayers().stream().map(GamePlayerDTO::new).collect(Collectors.toSet());
        this.scores = player.getScore().stream().map(ScoreDTO::new).collect(Collectors.toSet());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }
}
