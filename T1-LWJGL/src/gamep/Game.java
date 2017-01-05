package gamep;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import gamep.GamePiece;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.util.*;
import java.lang.Object;
import java.awt.Point;

import java.nio.*;

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
    State gameState;
    int width, height;
    
    public Game(int width, int height){
        this.width = width;
        this.height = height;
    }
    
    public int getWidth(){
        return this.width;
    }
    
    public int getHeight(){
        return this.height;
    }
    
    public static void main(String[] args) {
        Game gameInstance = new Game(SCREEN_WIDTH, SCREEN_HEIGHT);
	System.out.println("Crazy Pieces!");
        
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
        long window = glfwCreateWindow(SCREEN_WIDTH, 
                                  SCREEN_HEIGHT, 
                                  "Hello World!", 
                                  NULL, NULL);
        if ( window == NULL )
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
        
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 800, 0, 600, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        
        // Setting stuff up for loop
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        GamePiece testPiece = new GamePiece(50, 50, Shape.SQUARE);
        
        float x = 0, y = 0;
        
        // Rendering Looop
        while ( !glfwWindowShouldClose(window) ) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                glfwPollEvents();
                
                GL11.glColor3f(1.0f, 0.8f, 0.0f);        
                GL11.glPushMatrix();
                GL11.glBegin(GL11.GL_QUADS);
                for(int i = 0; i < testPiece.points.size(); i++){
                    GL11.glVertex2f((float)((Point)testPiece.points.get(i)).getX(), 
                                    (float)((Point)testPiece.points.get(i)).getY());
                }
                GL11.glEnd();
                GL11.glPopMatrix();
                
                // Should be in the end of the loop
                glfwSwapBuffers(window); // swap the color buffers
        }
        glDisable(GL_TEXTURE_2D);
        
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
