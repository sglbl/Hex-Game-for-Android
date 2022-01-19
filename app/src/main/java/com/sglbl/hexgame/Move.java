package com.sglbl.hexgame;

public class Move {
    private int x,y;
    private String status;

    public void setAll(int y,int x,String status){
        this.x=x;
        this.y=y;
        this.status = status;
    }

    public int getRow(){
        return y;
    }

    public int getColumn(){
        return x;
    }




}