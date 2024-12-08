package edu.co.arsw.gridmaster.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class GridMaster {

    @JsonProperty
    private Integer code;
    @JsonProperty
    private Integer time;
    @JsonProperty
    private Integer maxPlayers;
    @JsonProperty
    private ConcurrentMap<String, Integer> scores;
    @JsonProperty
    private ConcurrentMap<String, Player> players;
    @JsonProperty
    private int[] dimension;
    @JsonProperty
    private List<List<Box>> boxes;
    @JsonProperty
    private Color color;
    @JsonProperty
    private GameState gameState;

    public GridMaster() {
        SecureRandom random = new SecureRandom();
        this.code = random.nextInt(10000 - 1000 + 1) + 1000;
        this.scores = new ConcurrentHashMap<>();
        this.players = new ConcurrentHashMap<>();
        this.color = new Color();
        this.gameState = GameState.WAITING_FOR_PLAYERS;
        this.time = 300;
        this.dimension = new int[]{50, 50};
        this.maxPlayers = 4;
        this.boxes = new ArrayList<>();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer delta) {
        this.time = delta;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer newMaxPlayers) {
        this.maxPlayers = newMaxPlayers;
    }

    public ConcurrentMap<String, Integer> getScores() {
        return scores;
    }

    public void setScores(ConcurrentMap<String, Integer> scores) {
        this.scores = scores;
    }

    public ConcurrentMap<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(ConcurrentMap<String, Player> players) {
        this.players = players;
    }

    public Player getPlayerByName(String name){
        return this.players.get(name);
    }

    public List<List<Box>> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<List<Box>> boxes) {
        this.boxes = boxes;
    }

    public Box getBox(Position position){
        return boxes.get(position.getX()).get(position.getY());
    }

    public int[] getDimension() {
        return dimension;
    }

    public void setDimension(int[] dimension) {
        this.dimension = dimension;
    }

    public void updateScoreOfPlayer(String name, Integer score){
        scores.put(name, score);
    }

    public int[] obtainColor(){
        return color.getColor();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public String toString() {
        return "Game{" +
                "code=" + code +
                ", time=" + time +
                ", maxPlayers=" + maxPlayers +
                ", players=" + players +
                '}';
    }

    public void addPlayer(Player player){
        players.put(player.getName(), player);
        scores.put(player.getName(), player.getTrace().size());
    }

    public void removePlayer(String name){
        players.remove(name);
        scores.remove(name);
    }

    public void setPlayerPositionInScoreboard(){
        Map<String, Integer> orderedScores = this.scores.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        int position = 1;
        for(String key : orderedScores.keySet()){
            players.get(key).setScoreboardPosition(position);
            position++;
        }
    }

    public Map<String, Integer> topTen(){
        return this.scores.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public void updateSettings(Map<String, Integer> settings){
        this.time = (settings.get("minutes") * 60) + settings.get("seconds");
        this.dimension = new int[]{settings.get("xDimension"), settings.get("yDimension")};
        this.maxPlayers = settings.get("maxPlayers");
        this.boxes = new ArrayList<>();
        for(int i = 0; i < dimension[0]; i++){
            boxes.add(new ArrayList<>());
            for(int j = 0; j < dimension[1]; j++){
                boxes.get(i).add(new Box( new Position(i, j) ));
            }
        }
    }
}

