package edu.co.arsw.gridmaster.persistance;

import edu.co.arsw.gridmaster.model.GridMaster;
import edu.co.arsw.gridmaster.model.exceptions.GameNotFoundException;
import edu.co.arsw.gridmaster.model.exceptions.GridMasterException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GridMasterPersistence {

    private ConcurrentHashMap<Integer, GridMaster> games = new ConcurrentHashMap<>();

    public void saveGame(GridMaster game) throws GridMasterException {
        games.put(game.getCode(), game);
    }

    public GridMaster getGameByCode(Integer code) throws GridMasterException{
        if(!games.containsKey(code)){
            throw new GameNotFoundException();
        }
        return games.get(code);
    }

    public void deleteGame(Integer code) throws GridMasterException{
        if(!games.containsKey(code)){
            throw new GameNotFoundException();
        }
        games.remove(code);
    }

    public Set<GridMaster> getAllGames(){
        return new HashSet<>(games.values());
    }

}