package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toSet;

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
    private ShipRepository shipRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/games")
    public Map<String, Object> makeLoggedPlayer(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();

        Player authenticatedPlayer = getPlayer(authentication);
        if (authenticatedPlayer == null)
            dto.put("player", "Guest");
        else
            dto.put("player", playerDTO(authenticatedPlayer));
        dto.put("games", g_repo.findAll().stream().map(this::gameDTO).collect(toSet()));
        return dto;
    }

    //
    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> register(@RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty())
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);

        if (playerRepository.findByUserName(email) != null)
            return new ResponseEntity<>(makeMap("error","El email se encuentra ocupado."), HttpStatus.FORBIDDEN);

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    //---------------NEW GAME
    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        Player authenticatedPlayer = getPlayer(authentication);

        if (authenticatedPlayer == null){
            return new ResponseEntity<>(makeMap("error","No logged player"), HttpStatus.UNAUTHORIZED);
        }

        Game game = new Game();
        GamePlayer gamePlayer = new GamePlayer(new Date(),game, authenticatedPlayer);

        g_repo.save(game);
        gp_repo.save(gamePlayer);
        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    //-------- JOIN GAME

    @PostMapping("/game/{gameID}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameID, Authentication authentication) {
        //if user exists
        Player authenticatedPlayer = getPlayer(authentication);
        if (authenticatedPlayer == null){
            return new ResponseEntity<>(makeMap("error","No logged player"), HttpStatus.UNAUTHORIZED);
        }

        // if game exists
        Game game = g_repo.findById(gameID).orElse(null);
        if (game == null) {
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }

        // if game is full
        if (game.getGamePlayers().size() > 1){
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }

        Date now = new Date();
        GamePlayer gamePlayer = new GamePlayer(now, game, authenticatedPlayer);
        gp_repo.save(gamePlayer);
        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    //---------------Game View DTO-----------------
    @RequestMapping("/game_view/{id}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable long id, Authentication authentication) {
        Player authenticatedPlayer = getPlayer(authentication);
        GamePlayer gamePlayer = gp_repo.findById(id).orElse(null);
        Game game = gamePlayer.getGame();

        if (authenticatedPlayer == null){
            return new ResponseEntity<>(makeMap("error","No logged player"), HttpStatus.FORBIDDEN);
        }

        if (gamePlayer.getPlayer().getId() != authenticatedPlayer.getId()){
            return new ResponseEntity<>(makeMap("error","Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(gameViewDTO(gamePlayer), HttpStatus.OK);
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication){
        Player authenticatedPlayer = getPlayer(authentication);
        //GamePlayer gamePlayer = gp_repo.findById(gamePlayerId).orElse(null);
        Optional<GamePlayer> gamePlayer = gp_repo.findById(gamePlayerId);

        if (authenticatedPlayer == null){
            return new ResponseEntity<>(makeMap("error","No logged player"), HttpStatus.UNAUTHORIZED);
        }

        if(!gamePlayer.isPresent() || gamePlayer.get().getPlayer().getId() != authenticatedPlayer.getId()){
            return new ResponseEntity<>(makeMap("error","Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        else {
            System.out.println(ships.toString());
            if (gamePlayer.get().getShips().isEmpty()){
                ships.stream().forEach(ship -> {
                    ship.setGamePlayer(gamePlayer.get());
                    shipRepository.save(ship);});
                return new ResponseEntity<>(makeMap("OK", "Created"), HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>(makeMap("error","User already has ships placed."), HttpStatus.FORBIDDEN);
            }
        }

    }

    private Player getPlayer(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return null;
        }
        else {
            return (playerRepository.findByUserName(authentication.getName()));
        }
    }

    // ------------ GAME DTO
    private Map<String, Object> gameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::gamePlayerDTO).collect(toSet()));
        dto.put("scores", game.getGamePlayers().stream().map(this::scoresDTO).collect(toSet()));
        return dto;
    }

    //------------- GAMEPLAYER DTO
    private Map<String, Object> gamePlayerDTO(GamePlayer gp) {
        Map<String, Object> str = new LinkedHashMap<String, Object>();
        str.put("id", gp.getId());
        str.put("player", playerDTO(gp.getPlayer()));
        return str;
    }

    // ------------ PLAYER DTO
    private Map<String, Object> playerDTO(Player player) {
        Map<String, Object> str = new LinkedHashMap<String, Object>();
        str.put("id", player.getId());
        str.put("email", player.getUserName());
        return str;
    }

    private Map<String, Object> shipsDTO(Ship ship) {
        Map<String, Object> str = new LinkedHashMap<String, Object>();
        str.put("type", ship.getType());
        str.put("locations", ship.getShipLocations());
        return str;
    }

    private Map<String, Object> salvoesDTO(Salvo salvo) {
        Map<String, Object> str = new LinkedHashMap<String, Object>();
        str.put("turn", salvo.getTurn());
        str.put("player", salvo.getGamePlayer().getPlayer().getId());
        str.put("locations", salvo.getLocations());
        return str;
    }

    private Map<String, Object> scoresDTO(GamePlayer gp) {
        Map<String, Object> str = new LinkedHashMap<String, Object>();
        str.put("player", gp.getPlayer().getId());
        if (gp.getPlayer().getScore(gp.getGame()).isPresent()) {
            str.put("score", gp.getPlayer().getScore(gp.getGame()).get().getScore());
            str.put("finishDate", gp.getPlayer().getScore(gp.getGame()).get().getFinishDate());
        } else {
            str.put("score", "This game doesn't have a score yet");
        }
        return str;
    }

    private Map<String,Object> hitsDTO (GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("self", new ArrayList<>());
        dto.put("opponent", new ArrayList<>());
        return dto;
    }

    public Map<String, Object> gameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        //dto = gameDTO(gamePlayer.getGame());
        Game game = gamePlayer.getGame();

        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::gamePlayerDTO).collect(toSet()));


        dto.put("ships", gamePlayer.getShips().stream().map(this::shipsDTO).collect(toSet()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(this::salvoesDTO)).collect(toSet()));
        dto.put("hits", hitsDTO(gamePlayer));
        return dto;
    }


}