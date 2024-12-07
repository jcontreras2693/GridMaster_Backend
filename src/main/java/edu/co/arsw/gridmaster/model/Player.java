package edu.co.arsw.gridmaster.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
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
    private Position currentPosition;
    @JsonProperty
    private Position lastPosition;
    @JsonProperty
    private Set<Position> trace;
    @JsonProperty
    private Integer scoreboardPosition;
    @JsonProperty
    private PlayerRole playerRole;

    @JsonCreator
    public Player(@JsonProperty("name") String name, PlayerRole playerRole) {
        this.name = name;
        this.color = new int[]{0, 0, 0};
        this.trace = ConcurrentHashMap.newKeySet();
        this.currentPosition = new Position(0, 0);
        this.lastPosition = new Position(0, 0);
        this.scoreboardPosition = 0;
        this.playerRole = playerRole;
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

    public Position getLastPosition() {
        return this.lastPosition;
    }

    public void setLastPosition(Position oldPosition) {
        this.lastPosition = oldPosition;
    }

    public Position getPosition() {
        return this.currentPosition;
    }

    public void setPosition(Position position) {
        this.currentPosition = position;
    }

    public Integer getScoreboardPosition() {
        return this.scoreboardPosition;
    }

    public void setScoreboardPosition(Integer scoreboardPosition) {
        this.scoreboardPosition = scoreboardPosition;
    }

    public PlayerRole getPlayerRole() {
        return this.playerRole;
    }

    public void setPlayerRole(PlayerRole playerRole) {
        this.playerRole = playerRole;
    }

    public void generatePosition(Integer x, Integer y) {
        Random rand = new Random();
        this.currentPosition = new Position(rand.nextInt(x), rand.nextInt(y));
    }

    public Set<Position> getTrace() {
        return this.trace;
    }

    public void setTrace(Set<Position> newTrace) {
        this.trace = newTrace;
    }

    public void addToTrace(Position tuple) {
        System.out.println("Tuple to add: " + tuple);
        this.trace.add(tuple);
    }

    public void removeFromTrace(Integer x, Integer y) {
        trace.removeIf(p -> p.getX() == x && p.getY() == y);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", score=" + trace.size() +
                ", currentPosition=" + currentPosition +
                ", lastPosition=" + lastPosition +
                ", trace=" + trace.toString() +
                '}';
    }
}