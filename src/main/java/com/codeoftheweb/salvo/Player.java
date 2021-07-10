package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    private String password;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "player")
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "player")
    private Set<Score> scores = new HashSet<>();

    public Player() { }

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @JsonIgnore
    public List<Game> getGames(){
        return gamePlayers.stream().map(g -> g.getGame()).collect(Collectors.toList());
    }

    public long getId() {
        return id;
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

    public Set<Score> getScore(){
        return this.scores;
    }

    public Optional<Score> getScore(Game game) {
        return this.scores.stream().filter(s -> s.getGame().getId()==game.getId()).findFirst();
    }
}