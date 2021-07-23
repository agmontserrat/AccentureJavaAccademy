package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.utility.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HitsDTO {
    private List<Map<String, Object>> self ;
    private List<Map<String, Object>> opponent;

    public HitsDTO(GamePlayer gamePlayer){
        if (Util.getOpponent(gamePlayer).isPresent())
            this.self = Util.getOpponent(gamePlayer).get().getSalvoes().stream().map(s -> Util.getHits(s)).collect(Collectors.toList());
        else
            this.self = new ArrayList<>();
        this.opponent = gamePlayer.getSalvoes().stream().map(s -> Util.getHits(s)).collect(Collectors.toList());

    }

    public List<Map<String, Object>> getSelf() {
        return self;
    }

    public void setSelf(List<Map<String, Object>> self) {
        this.self = self;
    }

    public List<Map<String, Object>> getOpponent() {
        return opponent;
    }

    public void setOpponent(List<Map<String, Object>> opponent) {
        this.opponent = opponent;
    }
}
