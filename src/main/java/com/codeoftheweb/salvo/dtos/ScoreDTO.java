package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Score;

import java.util.Date;
import java.util.Optional;

public class ScoreDTO {
    private Optional<Long> player;
    private Optional<Double> score;
    private Optional<Date> finishDate;

    public ScoreDTO() {
    }

    public ScoreDTO(GamePlayer gp){
        if (gp.getPlayer().getScore(gp.getGame()).isPresent()) {
            this.player = Optional.of(gp.getPlayer().getId());
            this.score = Optional.of(gp.getPlayer().getScore(gp.getGame()).get().getScore());
            this.finishDate = Optional.of(gp.getPlayer().getScore(gp.getGame()).get().getFinishDate());
        }
        else {
            this.player = null;
            this.score = null;
            this.finishDate = null;
        }
    }

    public Optional<Long> getPlayer() {
        return player;
    }

    public void setPlayer(Optional<Long> player) {
        this.player = player;
    }

    public Optional<Double> getScore() {
        return score;
    }

    public void setScore(Optional<Double> score) {
        this.score = score;
    }

    public Optional<Date> getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Optional<Date> finishDate) {
        this.finishDate = finishDate;
    }
}
