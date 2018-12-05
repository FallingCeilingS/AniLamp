/* This code is from exercise sheet written by Dr. Steve Maddock */

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyKeyboardInput extends KeyAdapter {
    private Camera camera;

    public MyKeyboardInput(Camera camera) {
        this.camera = camera;
    }

    public void keyPressed(KeyEvent event) {
        Camera.Movement movement = Camera.Movement.NO_MOVEMENT;
        switch (event.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                movement = Camera.Movement.LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                movement = Camera.Movement.RIGHT;
                break;
            case KeyEvent.VK_UP:
                movement = Camera.Movement.UP;
                break;
            case KeyEvent.VK_DOWN:
                movement = Camera.Movement.DOWN;
                break;
            case KeyEvent.VK_A:
                movement = Camera.Movement.FORWARD;
                break;
            case KeyEvent.VK_Z:
                movement = Camera.Movement.BACK;
                break;
        }
        camera.keyboardInput(movement);
    }
}
