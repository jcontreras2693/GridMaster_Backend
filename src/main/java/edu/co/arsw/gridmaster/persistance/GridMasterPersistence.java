package edu.co.arsw.gridmaster.persistance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.co.arsw.gridmaster.model.GridMaster;
import edu.co.arsw.gridmaster.model.exceptions.GameNotFoundException;
import edu.co.arsw.gridmaster.model.exceptions.GamePersistanceException;
import edu.co.arsw.gridmaster.model.exceptions.GridMasterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Set;

@Service
public class GridMasterPersistence {

    private JedisPool jedisPool;
    private ObjectMapper objectMapper;

    @Autowired
    public GridMasterPersistence(JedisPool jedisPool, ObjectMapper objectMapper){
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
    }

    public void saveGame(GridMaster game) throws GridMasterException {
        try (Jedis jedis = jedisPool.getResource()) {
            String code = game.getCode().toString();
            String json = objectMapper.writeValueAsString(game);
            jedis.set(code, json);
        } catch (Exception e) {
            System.out.println(e);
            throw new GamePersistanceException();
        }
    }

    public GridMaster getGameByCode(Integer code) throws GridMasterException {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(code.toString());
            if (json == null) {
                throw new GameNotFoundException();
            }
            return objectMapper.readValue(json, GridMaster.class);
        } catch (JsonProcessingException e) {
            System.out.println(e);
            return null;
        }
    }

    public void deleteGame(Integer code) throws GridMasterException {
        try (Jedis jedis = jedisPool.getResource()) {
            String str = code.toString();
            if (!jedis.exists(str)) {
                throw new GameNotFoundException();
            }
            jedis.del(str);
        }
    }

    public Set<GridMaster> getAllGames() {
        Set<GridMaster> games = new HashSet<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("*");
            for (String key : keys) {
                try {
                    GridMaster game = getGameByCode(Integer.valueOf(key));
                    games.add(game);
                } catch (GridMasterException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return games;
    }
}
