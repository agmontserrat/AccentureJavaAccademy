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



}
