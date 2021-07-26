package com.codeoftheweb.salvo.services.implementations;

import com.codeoftheweb.salvo.dtos.*;
import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.services.GameService;
import com.codeoftheweb.salvo.utility.GameState;
import com.codeoftheweb.salvo.utility.Util;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
public class GameServiceImpl implements GameService {

    @Override
    public GameState getGameState(GamePlayer gamePlayer){
        if (gamePlayer.getShips().isEmpty()){
            return GameState.PLACESHIPS;
        }

        if (gamePlayer.getGame().getGamePlayers().size() == 1){
            return GameState.WAITINGFOROPP;
        }

        else if (gamePlayer.getGame().getGamePlayers().size() == 2){
            GamePlayer opponent = Util.getOpponent(gamePlayer).get();

            if (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size() && allSunk(gamePlayer, opponent)  && !allSunk(opponent, gamePlayer)){
                return GameState.WON;
            }
            if (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size() && allSunk(opponent, gamePlayer) && !allSunk(gamePlayer, opponent)){
                return GameState.LOST;
            }
            if (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size() && allSunk(opponent, gamePlayer) && allSunk(gamePlayer, opponent)){
                return GameState.TIE;
            }
            if (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size() && gamePlayer.getId() > opponent.getId()){
                return GameState.PLAY;
            }
            else if (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size() && gamePlayer.getId() < opponent.getId()){
                return GameState.WAIT;
            }
            if (gamePlayer.getSalvoes().size() < opponent.getSalvoes().size()){
                return GameState.PLAY;
            }
            if (gamePlayer.getSalvoes().size() > opponent.getSalvoes().size()){
                return GameState.WAIT;
            }

        }
        return GameState.UNDEFINED;
    }

    @Override
    public GameViewDTO gameViewDTO(GamePlayer gamePlayer) {
        HitsDTO hitsDTO = new HitsDTO();
        if (Util.getOpponent(gamePlayer).isPresent()) {
            hitsDTO.setSelf(Util.getOpponent(gamePlayer).get().getSalvoes().stream().map(this::getHits).collect(Collectors.toList()));
            hitsDTO.setOpponent(gamePlayer.getSalvoes().stream().map(this::getHits).collect(Collectors.toList()));
        }
        else {
            hitsDTO.setSelf(new ArrayList<>());
            hitsDTO.setOpponent(new ArrayList<>());
        }



        GameViewDTO gameViewDTO = new GameViewDTO();
        gameViewDTO.setId(gamePlayer.getGame().getId());
        gameViewDTO.setCreated(gamePlayer.getGame().getCreationDate());
        gameViewDTO.setGameState(getGameState(gamePlayer));
        gameViewDTO.setGamePlayers(gamePlayer.getGame().getGamePlayers().stream().map(GamePlayerDTO::new).collect(toSet()));
        gameViewDTO.setShips(gamePlayer.getShips().stream().map(ShipDTO::new).collect(toSet()));
        gameViewDTO.setSalvoes(gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(SalvoDTO::new)).collect(toSet()));
        gameViewDTO.setHits(hitsDTO);

        return gameViewDTO;

    }

    @Override
    public GameDTO gameDTO(Game game) {

        GameDTO gameDTO = new GameDTO();
        gameDTO.setId(game.getId());
        gameDTO.setCreated(game.getCreationDate());
        gameDTO.setGamePlayers(game.getGamePlayers().stream().map(GamePlayerDTO::new).collect(toSet()));
        gameDTO.setScores(game.getGamePlayers().stream().map(ScoreDTO::new).collect(Collectors.toSet()));

        return gameDTO;
    }

    public Map<String,Object> getDamages(Salvo salvo){
        if (Util.getOpponent(salvo.getGamePlayer()).isEmpty()){
            return null;
        }
        GamePlayer opponent = Util.getOpponent(salvo.getGamePlayer()).get();

        Map<String, Object> dto = new LinkedHashMap<>();

        List<String> carrierLocations = new ArrayList<>();
        List<String> battleshipLocations = new ArrayList<>();
        List<String> submarineLocations = new ArrayList<>();
        List<String> destroyerLocations = new ArrayList<>();
        List<String> patrolboatLocations = new ArrayList<>();


        for (Ship ship: opponent.getShips()) {
            switch (ship.getType()) {
                case "carrier":
                    carrierLocations = ship.getShipLocations();
                    break;
                case "battleship":
                    battleshipLocations = ship.getShipLocations();
                    break;
                case "submarine":
                    submarineLocations = ship.getShipLocations();
                    break;
                case "destroyer":
                    destroyerLocations = ship.getShipLocations();
                    break;
                case "patrolboat":
                    patrolboatLocations = ship.getShipLocations();
                    break;
            }
        }
        List<String> allSalvoes = salvo.getGamePlayer().getSalvoes().stream().filter(s -> s.getTurn() <= salvo.getTurn()).flatMap(salvo1 -> salvo1.getSalvoLocations().stream()).collect(Collectors.toList());
        //List<String> allSalvoes = salvo.getGamePlayer().getSalvoes().stream().flatMap(s -> s.getSalvoLocations().stream()).collect(Collectors.toList());

        dto.put("carrierHits", carrierLocations.stream().filter(c -> salvo.getSalvoLocations().contains(c)).count());
        dto.put("battleshipHits", battleshipLocations.stream().filter(c -> salvo.getSalvoLocations().contains(c)).count());
        dto.put("submarineHits", submarineLocations.stream().filter(c -> salvo.getSalvoLocations().contains(c)).count());
        dto.put("destroyerHits", destroyerLocations.stream().filter(c -> salvo.getSalvoLocations().contains(c)).count());
        dto.put("patrolboatHits", patrolboatLocations.stream().filter(c -> salvo.getSalvoLocations().contains(c)).count());
        dto.put("carrier", carrierLocations.stream().filter(allSalvoes::contains).count());
        dto.put("battleship", battleshipLocations.stream().filter(allSalvoes::contains).count());
        dto.put("submarine", submarineLocations.stream().filter(allSalvoes::contains).count());
        dto.put("destroyer", destroyerLocations.stream().filter(allSalvoes::contains).count());
        dto.put("patrolboat", patrolboatLocations.stream().filter(allSalvoes::contains).count());
        return dto;
    }

    public Map<String,Object> getHits(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurn());
        dto.put("hitLocations", getHitLocations(salvo));
        dto.put("damages", getDamages(salvo));
        dto.put("missed", salvo.getSalvoLocations().size() - getHitLocations(salvo).size());
        return dto;
    }

    public List<String> getHitLocations(Salvo salvo){
        if (Util.getOpponent(salvo.getGamePlayer()).isEmpty()) {
            return null;
        }
        List<String> ships = Util.getOpponent(salvo.getGamePlayer()).get().getShips().stream().flatMap(s -> s.getShipLocations().stream()).collect(Collectors.toList());
        return salvo.getSalvoLocations().stream().filter(ships::contains).collect(Collectors.toList());
    }

    public boolean allSunk(GamePlayer player, GamePlayer opponent){
        if (player.getSalvoes().isEmpty() && opponent.getShips().size() < 5) {
            return false;
        }
        List<String> ships = opponent.getShips().stream().flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList());
        List<String> salvoes = player.getSalvoes().stream().flatMap(s -> s.getSalvoLocations().stream()).collect(Collectors.toList());
        return (salvoes.containsAll(ships));
    }
}
