package edu.co.arsw.gridmaster.service;

import edu.co.arsw.gridmaster.model.*;
import edu.co.arsw.gridmaster.model.exceptions.*;
import edu.co.arsw.gridmaster.persistance.GridMasterPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GridMasterService {

    GridMasterPersistence gridMasterPersistence;
    GridMaster currentGame;

    @Autowired
    public GridMasterService(GridMasterPersistence gridMasterPersistence){
        this.gridMasterPersistence = gridMasterPersistence;
    }

    public Set<GridMaster> getAllGames(){
        return gridMasterPersistence.getAllGames();
    }

    public GridMaster getGameByCode(Integer code) throws GridMasterException {
        return (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
    }

    public List<Player> getPlayers(Integer code) throws GridMasterException {
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        return new ArrayList<>(game.getPlayers().values());
    }

    public Player getPlayerByName(Integer code, String name) throws GridMasterException {
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        Player player = game.getPlayerByName(name);
        if(player == null){
            throw new PlayerNotFoundException();
        }
        return player;
    }

    public Map<String, Integer> getScoreboard(Integer code) throws GridMasterException {
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        return game.topTen();
    }

    public int getTime(Integer code) throws GridMasterException {
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        return game.getTime();
    }

    public Integer createGridMaster() throws GridMasterException {
        GridMaster newGame = new GridMaster();
        gridMasterPersistence.saveGame(newGame);
        currentGame = newGame;
        return newGame.getCode();
    }

    public void startGame(Integer code) throws GridMasterException {
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        game.setGameState(GameState.STARTED);
        setPositions(game);
        gridMasterPersistence.saveGame(game);
    }

    public void endGame(Integer code) throws GridMasterException{
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        game.setGameState(GameState.FINISHED);
        game.setPlayerPositionInScoreboard();
        gridMasterPersistence.saveGame(game);
    }

    public void setPositions(GridMaster game) throws GridMasterException {
        ArrayList<int[]> positions = new ArrayList<>();
        int[] position;
        for(Player i : game.getPlayers().values()){
            do {
                i.generatePosition(game.getDimension()[0], game.getDimension()[1]);
                position = new int[]{i.getPosition()[0], i.getPosition()[1]};
            } while (positions.contains(position));
            positions.add(position);
            i.addToTrace(new int[]{position[0], position[1]});
            game.getBox(new int[]{position[0], position[1]}).setBusy(true);
        }
        gridMasterPersistence.saveGame(game);
    }

    public void addPlayer(Integer code, String name) throws GridMasterException {
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        if(game.getMaxPlayers() == game.getPlayers().size()){
            throw new GameException("Room is full.");
        }
        if(game.getPlayers().containsKey(name)){
            throw new PlayerSaveException();
        }
        Player player = (game.getPlayers().isEmpty()) ? new Player(name, PlayerRole.ADMIN) : new Player(name, PlayerRole.PLAYER);
        player.setColor(game.obtainColor());
        game.addPlayer(player);
        if(game.getGameState().equals(GameState.STARTED)){
            int[] position;
            while(true){
                Integer x = game.getDimension()[0];
                Integer y = game.getDimension()[1];
                player.generatePosition(x, y);
                position = new int[]{player.getPosition()[0], player.getPosition()[1]};
                Box box = game.getBox(new int[]{position[0], position[1]});
                synchronized (box){
                    if(!box.isBusy()){
                        box.setBusy(true);
                        break;
                    }
                }
            }
            player.addToTrace(new int[]{position[0], position[1]});
        }
        gridMasterPersistence.saveGame(game);
    }

    public void move(Integer code, String playerName, int[] newPosition) throws GridMasterException {
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        Integer x = newPosition[0];
        Integer y = newPosition[1];
        if(x < 0 || y < 0 || x >= game.getDimension()[0] || y >= game.getDimension()[1]){
            throw new BoardException("Invalid move.");
        }
        Player player = game.getPlayerByName(playerName);
        int[] oldPosition = new int[]{player.getPosition()[0], player.getPosition()[1]};
        player.setLastPosition(oldPosition);
        changeScore(game, player, game.getBox(newPosition), game.getBox(oldPosition));
        gridMasterPersistence.saveGame(game);
    }

    public void changeScore(GridMaster game, Player player, Box newBox, Box oldBox){
        // Just locking the box
        synchronized (newBox){
            // The box is free and nobody is standing there
            if(!newBox.isBusy()){
                player.setPosition(newBox.getPosition());

                if(!player.getTrace().contains(newBox.getPosition())){
                    player.addToTrace(newBox.getPosition());
                    player.incrementScore();
                    game.updateScoreOfPlayer(player.getName(), player.getScore().get());
                }

                oldBox.setBusy(false);
                oldBox.setOwner(player);
                oldBox.setColor(player.getColor());

                newBox.setBusy(true);

                // Decrementing opponent score
                if(newBox.getOwner() != null && !newBox.getOwner().getName().equals(player.getName())){
                    Player opponent = newBox.getOwner();
                    opponent.removeFromTrace(newBox.getPosition());
                    opponent.decrementScore();
                    game.updateScoreOfPlayer(opponent.getName(), opponent.getScore().get());
                }
            }
            try {
                gridMasterPersistence.saveGame(game);
            } catch (GridMasterException e) {
                throw new GridMasterRuntimeException(e);
            }
        }
    }

    public void updateGame(Integer code, Map<String, Integer> settings) throws GridMasterException{
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        game.updateSettings(settings);
        gridMasterPersistence.saveGame(game);
    }

    public void deleteGridMaster(Integer code) throws GridMasterException{
        gridMasterPersistence.deleteGame(code);
    }

    public void deletePlayer(Integer code, String name) throws GridMasterException{
        GridMaster game = (currentGame.getCode().equals(code)) ? currentGame : gridMasterPersistence.getGameByCode(code);
        if(!game.getPlayers().containsKey(name)){
            throw new PlayerNotFoundException();
        }
        game.removePlayer(name);
        gridMasterPersistence.saveGame(game);
    }

}
