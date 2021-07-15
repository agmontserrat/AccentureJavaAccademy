package com.codeoftheweb.salvo.models;

import com.codeoftheweb.salvo.models.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date creationDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gamePlayer")
    private List<Ship> ships;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gamePlayer")
    private Set<Salvo> salvoes = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;


    public GamePlayer() {
    }
    public GamePlayer(Date creationDate, Game game, Player player){
        this.creationDate = creationDate;
        this.game = game;
        this.player = player;

    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Optional<Score> getScore(){
        return this.getPlayer().getScore(this.getGame());
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Player getPlayer() {
        return player;
    }


    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public void addSalvo(Salvo salvo) {
        salvoes.add(salvo);
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public Game getGame() {
        return game;
    }

    public long getId() {
        return id;
    }
}
