import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Anilamp extends JFrame {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
    private static GLCanvas glCanvas;
    private static GLEventListener glEventListener;
    private final FPSAnimator fpsAnimator;
    private Camera camera;

    public Anilamp(String textForTitleBar) {
        super(textForTitleBar);
        GLCapabilities glCapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
        glCanvas = new GLCanvas(glCapabilities);
        Camera camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
        glEventListener = new Anilamp_GLEventListener(camera);
        glCanvas.addGLEventListener(glEventListener);
        glCanvas.addKeyListener(new MyKeyboardInput(camera));
        glCanvas.addMouseMotionListener(new MyMouseInput(camera));
        getContentPane().add(glCanvas, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                fpsAnimator.stop();
                remove(glCanvas);
                dispose();
                System.exit(0);
            }
        });
        fpsAnimator = new FPSAnimator(glCanvas, 60);
        fpsAnimator.start();
    }

    public static void main(String[] args) {
        Anilamp aniLamp = new Anilamp("ANILAMP -- Final Scene");
        aniLamp.getContentPane().setPreferredSize(dimension);
        aniLamp.pack();
        aniLamp.setVisible(true);
    }
}
