package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository g_repo;
    @Autowired
    private GamePlayerRepository gp_repo;

    @GetMapping("/games")
    public Map<String, Object> getGames() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("games",g_repo.findAll().stream().map(this::gameDTO).collect(toSet()));
        return dto;
    }

    private Map<String, Object> gameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::gamePlayerDTO).collect(toSet()));
        dto.put("scores", game.getGamePlayers().stream().map(this::scoresDTO).collect(toSet()));
        return dto;
    }

    private Map<String, Object> gamePlayerDTO(GamePlayer gp) {
        Map<String, Object> str = new LinkedHashMap<String, Object>();
        str.put("id", gp.getId());
        str.put("player", playerDTO(gp.getPlayer()));
        return str;
    }

    private Map<String,Object> playerDTO(Player player){
        Map<String, Object> str = new LinkedHashMap<String,Object>();
        str.put("id", player.getId());
        str.put("email", player.getUserName());
        return str;
    }

    private Map<String,Object> shipsDTO(Ship ship){
        Map<String, Object> str = new LinkedHashMap<String,Object>();
        str.put("type", ship.getType());
        str.put("locations", ship.getLocations());
        return str;
    }

    private Map<String,Object> salvoesDTO(Salvo salvo){
        Map<String, Object> str = new LinkedHashMap<String,Object>();
        str.put("turn", salvo.getTurn());
        str.put("player", salvo.getGamePlayer().getPlayer().getId());
        str.put("locations",salvo.getLocations());
        return str;
    }

    private Map<String,Object> scoresDTO(GamePlayer gp){
        Map<String, Object> str = new LinkedHashMap<String,Object>();
        str.put("player", gp.getPlayer().getId());
        if (gp.getPlayer().getScore(gp.getGame()).isPresent()){
            str.put("score", gp.getPlayer().getScore(gp.getGame()).get().getScore());
            str.put("finishDate", gp.getPlayer().getScore(gp.getGame()).get().getFinishDate());
        }
        else{
            str.put("score", "This game doesn't have a score yet");
        }
        return str;
    }


    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> gameView(@PathVariable Long gamePlayerId){
        GamePlayer gamePlayer = gp_repo.findById(gamePlayerId).get();
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto = gameDTO(gamePlayer.getGame());
        dto.put("ships", gamePlayer.getShips().stream().map(this::shipsDTO).collect(toSet()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(this::salvoesDTO)).collect(toSet()));
        return dto;
    }
}