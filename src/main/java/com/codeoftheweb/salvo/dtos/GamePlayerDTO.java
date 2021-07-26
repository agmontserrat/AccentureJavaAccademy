package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.*;


public class GamePlayerDTO {
    private long id;
    private PlayerDTO player;

    public GamePlayerDTO(GamePlayer gamePlayer){
        this.id = gamePlayer.getId();
        this.player = new PlayerDTO(gamePlayer.getPlayer());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }


}
