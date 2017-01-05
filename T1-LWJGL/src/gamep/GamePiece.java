package gamep;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import java.lang.Object;
import java.lang.Math;
import java.awt.Point;

enum Shape{
    SQUARE,
    TRIANGLE,
    INVERSE_TRIANGLE,
    CIRCLE,
    DIAMOND,
    HOURGLASS
    //PARALLELOGRAM;
}

public class GamePiece {
    Point point = new Point();
    Shape shape;
    ArrayList points = new ArrayList();
    static int radius = 35;
    
    public GamePiece(int x, int y, Shape shape){
        this.point.setLocation(x, y);
        this.shape = shape;
        updatePoints();
    }
    
    Shape getShape(){
        return this.shape;
    }
    
    void changeShape(Shape shape){
        this.shape = shape;
        updatePoints();
    }
    
    void updatePoints(){
        this.points.clear();
        
        switch (this.shape){
            case SQUARE:
                createSquarePoints();
                break;
            case TRIANGLE:
                createTrianglePoints();
                break;
            case INVERSE_TRIANGLE:
                createInverseTrianglePoints();
                break;
            case CIRCLE:
                createCirclePoints();
                break;
            case DIAMOND:
                createDiamondPoints();
                break;
            case HOURGLASS:
                createHourglassPoints();
                break;
            default:
                break;
        }      
    }
    
    void createSquarePoints(){
        this.points.add(new Point((int)this.point.getX() - radius,
                                  (int)this.point.getY() - radius));
        this.points.add(new Point((int)this.point.getX() + radius,
                                  (int)this.point.getY() - radius));
        this.points.add(new Point((int)this.point.getX() + radius,
                                  (int)this.point.getY() + radius));
        this.points.add(new Point((int)this.point.getX() - radius,
                                  (int)this.point.getY() + radius));
    }
    
    void createTrianglePoints(){
        this.points.add(new Point((int)this.point.getX() - radius,
                                  (int)this.point.getY() - radius));
        this.points.add(new Point((int)this.point.getX() + radius,
                                  (int)this.point.getY() - radius));
        this.points.add(new Point((int)this.point.getX(),
                                  (int)this.point.getY() + radius));
    }
    
    void createInverseTrianglePoints(){
        this.points.add(new Point((int)this.point.getX() + radius,
                                  (int)this.point.getY() + radius));
        this.points.add(new Point((int)this.point.getX() - radius,
                                  (int)this.point.getY() + radius));
        this.points.add(new Point((int)this.point.getX(),
                                  (int)this.point.getY() - radius));
    }
    
    void createCirclePoints(){
        int i;
	int triangleAmount = 40; //# of triangles used to draw circle
	
	//GLfloat radius = 0.8f; //radius
	float twicePi = (float)(2.0f * Math.PI);
	
        this.points.add(new Point((int)this.point.getX(),
                                  (int)this.point.getY()));
        for(i = 0; i <= triangleAmount; i++){
            this.points.add(new Point((int)(this.point.getX() + (radius * Math.cos(i * twicePi / triangleAmount))),
                                      (int)(this.point.getY() + (radius * Math.sin(i * twicePi / triangleAmount)))));
        }
    }
    
    void createDiamondPoints(){
        this.points.add(new Point((int)this.point.getX() - radius,
                                  (int)this.point.getY()));
        this.points.add(new Point((int)this.point.getX() + radius,
                                  (int)this.point.getY()));
        this.points.add(new Point((int)this.point.getX(),
                                  (int)this.point.getY() + radius));
        this.points.add(new Point((int)this.point.getX() - radius,
                                  (int)this.point.getY()));
        this.points.add(new Point((int)this.point.getX() + radius,
                                  (int)this.point.getY()));
        this.points.add(new Point((int)this.point.getX(),
                                  (int)this.point.getY() - radius));
    }
    
    void createHourglassPoints(){
        this.points.add(new Point((int)this.point.getX() - radius,
                                  (int)this.point.getY() - radius));
        this.points.add(new Point((int)this.point.getX() + radius,
                                  (int)this.point.getY() - radius));
        this.points.add(new Point((int)this.point.getX(),
                                  (int)this.point.getY() + radius));
        
        this.points.add(new Point((int)this.point.getX() + radius,
                                  (int)this.point.getY() + radius));
        this.points.add(new Point((int)this.point.getX() - radius,
                                  (int)this.point.getY() + radius));
        this.points.add(new Point((int)this.point.getX(),
                                  (int)this.point.getY() - radius));
    }
}
