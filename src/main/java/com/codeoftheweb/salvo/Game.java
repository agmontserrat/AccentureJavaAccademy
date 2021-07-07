package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date creationDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
    private Set<GamePlayer> gamePlayers = new HashSet<>();
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
    private Set<Score> scores = new HashSet<>();


    public Game(){
        this.creationDate = new Date();
    }
    public Game(Date creationDate) {
        this.creationDate = creationDate;
    }


    public List<Player> getPlayers() {
        return gamePlayers.stream().map(a -> a.getPlayer()).collect(toList());
    }


    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public long getId() {
        return id;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void AddGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }




}
