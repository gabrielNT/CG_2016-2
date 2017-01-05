package gamep;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import java.lang.Object;
import java.awt.Point;

enum Shape{
    SQUARE,
    TRIANGLE,
    CIRCLE,
    PARALLELOGRAM
}

public class GamePiece {
    Point point = new Point();
    Shape shape;
    ArrayList points = new ArrayList();
    
    public GamePiece(int x, int y, Shape shape){
        this.point.setLocation(x, y);
        this.shape = shape;
        updatePoints();
    }
    
    void updatePoints(){
        this.points.clear();
        
        switch (this.shape){
            case SQUARE:
                createSquarePoints();
                break;
            default:
                break;
        }      
    }
    
    void createSquarePoints(){
        this.points.add(new Point((int)this.point.getX() - 50,
                                  (int)this.point.getY() - 50));
        this.points.add(new Point((int)this.point.getX() + 50,
                                  (int)this.point.getY() - 50));
        this.points.add(new Point((int)this.point.getX() + 50,
                                  (int)this.point.getY() + 50));
        this.points.add(new Point((int)this.point.getX() - 50,
                                  (int)this.point.getY() + 50));
    }
}
