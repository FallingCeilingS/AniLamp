import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class MyMouseInput extends MouseMotionAdapter {
    private Point lastPoint;
    private Camera camera;

    public MyMouseInput(Camera camera) {
        this.camera = camera;
    }

    public void mouseDragged(MouseEvent event) {
        Point mouse = event.getPoint();
        float sensitivity = 0.001f;
        float dx = (float) (mouse.x - lastPoint.x) * sensitivity;
        float dy = (float) (mouse.y - lastPoint.y) * sensitivity;
        if (event.getModifiers() == MouseEvent.BUTTON1_MASK) {
            camera.updateYawPitch(dx, -dy);
        }
        lastPoint = mouse;
    }

    public void mouseMoved(MouseEvent event) {
        lastPoint = event.getPoint();
    }
}
