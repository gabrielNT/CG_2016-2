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
    GAME_SCREEN,
    GAME_SCORES
}

public class Game {
    static int SCREEN_WIDTH = 800;
    static int SCREEN_HEIGHT = 600;
    static long window;
    
    static Random rng = new Random();
    State gameState;
    int width, height;
    ArrayList fixedPieces = new ArrayList();
    GameCenterPiece centerPiece = new GameCenterPiece(SCREEN_WIDTH/2, 
                                                      SCREEN_HEIGHT/2, 
                                                      Shape.SQUARE);
    
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
                                  "Hello World!", 
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
        game.setGameState(State.GAME_MENU);
        
        game.fixedPieces.add(new GamePiece(SCREEN_WIDTH/2, 50, Shape.CIRCLE));
        game.fixedPieces.add(new GamePiece(SCREEN_WIDTH/2, SCREEN_HEIGHT - 50, Shape.TRIANGLE));
        game.fixedPieces.add(new GamePiece(SCREEN_WIDTH - 50, SCREEN_HEIGHT/2, Shape.INVERSE_TRIANGLE));
        game.fixedPieces.add(new GamePiece(50, SCREEN_HEIGHT/2, Shape.SQUARE));
        
        glfwSetKeyCallback(window, keyCallback = new InputHandler());	
        
        return game;
    }
    
    public static void update(Game game){
        if(InputHandler.isKeyDown(GLFW_KEY_DOWN)){
            System.out.println("Down Pressed");
            
            // Maintain insertion order
            Stack generated = new Stack();
            while (generated.size() < Shape.values().length)
            {
                int next = rng.nextInt(Shape.values().length);
                // As we're adding to a set, this will automatically do a containment check
                if (generated.search(next) == -1){
                    generated.push(next);
                }
            }
            
            for(int j = 0; j < game.fixedPieces.size(); j++){
                    ((GamePiece)game.fixedPieces.get(j)).changeShape(Shape.values()[(int)generated.pop()]);
            }
        }
        if(InputHandler.isKeyDown(GLFW_KEY_UP)){
            System.out.println("Up Pressed");
        }
        if(InputHandler.isKeyDown(GLFW_KEY_LEFT)){
            System.out.println("Left Pressed");
        }
        if(InputHandler.isKeyDown(GLFW_KEY_RIGHT)){
            System.out.println("Right Pressed");
        }
    }
    
    public static void drawCircle(Game game, int index, char type){
        GL11.glColor3f(1.0f, 0.8f, 0.0f);        
        GL11.glPushMatrix();
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
    
    public static void drawTriangle(Game game, int index, char type){
        GL11.glColor3f(1.0f, 0.8f, 0.0f);        
        GL11.glPushMatrix();
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
    
    public static void drawSquare(Game game, int index, char type){
        GL11.glColor3f(1.0f, 0.8f, 0.0f);        
        GL11.glPushMatrix();
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
    
    public static void drawShape(Game game, Shape shape, int index, char type){
        if(type == 'f'){
           switch (shape){
            case SQUARE:
                drawSquare(game, index, 'f');
                break;
            case TRIANGLE:
                drawTriangle(game, index, 'f');
                break;
            case INVERSE_TRIANGLE:
                drawTriangle(game, index, 'f');
                break;
            case CIRCLE:
                drawCircle(game, index, 'f');
                break;
            default:
                break;
            } 
        } else if(type == 'c'){
           switch (shape){
            case SQUARE:
                drawSquare(game, index, 'c');
                break;
            case TRIANGLE:
                drawTriangle(game, index, 'c');
                break;
            case INVERSE_TRIANGLE:
                drawTriangle(game, index, 'c');
                break;
            case CIRCLE:
                drawCircle(game, index, 'c');
                break;
            default:
                break;
            }  
        }
    }
    
    public static void gameLoop(Game game){
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Setting stuff up for loop
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Rendering Looop
        while ( !glfwWindowShouldClose(window) ) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                drawShape(game, ((GamePiece)game.centerPiece).getShape(), 0, 'c');
                for(int j = 0; j < game.fixedPieces.size(); j++){
                    drawShape(game, ((GamePiece)game.fixedPieces.get(j)).getShape(), j, 'f');
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
