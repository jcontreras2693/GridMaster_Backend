package edu.co.arsw.gridmaster;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import edu.co.arsw.gridmaster.model.*;
import edu.co.arsw.gridmaster.service.*;
import edu.co.arsw.gridmaster.persistance.GridMasterPersistence;
import edu.co.arsw.gridmaster.model.exceptions.*;
import edu.co.arsw.gridmaster.persistance.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;


@SpringBootTest
class GridmasterServiceTests {

	@Mock
	private GridMasterPersistence mockPersistence;

	@InjectMocks
	private GridMasterService mockService;

	private GridMaster mockGame;

	private Map<String, Player> mockPlayers;

	private Player mockPlayer;

	private Box mockBox;

	@BeforeEach
	void setUp() {
		mockGame = mock(GridMaster.class);

		mockPlayers = new HashMap<>();
		mockPlayers.put("Mauricio", new Player("Mauricio"));
		mockPlayers.put("Samuel", new Player("Samuel"));

		mockPlayer = mock(Player.class);

		// when(mockGame.getPlayers()).thenReturn(mockPlayers);
	}

	@Test
	void shouldGetAllGames() {
		HashSet<GridMaster> games = new HashSet<>();
		games.add(new GridMaster());
		games.add(new GridMaster());
		when(mockPersistence.getAllGames()).thenReturn(games);

		var result = mockService.getAllGames();

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(mockPersistence, times(1)).getAllGames();
	}

	@Test
	void shouldGetGameByCode() throws GridMasterException {
		when(mockPersistence.getGameByCode(1)).thenReturn(mockGame);

		GridMaster result = mockService.getGameByCode(1);

		assertNotNull(result);
		assertEquals(mockGame, result);

		verify(mockPersistence).getGameByCode(1);
	}

	@Test
	void shouldNotGetGameByCode() throws GridMasterException {
		when(mockPersistence.getGameByCode(9)).thenThrow(new GridMasterException("Game not found"));

		assertThrows(GridMasterException.class, () -> {
			mockService.getGameByCode(9);
		});

		verify(mockPersistence).getGameByCode(9);
	}

	@Test
	void shouldGetPlayers() throws GridMasterException {
		when(mockPersistence.getGameByCode(1)).thenReturn(mockGame);

		ArrayList<Player> players = mockService.getPlayers(1);

		assertNotNull(players);
		assertEquals(2, players.size());
		assertTrue(players.stream().anyMatch(p -> p.getName().equals("Mauricio")));
		assertTrue(players.stream().anyMatch(p -> p.getName().equals("Samuel")));

		verify(mockPersistence).getGameByCode(1);
	}

	@Test
	void shouldGetPlayerByName() throws GridMasterException {
		when(mockPersistence.getGameByCode(1)).thenReturn(mockGame);
		when(mockGame.getPlayerByName("Mauricio")).thenReturn(mockPlayers.get("Mauricio"));

		Player player = mockService.getPlayerByName(1, "Mauricio");

		assertNotNull(player);
		assertEquals("Mauricio", player.getName());

		verify(mockPersistence).getGameByCode(1);
		verify(mockGame).getPlayerByName("Mauricio");
	}

	@Test
	void testGetScoreboard_Success() throws GridMasterException {
		Map<String, Integer> mockScoreboard = new LinkedHashMap<>();
		mockScoreboard.put("Mauricio", 10);
		mockScoreboard.put("Samuel", 5);
		mockScoreboard.put("Juan", 1);

		when(mockPersistence.getGameByCode(1)).thenReturn(mockGame);

		when(mockGame.topTen()).thenReturn(mockScoreboard);

		Map<String, Integer> result = mockService.getScoreboard(1);

		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(10, result.get("Mauricio"));
		assertEquals(5, result.get("Samuel"));
		assertEquals(1, result.get("Juan"));

		verify(mockPersistence).getGameByCode(1);
		verify(mockGame).topTen();
	}

	@Test
	void shouldGetTime() throws GridMasterException {
		String mockTime = "05:00";

		when(mockPersistence.getGameByCode(1)).thenReturn(mockGame);

		when(mockGame.getFormatTime()).thenReturn(mockTime);

		String result = mockService.getTime(1);

		assertNotNull(result);
		assertEquals(mockTime, result);

		verify(mockPersistence).getGameByCode(1);
		verify(mockGame).getFormatTime();
	}

	@Test
	void shouldCreateGridMaster() throws GridMasterException {
		GridMaster mockGame = mock(GridMaster.class);
		int mockCode = 11;

		when(mockGame.getCode()).thenReturn(mockCode);

		doNothing().when(mockPersistence).saveGame(any(GridMaster.class));

		Integer result = mockService.createGridMaster();

		assertNotNull(result);
		assertEquals(mockCode, result);

		verify(mockPersistence).saveGame(any(GridMaster.class));
	}

	@Test
	void shouldNotCreateGridMaster() throws GridMasterException {
		doThrow(new GridMasterException("Error saving game"))
				.when(mockPersistence)
				.saveGame(any(GridMaster.class));

		assertThrows(GridMasterException.class, () -> mockService.createGridMaster());

		verify(mockPersistence).saveGame(any(GridMaster.class));
	}

	@Test
	void shouldStartGame() throws GridMasterException {
		Integer gameCode = 12;
		GridMaster mockGame = mock(GridMaster.class);

		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);

