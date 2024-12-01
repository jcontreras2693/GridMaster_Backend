package edu.co.arsw.gridmaster.model;

public class Box {

    private int[] color;
    private Player owner;
    private int[] position;
    private boolean isBusy;

    public Box(int[] position){
        this.position = position;
        this.owner = null;
        this.isBusy = false;
        this.color = new int[]{0, 0, 0};
    }

    public Box(int[] color, Player owner, int[] position) {
        this.color = color;
        this.owner = owner;
        this.position = position;
    }

    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public int[] getPosition() {
        return position;
    }

}
