package edu.co.arsw.gridmaster;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import edu.co.arsw.gridmaster.model.*;
import edu.co.arsw.gridmaster.service.*;
import edu.co.arsw.gridmaster.persistance.GridMasterPersistence;
import edu.co.arsw.gridmaster.model.exceptions.*;
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
import java.util.concurrent.ConcurrentMap;


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

	@Test
	void contextLoads() {
	}

	@BeforeEach
	void setUp() throws GridMasterException{
		mockGame = mock(GridMaster.class);

		mockPlayers = new ConcurrentHashMap<>();
		mockPlayers.put("Mauricio", new Player("Mauricio", PlayerRole.ADMIN));
		mockPlayers.put("Samuel", new Player("Samuel", PlayerRole.PLAYER));

		mockPlayer = mock(Player.class);

		mockBox = mock(Box.class);
	}

	@Test
	void shouldGetAllGames() throws GridMasterException {
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

		GridMaster game = mockPersistence.getGameByCode(1);

		game.setPlayers((ConcurrentMap<String, Player>) mockPlayers);

		assertNotNull(mockPlayers);
		assertEquals(2, mockPlayers.size());
		assertTrue(mockPlayers.containsKey("Mauricio"));
		assertTrue(mockPlayers.containsKey("Samuel"));

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
		int mockTime = 300;

		when(mockPersistence.getGameByCode(1)).thenReturn(mockGame);

		when(mockGame.getTime()).thenReturn(mockTime);

		int result = mockService.getTime(1);

		assertNotNull(result);
		assertEquals(mockTime, result);

		verify(mockPersistence).getGameByCode(1);
		verify(mockGame).getTime();
	}

	@Test
	void shouldCreateGridMaster() throws GridMasterException {
		doNothing().when(mockPersistence).saveGame(any(GridMaster.class));

		Integer resultCode = mockService.createGridMaster();

		assertNotNull(resultCode);

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
		Integer code = 123;
		when(mockPersistence.getGameByCode(code)).thenReturn(mockGame);

		mockService.startGame(code);

		verify(mockGame).setGameState(GameState.STARTED);

		verify(mockPersistence).saveGame(mockGame);
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
		int[] dimensions = {50, 50};
		when(mockGame.getDimension()).thenReturn(dimensions);
		when(mockGame.getPlayers()).thenReturn((ConcurrentMap<String, Player>) mockPlayers);
		when(mockGame.getBox(any(Position.class))).thenReturn(mockBox);

		when(mockPlayer.getPosition()).thenReturn(new Position(0, 0));

		doNothing().when(mockPlayer).generatePosition(anyInt(), anyInt());

		mockService.setPositions(mockGame);

		verify(mockBox, times(2)).setBusy(true);
		verify(mockPersistence).saveGame(mockGame);
	}

	//Pruebas de addPlayer()

	@Test
	void shouldNotAddPlayerRoomFull() throws GridMasterException {
		Integer gameCode = mockGame.getCode();

		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);
		when(mockGame.getMaxPlayers()).thenReturn(2);
		when(mockGame.getPlayers()).thenReturn((ConcurrentMap<String, Player>) mockPlayers);

		GameException exception = assertThrows(GameException.class, () -> {
			mockService.addPlayer(gameCode, "Mauricio");
		});

		assertEquals("Room is full.", exception.getMessage());
	}

	@Test
	void shouldNotAddPlayerAlreadyExists() throws GridMasterException {
		Integer gameCode = mockGame.getCode();

		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);
		when(mockGame.getMaxPlayers()).thenReturn(4);
		when(mockGame.getPlayers()).thenReturn((ConcurrentMap<String, Player>) mockPlayers);

		assertThrows(PlayerSaveException.class, () -> {
			mockService.addPlayer(gameCode, "Mauricio");
		});
	}

	@Test
	void shouldAddPlayerSuccessfully() throws GridMasterException {
// Configuración de prueba
		Integer code = 123;
		String playerName = "Juan";

		when(mockPersistence.getGameByCode(code)).thenReturn(mockGame);
		when(mockGame.getPlayers()).thenReturn((ConcurrentMap<String, Player>) mockPlayers);
		when(mockGame.getMaxPlayers()).thenReturn(4);
		when(mockGame.getGameState()).thenReturn(GameState.STARTED);

		when(mockBox.isBusy()).thenReturn(false);
		when(mockGame.getBox(any(Position.class))).thenReturn(mockBox);

		int[] blue = {0, 0, 255};
		when(mockGame.obtainColor()).thenReturn(blue);
		when(mockGame.getDimension()).thenReturn(new int[]{50, 50});

		mockService.addPlayer(code, playerName);

		verify(mockGame).addPlayer(any(Player.class));

		verify(mockPersistence).saveGame(mockGame);

		assertDoesNotThrow(() -> mockService.addPlayer(code, playerName));
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
		settings.put("minutes", 5);
		settings.put("seconds", 0);
		settings.put("xDimension", 50);
		settings.put("yDimension", 50);

		mockPersistence.saveGame(mockGame);

		when(mockPersistence.getGameByCode(1)).thenReturn(mockGame);

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
		Integer gameCode = 123;

		when(mockGame.getPlayers()).thenReturn((ConcurrentMap<String, Player>) mockPlayers);
		when(mockPersistence.getGameByCode(gameCode)).thenReturn(mockGame);

		mockService.deletePlayer(gameCode, "Mauricio");

		verify(mockGame).removePlayer("Mauricio");
	}
}