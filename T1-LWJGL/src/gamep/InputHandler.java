/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamep;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public class InputHandler extends GLFWKeyCallback{

	public static boolean[] keys = new boolean[10000];
	
	
	// The GLFWKeyCallback class is an abstract method that
	// can't be instantiated by itself and must instead be extended
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
            keys[key] = action != GLFW_RELEASE;
        }
	
	// Boolean method that returns true if a given key
	// is pressed.
	public static boolean isKeyDown(int keycode) {
            return keys[keycode];
	}
	
}