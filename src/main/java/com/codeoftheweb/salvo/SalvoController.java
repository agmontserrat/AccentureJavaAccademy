package com.codeoftheweb.salvo;

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
    private PasswordEncoder passwordEncoder;

    @GetMapping("/games")
    public Map<String, Object> makeLoggedPlayer(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();

        Player authenticatedPlayer = getPlayer(authentication);
        if (authenticatedPlayer == null)
            dto.put("player", "Guest");
        else
            dto.put("player", authenticatedPlayerDTO(authenticatedPlayer));
        dto.put("games", getGames());
        return dto;
    }

    private Map<String, Object> authenticatedPlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    //----------GET GAMES
    public Set<Map<String, Object>> getGames() {
        return g_repo.findAll().stream().map(this::gameDTO).collect(toSet());
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
        str.put("locations", ship.getLocations());
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

    //---------------Game View DTO-----------------
    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> gameView(@PathVariable Long gamePlayerId) {
        GamePlayer gamePlayer = gp_repo.findById(gamePlayerId).get();
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto = gameDTO(gamePlayer.getGame());
        dto.put("ships", gamePlayer.getShips().stream().map(this::shipsDTO).collect(toSet()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(this::salvoesDTO)).collect(toSet()));
        return dto;
    }

    //
    @RequestMapping(path = "/players", method = RequestMethod.POST)
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
}