package gamep;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import gamep.GamePiece;

import java.nio.*;
import java.util.*;
import java.lang.Object;
import java.awt.Point;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

enum State{
    GAME_MENU,
    GAME_SCREEN_WAITING,
    GAME_SCREEN_MOVING,
    GAME_SCORES
}

public class Game {
    static int SCREEN_WIDTH = 800;
    static int SCREEN_HEIGHT = 600;
    static int dx = 0, dy = 0;
    static long window;
    static Random rng = new Random();
    
    State gameState;
    int width, height;
    boolean centerPieceIsMoving;
    ArrayList fixedPieces = new ArrayList();
    GameCenterPiece centerPiece;
    char solution;
    
    // This prevents our window from crashing later on.
    private static GLFWKeyCallback keyCallback;

    public Game(int width, int height){
        this.width = width;
        this.height = height;
    }
    
    public void setGameState(State gameState){
        this.gameState = gameState;
    }
    
    public ArrayList getFixedPieces(){
        return fixedPieces;
    }
    
    public int getWidth(){
        return this.width;
    }
    
    public int getHeight(){
        return this.height;
    }
    
    public static void initRendering(){
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
                throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will NOT be resizable

        // Create the window
        window = glfwCreateWindow(SCREEN_WIDTH, 
                                  SCREEN_HEIGHT, 
                                  "JOGUINEO", 
                                  NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (windowAux, key, scancode, action, mods) -> {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                        glfwSetWindowShouldClose(window, true); 
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        glfwShowWindow(window);   
        GL.createCapabilities();
    }
        
    public static Game initGame(){
        Game game = new Game(SCREEN_WIDTH, SCREEN_HEIGHT);
        game.setGameState(State.GAME_SCREEN_WAITING);
        game.centerPieceIsMoving = false;
        
        game.centerPiece = new GameCenterPiece(SCREEN_WIDTH/2, 
                                               SCREEN_HEIGHT/2, 
                                               Shape.SQUARE);
        
        game.fixedPieces.add(new GamePiece(SCREEN_WIDTH/2, 50, Shape.CIRCLE));
        game.fixedPieces.add(new GamePiece(SCREEN_WIDTH/2, SCREEN_HEIGHT - 50, Shape.TRIANGLE));
        game.fixedPieces.add(new GamePiece(SCREEN_WIDTH - 50, SCREEN_HEIGHT/2, Shape.INVERSE_TRIANGLE));
        game.fixedPieces.add(new GamePiece(50, SCREEN_HEIGHT/2, Shape.SQUARE));
        
        changeGamePieces(game);
        glfwSetKeyCallback(window, keyCallback = new InputHandler());
        
        return game;
    }
    
    public static void update(Game game){
        if(InputHandler.isKeyDown(GLFW_KEY_DOWN)){
            System.out.println("Down Pressed");
            
            if(game.gameState == State.GAME_SCREEN_WAITING){
                game.centerPiece.moveDown();
                game.setGameState(State.GAME_SCREEN_MOVING);
            }
        }
        if(InputHandler.isKeyDown(GLFW_KEY_UP)){
            System.out.println("Up Pressed");
            
            if(game.gameState == State.GAME_SCREEN_WAITING){
                game.centerPiece.moveUp();
                game.setGameState(State.GAME_SCREEN_MOVING);
            }
        }
        if(InputHandler.isKeyDown(GLFW_KEY_LEFT)){
            System.out.println("Left Pressed");
            
            if(game.gameState == State.GAME_SCREEN_WAITING){
                game.centerPiece.moveLeft();
                game.setGameState(State.GAME_SCREEN_MOVING);
            }
        }
        if(InputHandler.isKeyDown(GLFW_KEY_RIGHT)){
            System.out.println("Right Pressed");
            
            if(game.gameState == State.GAME_SCREEN_WAITING){
                game.centerPiece.moveRight();
                game.setGameState(State.GAME_SCREEN_MOVING);
            }
        }
    }
    
    public static void changeGamePieces(Game game){
        // Maintain insertion order
        Stack generated = new Stack();
        int next;
        
        while (generated.size() < 4)
        {
            next = rng.nextInt(Shape.values().length);
            // As we're adding to a set, this will automatically do a containment check
            if (generated.search(next) == -1){
                generated.push(next);
            }
        }
        
        boolean centerIsValid = false;
        do {
            next = rng.nextInt(Shape.values().length);
            if(generated.contains(next)){
                ((GamePiece)game.centerPiece).changeShape(Shape.values()[(int)next]);
                centerIsValid = true;
            }
        } while (centerIsValid == false);
        
        for(int j = 0; j < game.fixedPieces.size(); j++){
            ((GamePiece)game.fixedPieces.get(j)).changeShape(Shape.values()[(int)generated.pop()]);
            if(Shape.values()[next] == ((GamePiece)game.fixedPieces.get(j)).getShape()){
                switch (j){
                    case 0:
                        game.solution = 'u';
                        break;
                    case 1:
                        game.solution = 'd';
                        break;
                    case 2:
                        game.solution = 'r';
                        break;
                    case 3:
                        game.solution = 'l';
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    public static void drawCircle(Game game, int index, char type, int dx, int dy){
        GL11.glColor3f(1.0f, 0.8f, 0.0f);        
        GL11.glPushMatrix();
        GL11.glTranslated(dx, dy, 0);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        if(type == 'f'){
            for(int i = 0; i < ((GamePiece)game.fixedPieces.get(index)).points.size(); i++){
                GL11.glVertex2f((float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getX(), 
                                (float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getY());
            }
        } else if(type == 'c'){
            for(int i = 0; i < game.centerPiece.points.size(); i++){
                GL11.glVertex2f((float)((Point)(game.centerPiece.points.get(i))).getX(), 
                                (float)((Point)(game.centerPiece.points.get(i))).getY());
            }
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    
    public static void drawDiamond(Game game, int index, char type, int dx, int dy){
        GL11.glColor3f(1.0f, 0.8f, 0.0f);        
        GL11.glPushMatrix();
        GL11.glTranslated(dx, dy, 0);
        GL11.glBegin(GL11.GL_TRIANGLES);
        if(type == 'f'){
            for(int i = 0; i < ((GamePiece)game.fixedPieces.get(index)).points.size(); i++){
                GL11.glVertex2f((float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getX(), 
                                (float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getY());
            }
        } else if(type == 'c'){
            for(int i = 0; i < game.centerPiece.points.size(); i++){
                GL11.glVertex2f((float)((Point)(game.centerPiece.points.get(i))).getX(), 
                                (float)((Point)(game.centerPiece.points.get(i))).getY());
            }
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    
    public static void drawTriangle(Game game, int index, char type, int dx, int dy){
        GL11.glColor3f(1.0f, 0.8f, 0.0f);        
        GL11.glPushMatrix();
        GL11.glTranslated(dx, dy, 0);
        GL11.glBegin(GL11.GL_TRIANGLES);
        if(type == 'f'){
            for(int i = 0; i < ((GamePiece)game.fixedPieces.get(index)).points.size(); i++){
                GL11.glVertex2f((float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getX(), 
                                (float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getY());
            }
        } else if(type == 'c'){
            for(int i = 0; i < game.centerPiece.points.size(); i++){
                GL11.glVertex2f((float)((Point)(game.centerPiece.points.get(i))).getX(), 
                                (float)((Point)(game.centerPiece.points.get(i))).getY());
            }
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    
    public static void drawHourglass(Game game, int index, char type, int dx, int dy){
        GL11.glColor3f(1.0f, 0.8f, 0.0f);        
        GL11.glPushMatrix();
        GL11.glTranslated(dx, dy, 0);
        GL11.glBegin(GL11.GL_TRIANGLES);
        if(type == 'f'){
            for(int i = 0; i < ((GamePiece)game.fixedPieces.get(index)).points.size(); i++){
                GL11.glVertex2f((float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getX(), 
                                (float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getY());
            }
        } else if(type == 'c'){
            for(int i = 0; i < game.centerPiece.points.size(); i++){
                GL11.glVertex2f((float)((Point)(game.centerPiece.points.get(i))).getX(), 
                                (float)((Point)(game.centerPiece.points.get(i))).getY());
            }
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    
    public static void drawSquare(Game game, int index, char type, int dx, int dy){
        GL11.glColor3f(1.0f, 0.8f, 0.0f);        
        GL11.glPushMatrix();
        GL11.glTranslated(dx, dy, 0);
        GL11.glBegin(GL11.GL_QUADS);
        if(type == 'f'){
            for(int i = 0; i < ((GamePiece)game.fixedPieces.get(index)).points.size(); i++){
                GL11.glVertex2f((float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getX(), 
                                (float)((Point)((GamePiece)game.fixedPieces.get(index)).points.get(i)).getY());
            }
        } else if(type == 'c'){
            for(int i = 0; i < game.centerPiece.points.size(); i++){
                GL11.glVertex2f((float)((Point)(game.centerPiece.points.get(i))).getX(), 
                                (float)((Point)(game.centerPiece.points.get(i))).getY());
            }
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    
    public static void drawShape(Game game, Shape shape, int index, char type, int dx, int dy){
        if(type == 'f'){
           switch (shape){
            case SQUARE:
                drawSquare(game, index, 'f', 0, 0);
                break;
            case TRIANGLE:
                drawTriangle(game, index, 'f', 0, 0);
                break;
            case INVERSE_TRIANGLE:
                drawTriangle(game, index, 'f', 0, 0);
                break;
            case CIRCLE:
                drawCircle(game, index, 'f', 0, 0);
                break;
            case DIAMOND:
                drawDiamond(game, index, 'f', 0, 0);
                break;
            case HOURGLASS:
                drawHourglass(game, index, 'f', 0, 0);
                break;
            default:
                break;
            } 
        } else if(type == 'c'){
           switch (shape){
            case SQUARE:
                drawSquare(game, index, 'c', dx, dy);
                break;
            case TRIANGLE:
                drawTriangle(game, index, 'c', dx, dy);
                break;
            case INVERSE_TRIANGLE:
                drawTriangle(game, index, 'c', dx, dy);
                break;
            case CIRCLE:
                drawCircle(game, index, 'c', dx, dy);
                break;
            case DIAMOND:
                drawDiamond(game, index, 'c', dx, dy);
                break;
            case HOURGLASS:
                drawHourglass(game, index, 'c', dx, dy);
                break;
            default:
                break;
            }  
        }
    }
    
    public static void gameWaitingInput(Game game){
        drawShape(game, ((GamePiece)game.centerPiece).getShape(), 0, 'c', 0, 0);
        for(int j = 0; j < game.fixedPieces.size(); j++){
            drawShape(game, ((GamePiece)game.fixedPieces.get(j)).getShape(), j, 'f', 0, 0);
        }
    }
    
    public static void gameMoving(Game game){
        drawShape(game, ((GamePiece)game.centerPiece).getShape(), 0, 'c', dx, dy);
        for(int j = 0; j < game.fixedPieces.size(); j++){
            drawShape(game, ((GamePiece)game.fixedPieces.get(j)).getShape(), j, 'f', 0, 0);
        }
        
        if(game.centerPiece.direction == 'd'){
            dy += 8;
            
            if (game.centerPiece.point.y + dy >= SCREEN_HEIGHT - 50){
                dx = 0;
                dy = 0;
                
                if (game.solution == 'd'){
                    System.out.println("Acertou!!!");
                } else {
                    System.out.println("Errou!!");
                }
                
                changeGamePieces(game);
                game.centerPiece.stopMoving();
                game.setGameState(State.GAME_SCREEN_WAITING);
            }
        }
        else if(game.centerPiece.direction == 'u'){
            dy -= 8;
            
            if (game.centerPiece.point.y + dy <= 50){
                dx = 0;
                dy = 0;
                
                if (game.solution == 'u'){
                    System.out.println("Acertou!!!");
                } else {
                    System.out.println("Errou!!");
                }
                
                changeGamePieces(game);
                game.centerPiece.stopMoving();
                game.setGameState(State.GAME_SCREEN_WAITING);
            }
        }
        else if(game.centerPiece.direction == 'r'){
            dx += 10;
            
            if (game.centerPiece.point.x + dx >= SCREEN_WIDTH - 50){
                dx = 0;
                dy = 0;
                
                if (game.solution == 'r'){
                    System.out.println("Acertou!!!");
                } else {
                    System.out.println("Errou!!");
                }
                
                changeGamePieces(game);
                game.centerPiece.stopMoving();
                game.setGameState(State.GAME_SCREEN_WAITING);
            }
        }
        else if(game.centerPiece.direction == 'l'){
            dx -= 10;
            
            if (game.centerPiece.point.x + dx <= 50){
                dx = 0;
                dy = 0;
                
                if (game.solution == 'l'){
                    System.out.println("Acertou!!!");
                } else {
                    System.out.println("Errou!!");
                }
                
                changeGamePieces(game);
                game.centerPiece.stopMoving();
                game.setGameState(State.GAME_SCREEN_WAITING);
            }
        }
    }
    
    public static void gameLoop(Game game){
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Setting stuff up for loop
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Rendering Looop
        while ( !glfwWindowShouldClose(window) ) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
                
                switch (game.gameState){
                    case GAME_SCREEN_WAITING:
                        gameWaitingInput(game);
                        game.centerPieceIsMoving = false;
                        break;
                    case GAME_SCREEN_MOVING:
                        game.centerPieceIsMoving = true;
                        gameMoving(game);
                        break;
                    default:
                        break;
                }
                
                // Should be in the end of the loop
                glfwSwapBuffers(window); // swap the color buffers

                glfwPollEvents();
                update(game);
        }
        glDisable(GL_TEXTURE_2D);
    }
    
    public static void main(String[] args) {
	System.out.println("Crazy Pieces!");
        
        initRendering();
        Game gameInstance = initGame();
        gameLoop(gameInstance);
      
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
