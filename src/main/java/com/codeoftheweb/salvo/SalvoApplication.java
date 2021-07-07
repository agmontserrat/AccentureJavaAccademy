package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {

		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(GameRepository g_repository, PlayerRepository p_repository,
									  GamePlayerRepository gp_repository, ShipRepository s_repository, SalvoRepository salvoRepository, ScoreRepository scRepository) {
		return (args) -> {
			Date date = new Date();

			// GAME
			Game game1 = new Game();
			Game game2 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
			Game game3 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			g_repository.save(game1);
			g_repository.save(game2);
			g_repository.save(game3);

			//PLAYER
			Player player1 = (new Player("j.bauer@ctu.gov"));
			Player player2 = new Player("c.obrian@ctu.gov");
			Player player3 = new Player("c.kim_bauer@gmail.com");
			Player player4 = new Player("t.almeida@ctu.gov");
			p_repository.save(player1);
			p_repository.save(player2);
			p_repository.save(player3);
			p_repository.save(player4);

			//GAME PLAYER
			GamePlayer gp1 = new GamePlayer(Date.from(date.toInstant().plusSeconds(3600)), game1, player1 );
			GamePlayer gp2 = new GamePlayer(new Date(), game1, player2);
			GamePlayer gp3 = new GamePlayer(new Date(), game2, player3);
			GamePlayer gp4 = new GamePlayer(new Date(), game2, player4);
			gp_repository.save(gp1);
			gp_repository.save(gp2);
			gp_repository.save(gp3);
			gp_repository.save(gp4);

			//SHIP
			Ship s1 = new Ship("Destroyer", List.of("H2", "H3", "H4"), gp1);
			Ship s2 = new Ship("Submarine", List.of("E1", "F1", "G1"), gp1);
			Ship s3 = new Ship("Patrol Boat", List.of("B4", "B5"), gp1);
			Ship s4 = new Ship("Destroyer", List.of("B5", "C5", "D5"), gp2);
			Ship s5 = new Ship("Patrol Boat", List.of("F1", "F2"), gp2);
			Ship s6 = new Ship("Destroyer", List.of("B5", "C5", "D5"), gp3);
			Ship s7 = new Ship("Patrol Boat", List.of("C6", "C7"), gp3);
			Ship s8 = new Ship("Submarine", List.of("A2", "A3", "A4"), gp4);
			Ship s9 = new Ship("Patrol Boat", List.of("G6", "H6"), gp3);
			s_repository.save(s1);
			s_repository.save(s2);
			s_repository.save(s3);
			s_repository.save(s4);
			s_repository.save(s5);
			s_repository.save(s6);

			//SALVOES
			Salvo salvo1 = new Salvo(1, gp1, List.of("B5", "C5", "F1"));
			Salvo salvo2 = new Salvo(1, gp2, List.of("B4", "B5", "B6"));
			Salvo salvo3 = new Salvo(2, gp1, List.of("F2", "D5"));
			Salvo salvo4 = new Salvo(2, gp2, List.of("E1", "H3", "A2"));

			Salvo salvo5 = new Salvo(1, gp3, List.of("A2", "A4", "G6" ));
			Salvo salvo6 = new Salvo(1, gp4, List.of("B5", "D5", "C7"));
			Salvo salvo7 = new Salvo(2, gp3, List.of("A3", "H6"));
			Salvo salvo8 = new Salvo(2, gp4, List.of("C5", "C6"));
			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);
			salvoRepository.save(salvo5);
			salvoRepository.save(salvo6);
			salvoRepository.save(salvo7);
			salvoRepository.save(salvo8);

			//SCORE
			Score sc1 = new Score(game1, player1, 1, Date.from(game1.getCreationDate().toInstant().plusSeconds(3600)));
			Score sc2 = new Score(game1, player2, 0, Date.from(game1.getCreationDate().toInstant().plusSeconds(3600)));
			//Score sc3 = new Score(game2, player3, 0.5, Date.from(game2.getCreationDate().toInstant().plusSeconds(3600)));
			//Score sc4 = new Score(game2, player4, 0.5, Date.from(game2.getCreationDate().toInstant().plusSeconds(3600)));
			scRepository.save(sc1);
			scRepository.save(sc2);
			//scRepository.save(sc3);
			//scRepository.save(sc4);

		};
	}
}
