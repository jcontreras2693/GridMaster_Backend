package edu.co.arsw.gridmaster.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.co.arsw.gridmaster.persistance.Tuple;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {

    private String name;
    private int[] color;
    private AtomicInteger score;
    private Tuple<Integer, Integer> currentPosition;
    private Tuple<Integer, Integer> lastPosition;
    private Set<Tuple<Integer, Integer>> trace;
    private Integer scoreboardPosition;

    @JsonCreator
    public Player(@JsonProperty("name") String name){
        this.name = name;
        this.score = new AtomicInteger(1);
        this.color = new int[]{0, 0, 0};
        this.trace = ConcurrentHashMap.newKeySet();
        this.currentPosition = new Tuple<>(0, 0);
        this.lastPosition = new Tuple<>(0, 0);
        this.scoreboardPosition = 0;
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
        return new int[]{this.lastPosition.getFirst(), this.lastPosition.getSecond()};
    }

    public void setLastPosition(Tuple<Integer, Integer> oldPosition){
        this.lastPosition = oldPosition;
    }

    public void incrementScore(){
        this.score.incrementAndGet();
    }

    public void decrementScore(){
        this.score.decrementAndGet();
    }

    public int[] getPosition() {
        return new int[]{this.currentPosition.getFirst(), this.currentPosition.getSecond()};
    }

    public void setPosition(Tuple<Integer, Integer> position) {
        this.currentPosition = position;
    }

    public Integer getScoreboardPosition() {
        return scoreboardPosition;
    }

    public void setScoreboardPosition(Integer scoreboardPosition) {
        this.scoreboardPosition = scoreboardPosition;
    }

    public void generatePosition(Integer x, Integer y) {
        Random rand = new Random();
        this.currentPosition = new Tuple<>(rand.nextInt(x), rand.nextInt(y));
    }

    public boolean isLocatedAt(Integer x, Integer y){
        return (Objects.equals(this.currentPosition.getFirst(), x) && Objects.equals(this.currentPosition.getSecond(), y));
    }

    public Set<Tuple<Integer, Integer>> getTrace(){
        return this.trace;
    }

    public void setTrace(Set<Tuple<Integer, Integer>> newTrace){
        this.trace = newTrace;
    }

    public void addToTrace(Tuple<Integer, Integer> tuple){
        trace.add(tuple);
    }

    public void removeFromTrace(Tuple<Integer, Integer> tuple){
        trace.remove(tuple);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", position=" + currentPosition.getFirst() + " " + currentPosition.getSecond() +
                '}';
    }
}
