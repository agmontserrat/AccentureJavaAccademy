package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.dtos.GameDTO;
import com.codeoftheweb.salvo.dtos.PlayerDTO;
import com.codeoftheweb.salvo.utility.Util;
import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GameRepository g_repo;
    @Autowired
    private GamePlayerRepository gp_repo;
    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/games")
    public ResponseEntity<Map<String, Object>> getGames(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();
        if (Util.isGuest(authentication))
            dto.put("player", "Guest");
        else
            dto.put("player", new PlayerDTO(playerRepository.findByUserName(authentication.getName())));
        dto.put("games",g_repo.findAll().stream().map(GameDTO::new).collect(toSet()));
        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
    }

    //---------------NEW GAME
    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {

        if (Util.isGuest(authentication))
            return new ResponseEntity<>(Util.makeMap("error","No logged player"), HttpStatus.UNAUTHORIZED);

        Player authenticatedPlayer = playerRepository.findByUserName(authentication.getName());
        Game game = new Game();
        GamePlayer gamePlayer = new GamePlayer(new Date(),game, authenticatedPlayer);

        g_repo.save(game);
        gp_repo.save(gamePlayer);
        return new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    //-------- JOIN GAME
    @PostMapping("/game/{gameID}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameID, Authentication authentication) {
        //if user exists
        if (Util.isGuest(authentication)){
            return new ResponseEntity<>(Util.makeMap("error","No logged player"), HttpStatus.UNAUTHORIZED);
        }
        Player authenticatedPlayer = playerRepository.findByUserName(authentication.getName());

        // if game exists
        Game game = g_repo.findById(gameID).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(Util.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }

        // if game is full
        if (game.getGamePlayers().size() > 1){
            return new ResponseEntity<>(Util.makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }

        Date now = new Date();
        GamePlayer gamePlayer = new GamePlayer(now, game, authenticatedPlayer);
        gp_repo.save(gamePlayer);
        return new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

}
