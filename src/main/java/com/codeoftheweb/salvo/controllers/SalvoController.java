package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.dtos.GameViewDTO;
import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import com.codeoftheweb.salvo.utility.GameState;
import com.codeoftheweb.salvo.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository g_repo;
    @Autowired
    private GamePlayerRepository gp_repo;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SalvoRepository salvoRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //-----------------PLAYERS
    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> register(@RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty())
            return new ResponseEntity<>(Util.makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);

        if (playerRepository.findByUserName(email) != null)
            return new ResponseEntity<>(Util.makeMap("error","El email se encuentra ocupado."), HttpStatus.FORBIDDEN);

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //---------------Game View DTO-----------------
    @RequestMapping("/game_view/{id}")
    public ResponseEntity<?> getGameView(@PathVariable long id, Authentication authentication) {
        Optional<GamePlayer> gamePlayer = gp_repo.findById(id);

        if (Util.isGuest(authentication)){
            return new ResponseEntity<>(Util.makeMap("error","No logged player"), HttpStatus.FORBIDDEN);
        }
        Player authenticatedPlayer = playerRepository.findByUserName(authentication.getName());

        if (gamePlayer.isPresent()) {
            if (gamePlayer.get().getPlayer().getId() != authenticatedPlayer.getId()) {
                return new ResponseEntity<>(Util.makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
            }
        }
        else {
            return new ResponseEntity<>(Util.makeMap("error", "Bad request"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new GameViewDTO(gamePlayer.get()), HttpStatus.OK);
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication){
        Optional<GamePlayer> gamePlayer = gp_repo.findById(gamePlayerId);

        if (Util.isGuest(authentication)){ //If user is authenticated or not
            return new ResponseEntity<>(Util.makeMap("error","No logged player"), HttpStatus.UNAUTHORIZED);
        }
        Player authenticatedPlayer = playerRepository.findByUserName(authentication.getName());

        //If the authenticated player is playing the gamePlayer requested
        if(!gamePlayer.isPresent() || gamePlayer.get().getPlayer().getId() != authenticatedPlayer.getId()){
            return new ResponseEntity<>(Util.makeMap("error","Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        //If player has placed ships
        if (gamePlayer.get().getShips().size()==5){
            return new ResponseEntity<>(Util.makeMap("error","Ships already placed"), HttpStatus.FORBIDDEN);
        }

        //If there are five ships
        if (ships.size() != 5) {
            return new ResponseEntity<>(Util.makeMap("error", "The request does not contains 5 ships."), HttpStatus.FORBIDDEN);
        }

        //Save the ships
        ships.stream().forEach(ship -> {
            ship.setGamePlayer(gamePlayer.get());
            shipRepository.save(ship);});
        return new ResponseEntity<>(Util.makeMap("OK", "Created"), HttpStatus.CREATED);


    }

    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> addSalvo(@PathVariable long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication){
        // GAME AND GAME PLAYER CHECKS
        Optional<GamePlayer> gamePlayer = gp_repo.findById(gamePlayerId);
        if (Util.isGuest(authentication)){
            return new ResponseEntity<>(Util.makeMap("error","No logged player"), HttpStatus.UNAUTHORIZED);
        }
        Player authenticatedPlayer = playerRepository.findByUserName(authentication.getName());
        if(!gamePlayer.isPresent()){
            return new ResponseEntity<>(Util.makeMap("error", "The gameplayer does not exist"), HttpStatus.UNAUTHORIZED);
        }
        if(gamePlayer.get().getPlayer().getId() != authenticatedPlayer.getId()){
            return new ResponseEntity<>(Util.makeMap("error", "The gameplayer does not match with the player"), HttpStatus.UNAUTHORIZED);
        }

        // SALVO CHECKS
        if (salvo.getSalvoLocations().size() == 0){ // Al least 1 location.
            return new ResponseEntity<>(Util.makeMap("error", "Salvo needs at least 1 location"), HttpStatus.UNAUTHORIZED);
        }

        Optional<GamePlayer> opponent = gamePlayer.get().getGame().getGamePlayers().stream().filter(gp -> gp.getId() != gamePlayer.get().getId()).findFirst();

        if (opponent.isEmpty() || gamePlayer.get().getSalvoes().size() > opponent.get().getSalvoes().size()){ //The game needs two players to start.
            return new ResponseEntity<>(Util.makeMap("error", "You have to wait for your opponent"), HttpStatus.FORBIDDEN);
        }

        if (Util.getGameState(gamePlayer.get()) == GameState.PLAY) {
            salvo.setTurn(gamePlayer.get().getSalvoes().size() + 1);
            salvo.setGamePlayer(gamePlayer.get());
            salvoRepository.save(salvo);
            gamePlayer.get().getSalvoes().add(salvo);

            if (Util.getGameState(gamePlayer.get()) == GameState.WON){
                scoreRepository.save(new Score(gamePlayer.get().getGame(), gamePlayer.get().getPlayer(), 1,new Date()));
                scoreRepository.save(new Score(opponent.get().getGame(), opponent.get().getPlayer(), 0,new Date()));
            }
            if(Util.getGameState(gamePlayer.get()) == GameState.LOST){
                scoreRepository.save(new Score(gamePlayer.get().getGame(), gamePlayer.get().getPlayer(), 0,new Date()));
                scoreRepository.save(new Score(opponent.get().getGame(), opponent.get().getPlayer(), 1,new Date()));
            }
            if (Util.getGameState(gamePlayer.get()) == GameState.TIE){
                scoreRepository.save(new Score(gamePlayer.get().getGame(), gamePlayer.get().getPlayer(), 0.5,new Date()));
                scoreRepository.save(new Score(opponent.get().getGame(), opponent.get().getPlayer(), 0.5,new Date()));
            }
            return new ResponseEntity<>(Util.makeMap("OK", "Salvoes fired"), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(Util.makeMap("error", "Error"), HttpStatus.FORBIDDEN);

    }
}