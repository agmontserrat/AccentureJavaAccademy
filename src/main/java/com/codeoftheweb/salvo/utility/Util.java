package com.codeoftheweb.salvo.utility;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Collectors;

public class Util {
    @Autowired
    private PlayerRepository playerRepository;

    public static Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static boolean isGuest(Authentication authentication) {
        return (authentication == null || authentication instanceof AnonymousAuthenticationToken);
    }

    public static Optional<GamePlayer> getOpponent(GamePlayer gamePlayer){
        return gamePlayer.getGame().getGamePlayers().stream().filter(gp -> gp.getId() != gamePlayer.getId()).findFirst();
    }

    public static Map<String,Object> getHits(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurn());
        dto.put("hitLocations", getHitLocations(salvo));
        dto.put("damages", getDamages(salvo));
        dto.put("missed", salvo.getSalvoLocations().size() - getHitLocations(salvo).size());
        return dto;
    }

    public static List<String> getHitLocations(Salvo salvo){
        if (getOpponent(salvo.getGamePlayer()).isEmpty()) {
            return null;
        }
        List<String> ships = getOpponent(salvo.getGamePlayer()).get().getShips().stream().flatMap(s -> s.getShipLocations().stream()).collect(Collectors.toList());
        return salvo.getSalvoLocations().stream().filter(s -> (ships.contains(s))).collect(Collectors.toList());
    }

    public static Map<String,Object> getDamages(Salvo salvo){
        if (getOpponent(salvo.getGamePlayer()).isEmpty()){
            return null;
        }
        GamePlayer opponent = getOpponent(salvo.getGamePlayer()).get();

        Map<String, Object> dto = new LinkedHashMap<>();

        List<String> carrierLocations = new ArrayList<>();
        List<String> battleshipLocations = new ArrayList<>();
        List<String> submarineLocations = new ArrayList<>();
        List<String> destroyerLocations = new ArrayList<>();
        List<String> patrolboatLocations = new ArrayList<>();


        for (Ship ship: getOpponent(salvo.getGamePlayer()).get().getShips()) {
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

    public static GameState getGameState(GamePlayer gamePlayer){
        if (gamePlayer.getShips().isEmpty()){
            return GameState.PLACESHIPS;
        }

        if (gamePlayer.getGame().getGamePlayers().size() == 1){
            return GameState.WAITINGFOROPP;
        }

        else if (gamePlayer.getGame().getGamePlayers().size() == 2){
            GamePlayer opponent = getOpponent(gamePlayer).get();

            if (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size() && allSunk(gamePlayer, opponent)){
                return GameState.WON;
            }
            if (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size() && allSunk(opponent, gamePlayer)){
                return GameState.LOST;
            }
            if (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size() && allSunk(opponent, gamePlayer) && allSunk(gamePlayer, opponent)){
                return GameState.TIE;
            }
            if (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size() && gamePlayer.getId() > opponent.getId()){
                return GameState.PLAY;
            }
            if (gamePlayer.getSalvoes().size() < opponent.getSalvoes().size()){
                return GameState.PLAY;
            }
            if (gamePlayer.getSalvoes().size() > opponent.getSalvoes().size()){
                return GameState.WAIT;
            }

        }
        return GameState.WAIT;
    }

    public static boolean allSunk(GamePlayer player, GamePlayer opponent){
        if (player.getSalvoes().isEmpty() && opponent.getShips().size() < 5) {
            return false;
        }
            List<String> ships = opponent.getShips().stream().flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList());
            List<String> salvoes = player.getSalvoes().stream().flatMap(s -> s.getSalvoLocations().stream()).collect(Collectors.toList());
            return (salvoes.containsAll(ships));
    }

}
