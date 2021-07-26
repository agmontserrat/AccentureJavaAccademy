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

    public HitsDTO() {

    }
    public HitsDTO(List<Map<String, Object>> self, List<Map<String, Object>> opponent) {
        this.self = self;
        this.opponent = opponent;
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
