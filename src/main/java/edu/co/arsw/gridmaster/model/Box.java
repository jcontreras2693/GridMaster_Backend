package edu.co.arsw.gridmaster.model;

import java.util.concurrent.locks.ReentrantLock;

public class Box {

    private int[] color;
    private Player owner;
    private Position position;
    private boolean isBusy;
    private ReentrantLock lock;

    public Box(Position position){
        this.position = position;
        this.owner = null;
        this.isBusy = false;
        this.color = new int[]{0, 0, 0};
        this.lock = new ReentrantLock();
    }

    public Box(int[] color, Player owner, Position position) {
        this.color = color;
        this.owner = owner;
        this.position = position;
    }

    public int[] getColor() {
        return this.color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isBusy() {
        return this.isBusy;
    }

    public void setBusy(boolean busy) {
        this.isBusy = busy;
    }

    public Position getPosition() {
        return this.position;
    }

    public ReentrantLock getLock() {
        return this.lock;
    }

    @Override
    public String toString() {
        return "Box{" +
                ", owner=" + owner +
                ", position=" + position +
                ", isBusy=" + isBusy +
                '}';
    }
}
