package com.codeoftheweb.salvo.dtos;

import java.util.ArrayList;

public class HitsDTO {
    private ArrayList self = new ArrayList<>();
    private ArrayList opponent = new ArrayList<>();

    public HitsDTO(){
        this.self = new ArrayList<>();
        this.opponent = new ArrayList<>();
    }

    public ArrayList getSelf() {
        return self;
    }

    public void setSelf(ArrayList self) {
        this.self = self;
    }

    public ArrayList getOpponent() {
        return opponent;
    }

    public void setOpponent(ArrayList opponent) {
        this.opponent = opponent;
    }
}
