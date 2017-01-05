import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import utils.lwjgl.graphic.Texture;

public class Example {
    /** position of quad */
    float x = 0, y = 0;
    /** angle of quad rotation */
    float rotation = (float)45;

    /** time at last frame */
    long lastFrame;

    /** frames per second */
    int fps;
    /** last fps time */
    long lastFPS;

    // The window handle
    private long window;

    public void run() {
            System.out.println("Hello LWJGL " + Version.getVersion() + "!");

            init();
            loop();

            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            // Terminate GLFW and free the error callback
            glfwTerminate();
            glfwSetErrorCallback(null).free();
    }

    private void init() {
            // Setup an error callback. The default implementation
            // will print the error message in System.err.
            GLFWErrorCallback.createPrint(System.err).set();

            // Initialize GLFW. Most GLFW functions will not work before doing this.
            if ( !glfwInit() )
                    throw new IllegalStateException("Unable to initialize GLFW");

            // Configure GLFW
            glfwDefaultWindowHints(); // optional, the current window hints are already the default
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

            // Create the window
            window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
            if ( window == NULL )
                    throw new RuntimeException("Failed to create the GLFW window");

            // Setup a key callback. It will be called every time a key is pressed, repeated or released.
            glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
                    if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
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

            // Make the OpenGL context current
            glfwMakeContextCurrent(window);
            // Enable v-sync
            glfwSwapInterval(1);

            // Make the window visible
            glfwShowWindow(window);
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GL11.glOrtho(0, 800, 0, 600, 1, -1);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                
		// Set the clear color
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

                Texture texture = Texture.loadTexture("C:\\Users\\gabri\\Documents\\NetBeansProjects\\T1-LWJGL\\t.png");
                
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
                        
                        // Texture stuff
                        glEnable(GL_TEXTURE_2D);
                        glBindTexture(GL_TEXTURE_2D, texture.getId());
                        
                        GL11.glColor3f(1.0f, 1.0f, 1.0f);
                
                         // draw quad
                        GL11.glPushMatrix();
                            GL11.glTranslated(150, 100, 0);
                            //GL11.glTranslatef(x, y, 0);
                            //GL11.glRotatef(rotation, 0f, 0f, 1f);
                            //GL11.glTranslatef(-x, -y, 0);

                            GL11.glBegin(GL11.GL_QUADS);
                                glTexCoord2f(0.0f, 0.0f);
                                GL11.glVertex2f(x, y);
                                glTexCoord2f(1.0f, 0.0f);
                                GL11.glVertex2f(x + 100, y);
                                glTexCoord2f(1.0f, 1.0f);
                                GL11.glVertex2f(x + 100, y + 100);
                                glTexCoord2f(0.0f, 1.0f);
                                GL11.glVertex2f(x, y + 100);
                            GL11.glEnd();
                        GL11.glPopMatrix();

                        GL11.glColor3f(0.5f, 1.0f, 1.0f);
                
                        glDisable(GL_TEXTURE_2D);
                        
                         // draw quad
                        GL11.glPushMatrix();
                            //GL11.glTranslated(500, 500, 0);
                            //GL11.glTranslatef(x, y, 0);
                            //GL11.glRotatef(rotation, 0f, 0f, 1f);
                            //GL11.glTranslatef(-x, -y, 0);

                            GL11.glBegin(GL11.GL_QUADS);
                                GL11.glVertex2f(x + 600, y + 400);
                                GL11.glColor3f(0.5f, 0.0f, 0.9f);
                                GL11.glVertex2f(x + 800, y + 400);
                                GL11.glColor3f(0.5f, 0.7f, 0.8f);
                                GL11.glVertex2f(x + 800, y + 600);
                                GL11.glColor3f(0.5f, 0.7f, 0.0f);
                                GL11.glVertex2f(x + 600, y + 600);
                            GL11.glEnd();
                        GL11.glPopMatrix();
                        
                        glfwSwapBuffers(window); // swap the color buffers
                }
                
                        glDisable(GL_TEXTURE_2D);
	}

//	public static void main(String[] args) {
//		new Example().run();
//                System.out.println("Working Directory = " +
//              System.getProperty("user.dir"));
//	}

}