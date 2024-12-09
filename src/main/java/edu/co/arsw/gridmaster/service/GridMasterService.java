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

    @Autowired
    public GridMasterService(GridMasterPersistence gridMasterPersistence){
        this.gridMasterPersistence = gridMasterPersistence;
    }

    public Set<GridMaster> getAllGames() throws GridMasterException{
        return gridMasterPersistence.getAllGames();
    }

    public GridMaster getGameByCode(Integer code) throws GridMasterException {
        return gridMasterPersistence.getGameByCode(code);
    }

    public List<Player> getPlayers(Integer code) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        return new ArrayList<>(game.getPlayers().values());
    }

    public Player getPlayerByName(Integer code, String name) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        Player player = game.getPlayerByName(name);
        if(player == null){
            throw new PlayerNotFoundException();
        }
        return player;
    }

    public Map<String, Integer> getScoreboard(Integer code) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        return game.topTen();
    }

    public int getTime(Integer code) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        return game.getTime();
    }

    public Integer createGridMaster() throws GridMasterException {
        GridMaster newGame = new GridMaster();
        gridMasterPersistence.saveGame(newGame);
        return newGame.getCode();
    }

    public void startGame(Integer code) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        setPositions(game);
        game.setGameState(GameState.STARTED);
        gridMasterPersistence.saveGame(game);
    }

    public void endGame(Integer code) throws GridMasterException{
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        game.setGameState(GameState.FINISHED);
        game.setPlayerPositionInScoreboard();
        gridMasterPersistence.saveGame(game);
    }

    public void setPositions(GridMaster game) throws GridMasterException {
        for(Player i : game.getPlayers().values()){
            i.generatePosition(game.getDimension()[0], game.getDimension()[1]);
            i.addToTrace(new Position(i.getPosition().getX(), i.getPosition().getY()));
            game.getBox(new Position(i.getPosition().getX(), i.getPosition().getY())).setBusy(true);
            // System.out.println("Player: " + i.getName() + ", Position: " + i.getPosition());
        }
        gridMasterPersistence.saveGame(game);
    }

    public void addPlayer(Integer code, String name) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
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
                position = new int[]{player.getPosition().getX(), player.getPosition().getY()};
                Box box = game.getBox(new Position(position[0], position[1]));
                synchronized (box){
                    if(!box.isBusy()){
                        box.setBusy(true);
                        break;
                    }
                }
            }
            player.addToTrace(new Position(position[0], position[1]));
        }
        gridMasterPersistence.saveGame(game);
    }

    public void move(Integer code, String playerName, Position newPosition) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        Integer x = newPosition.getX();
        Integer y = newPosition.getY();
        if(x < 0 || y < 0 || x >= game.getDimension()[0] || y >= game.getDimension()[1]){
            throw new BoardException("Invalid move.");
        }
        Player player = game.getPlayerByName(playerName);
        Position oldPosition = new Position(player.getPosition().getX(), player.getPosition().getY());
        player.setLastPosition(oldPosition);
        changeScore(game, player, game.getBox(newPosition), game.getBox(oldPosition));
        gridMasterPersistence.saveGame(game);
    }

    public void changeScore(GridMaster game, Player player, Box newBox, Box oldBox){
        // The box is free and nobody is standing there
        if(newBox.getLock().tryLock() && !newBox.isBusy()){
            player.setPosition(newBox.getPosition());

            if(!player.containsPosition(newBox.getPosition().getX(), newBox.getPosition().getY())){
                player.addToTrace(newBox.getPosition());
                game.updateScoreOfPlayer(player.getName(), player.getTrace().size());
            }

            oldBox.setBusy(false);
            oldBox.setOwner(player);
            oldBox.setColor(player.getColor());

            newBox.setBusy(true);

            // Decrementing opponent score
            if(newBox.getOwner() != null && !newBox.getOwner().getName().equals(player.getName())){
                Player opponent = game.getPlayerByName(newBox.getOwner().getName());
                opponent.removeFromTrace(newBox.getPosition().getX(), newBox.getPosition().getY());
                game.updateScoreOfPlayer(opponent.getName(), opponent.getTrace().size());
            }
            newBox.getLock().unlock();
        }
        try {
            gridMasterPersistence.saveGame(game);
        } catch (GridMasterException e) {
            throw new GridMasterRuntimeException(e);
        }
    }

    public void updateGame(Integer code, Map<String, Integer> settings) throws GridMasterException{
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        game.updateSettings(settings);
        gridMasterPersistence.saveGame(game);
    }

    public void deleteGridMaster(Integer code) throws GridMasterException{
        gridMasterPersistence.deleteGame(code);
    }

    public void deletePlayer(Integer code, String name) throws GridMasterException{
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        if(!game.getPlayers().containsKey(name)){
            throw new PlayerNotFoundException();
        }
        game.removePlayer(name);
        gridMasterPersistence.saveGame(game);
    }

}
