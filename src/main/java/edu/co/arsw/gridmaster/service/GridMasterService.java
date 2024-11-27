package edu.co.arsw.gridmaster.service;

import edu.co.arsw.gridmaster.model.*;
import edu.co.arsw.gridmaster.model.exceptions.*;
import edu.co.arsw.gridmaster.persistance.GridMasterPersistence;
import edu.co.arsw.gridmaster.persistance.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GridMasterService {

    GridMasterPersistence gridMasterPersistence;
    SimpMessagingTemplate msgt;

    @Autowired
    public GridMasterService(GridMasterPersistence gridMasterPersistence, SimpMessagingTemplate msgt){
        this.gridMasterPersistence = gridMasterPersistence;
        this.msgt = msgt;
    }

    public Set<GridMaster> getAllGames(){
        return gridMasterPersistence.getAllGames();
    }

    public GridMaster getGameByCode(Integer code) throws GridMasterException {
        return gridMasterPersistence.getGameByCode(code);
    }

    public ArrayList<Player> getPlayers(Integer code) throws GridMasterException {
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

    public String getTime(Integer code) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        return game.getFormatTime();
    }

    public Integer createGridMaster() throws GridMasterException {
        GridMaster newGame = new GridMaster();
        gridMasterPersistence.saveGame(newGame);
        return newGame.getCode();
    }

    public void startGame(Integer code) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        game.setGameState(GameState.STARTED);
        startTime(game);
        setPositions(game);
    }

    public void startTime(GridMaster game){
        Timer timer = new Timer();
        Integer gameCode = game.getCode();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Sending scoreboard
                Map<String, Integer> scoreboard = game.topTen();
                msgt.convertAndSend("/topic/game/" + gameCode + "/score", scoreboard);
                // Sending time
                String time = game.getFormatTime();
                msgt.convertAndSend("/topic/game/" + gameCode + "/time", time);

                game.decrementTime();
                if(game.getTime() < 0){
                    timer.cancel();
                    try {
                        endGame(game.getCode());
                    } catch (GridMasterException e) {
                        System.out.println("Error finishing game.");
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void endGame(Integer code) throws GridMasterException{
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        game.setGameState(GameState.FINISHED);
        game.setPlayerPositionInScoreboard();
    }

    public void setPositions(GridMaster game) throws GridMasterException {
        ArrayList<int[]> positions = new ArrayList<>();
        int[] position;
        for(Player i : game.getPlayers().values()){
            do {
                i.generatePosition(game.getDimension().getFirst(), game.getDimension().getSecond());
                position = i.getPosition();
            } while (positions.contains(position));
            positions.add(position);
            i.addToTrace(new Tuple<>(position[0], position[1]));
            game.getBox(new Tuple<>(position[0], position[1])).setBusy(true);
        }
    }

    public void addPlayer(Integer code, String name) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        if(game.getMaxPlayers() == game.getPlayers().size()){
            throw new GameException("Room is full.");
        }
        if(game.getPlayers().containsKey(name)){
            throw new PlayerSaveException();
        }
        Player player = (game.getPlayers().isEmpty()) ? new Player(name, PlayerRole.ADMIN) : new Player(name, PlayerRole.PLAYER);;
        player.setColor(game.obtainColor());
        game.addPlayer(player);
        if(game.getGameState().equals(GameState.STARTED)){
            int[] position;
            while(true){
                Integer x = game.getDimension().getFirst();
                Integer y = game.getDimension().getSecond();
                player.generatePosition(x, y);
                position = player.getPosition();
                Box box = game.getBox(new Tuple<>(position[0], position[1]));
                synchronized (box){
                    if(!box.isBusy()){
                        box.setBusy(true);
                        break;
                    }
                }
            }
            player.addToTrace(new Tuple<>(position[0], position[1]));
        }
    }

    public void move(Integer code, String playerName, Tuple<Integer, Integer> newPosition) throws GridMasterException {
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        Integer x = newPosition.getFirst();
        Integer y = newPosition.getSecond();
        if(x < 0 || y < 0 || x >= game.getDimension().getFirst() || y >= game.getDimension().getSecond()){
            throw new BoardException("Invalid move.");
        }
        Player player = game.getPlayerByName(playerName);
        Tuple<Integer, Integer> oldPosition = new Tuple<>(player.getPosition()[0], player.getPosition()[1]);
        player.setLastPosition(oldPosition);
        changeScore(game, player, game.getBox(newPosition), game.getBox(oldPosition));
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
        }
    }

    public void updateGame(Integer code, HashMap<String, Integer> settings) throws GridMasterException{
        GridMaster game = gridMasterPersistence.getGameByCode(code);
        game.updateSettings(settings);
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
    }

}