		mockService.startGame(gameCode);

		verify(mockGame).setGameState(GameState.STARTED);
		verify(mockService).startTime(mockGame);
		verify(mockService).setPositions(mockGame);
		verify(mockPersistence).getGameByCode(gameCode);
	}

	/*
	@Test
	void shouldStartTime() throws GridMasterException {
	}
	 */

	@Test
	void shouldEndGame() throws GridMasterException {
		Integer gameCode = 2;
		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);

		mockService.endGame(gameCode);

		verify(mockPersistence).getGameByCode(gameCode);
		verify(mockGame).setGameState(GameState.FINISHED);
		verify(mockGame).setPlayerPositionInScoreboard();
	}

	@Test
	void shouldSetPositions() throws GridMasterException {
		Integer gameCode = 123;
		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);

		when(mockGame.getPlayers()).thenReturn((ConcurrentHashMap<String, Player>) Map.of("player1", mockPlayer));

		int[] generatedPosition = {1, 2};
		when(mockPlayer.getPosition()).thenReturn(generatedPosition);
		doNothing().when(mockPlayer).addToTrace(any());

		when(mockGame.getBox(new Tuple<>(1, 2))).thenReturn(mockBox);
		doNothing().when(mockBox).setBusy(true);

		mockService.setPositions(mockGame);

		verify(mockPlayer).generatePosition(anyInt(), anyInt());
		verify(mockPlayer).addToTrace(any());
		verify(mockBox).setBusy(true);
	}

	//Pruebas de addPlayer()

	@Test
	void shouldAddPlayerRoomFull() throws GridMasterException {
		// Arrange
		Integer gameCode = 123;
		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);
		when(mockGame.getMaxPlayers()).thenReturn(2);
		when(mockGame.getPlayers().size()).thenReturn(2);

		// Act and Assert
		GameException exception = assertThrows(GameException.class, () -> {
			mockService.addPlayer(gameCode, "Player1");
		});

		assertEquals("Room is full.", exception.getMessage());
	}

	@Test
	void shouldAddPlayerAlreadyExists() throws GridMasterException {
		Integer gameCode = 123;
		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);
		when(mockGame.getMaxPlayers()).thenReturn(4);
		when(mockGame.getPlayers().size()).thenReturn(2);
		when(mockGame.getPlayers().containsKey("Player1")).thenReturn(true);

		assertThrows(PlayerSaveException.class, () -> {
			mockService.addPlayer(gameCode, "Player1");
		});
	}

	@Test
	void shouldAddPlayerSuccessfully() throws GridMasterException {
		// Arrange
		Integer gameCode = 123;
		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);
		when(mockGame.getMaxPlayers()).thenReturn(4);
		when(mockGame.getPlayers().size()).thenReturn(2);
		when(mockGame.getPlayers().containsKey("Player1")).thenReturn(false);

		when(mockGame.getPlayers().isEmpty()).thenReturn(true);
		int[] red = {255, 0, 0};
		when(mockGame.obtainColor()).thenReturn(red);

		mockService.addPlayer(gameCode, "Player1");

		// Assert
		verify(mockGame).addPlayer(any(Player.class));
		verify(mockPlayer).setPosition(any());
		verify(mockPlayer).addToTrace(any());
	}

	@Test
	void shouldAddPlayerPositionGeneration() throws GridMasterException {
		// Arrange
		Integer gameCode = 123;
		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);
		when(mockGame.getMaxPlayers()).thenReturn(4);
		when(mockGame.getPlayers().size()).thenReturn(2);
		when(mockGame.getPlayers().containsKey("Player1")).thenReturn(false);
		when(mockGame.getPlayers().isEmpty()).thenReturn(true);
		int[] blue = {0, 0, 255};
		when(mockGame.obtainColor()).thenReturn(blue);

		int[] generatedPosition = {2, 3};
		when(mockPlayer.getPosition()).thenReturn(generatedPosition);
		when(mockGame.getBox(any())).thenReturn(mockBox);
		when(mockBox.isBusy()).thenReturn(false);

		mockService.addPlayer(gameCode, "Player1");

		verify(mockPlayer).generatePosition(anyInt(), anyInt());
		verify(mockBox).setBusy(true);
	}

	/*
	@Test
	void shouldMove() throws GridMasterException {
	}
	 */

	/*
	@Test
	void shouldChangeScore() throws GridMasterException {
	}
	 */

	@Test
	public void shouldUpdateGame() throws GridMasterException {
		HashMap<String, Integer> settings = new HashMap<>();
		settings.put("maxPlayers", 4);
		settings.put("timeLimit", 30);

		mockService.updateGame(1, settings);

		verify(mockGame).updateSettings(settings);
	}

	@Test
	public void shouldDeleteGridMaster() throws GridMasterException {
		mockService.deleteGridMaster(1);

		verify(mockPersistence).deleteGame(1);
	}

	@Test
	public void shouldDeletePlayer() throws GridMasterException {
		when(mockGame.getPlayers()).thenReturn((ConcurrentHashMap<String, Player>) Map.of("Mauricio", new Player("Mauricio")));

		mockService.deletePlayer(1, "Mauricio");

		verify(mockGame).removePlayer("Mauricio");
	}
}
