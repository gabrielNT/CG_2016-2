package gamep;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import gamep.GamePiece;

public class GameCenterPiece extends GamePiece {
    char direction;
    
    public GameCenterPiece(int x, int y, Shape shape){
        super(x, y, shape);
    }
   
    public void moveRight(){
        this.direction = 'r';
    }
    
    public void moveLeft(){
        this.direction = 'l';
    }
    
    public void moveUp(){
        this.direction = 'u';
    }
    
    public void moveDown(){
        this.direction = 'd';
    }
    
    public void stopMoving(){
        this.direction = 'n';
    }
}
