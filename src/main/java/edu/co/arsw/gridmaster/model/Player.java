package edu.co.arsw.gridmaster.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {

    @JsonProperty
    private String name;
    @JsonProperty
    private int[] color;
    @JsonProperty
    private AtomicInteger score;
    @JsonProperty
    private int[] currentPosition;
    @JsonProperty
    private int[] lastPosition;
    @JsonProperty
    private Set<int[]> trace;
    @JsonProperty
    private Integer scoreboardPosition;
    @JsonProperty
    private PlayerRole playerRole;
    private final Random rand;

    @JsonCreator
    public Player(@JsonProperty("name") String name, PlayerRole playerRole){
        this.name = name;
        this.score = new AtomicInteger(1);
        this.color = new int[]{0, 0, 0};
        this.trace = ConcurrentHashMap.newKeySet();
        this.currentPosition = new int[]{0, 0};
        this.lastPosition = new int[]{0, 0};
        this.scoreboardPosition = 0;
        this.playerRole = playerRole;
        this.rand = new Random();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public AtomicInteger getScore() {
        return score;
    }

    public void setScore(AtomicInteger score) {
        this.score = score;
    }

    public int[] getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int[] oldPosition){
        this.lastPosition = oldPosition;
    }

    public void incrementScore(){
        this.score.incrementAndGet();
    }

    public void decrementScore(){
        this.score.decrementAndGet();
    }

    public int[] getPosition() {
        return currentPosition;
    }

    public void setPosition(int[] position) {
        this.currentPosition = position;
    }

    public Integer getScoreboardPosition() {
        return scoreboardPosition;
    }

    public void setScoreboardPosition(Integer scoreboardPosition) {
        this.scoreboardPosition = scoreboardPosition;
    }

    public PlayerRole getPlayerRole() {
        return playerRole;
    }

    public void setPlayerRole(PlayerRole playerRole) {
        this.playerRole = playerRole;
    }

    public void generatePosition(Integer x, Integer y) {
        this.currentPosition = new int[]{rand.nextInt(x), rand.nextInt(y)};
    }

    public boolean isLocatedAt(Integer x, Integer y){
        return (Objects.equals(this.currentPosition[0], x) && Objects.equals(this.currentPosition[1], y));
    }

    public Set<int[]> getTrace(){
        return this.trace;
    }

    public void setTrace(Set<int[]> newTrace){
        this.trace = newTrace;
    }

    public void addToTrace(int[] tuple){
        trace.add(tuple);
    }

    public void removeFromTrace(int[] tuple){
        trace.remove(tuple);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", position=" + currentPosition[0] + " " + currentPosition[1] +
                ", trace" + trace.toString() +
                '}';
    }
}
