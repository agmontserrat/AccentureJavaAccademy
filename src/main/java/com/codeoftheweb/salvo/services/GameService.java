package com.codeoftheweb.salvo.services;

import com.codeoftheweb.salvo.dtos.GameDTO;
import com.codeoftheweb.salvo.dtos.GameViewDTO;
import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.utility.GameState;

public interface GameService {
    //GameViewDTO GameViewDTO(GamePlayer gamePlayer);
    GameState getGameState(GamePlayer gamePlayer);
    GameViewDTO gameViewDTO(GamePlayer gamePlayer);
    GameDTO gameDTO(Game game);
    boolean allSunk(GamePlayer gamePlayer, GamePlayer opponent);
}
