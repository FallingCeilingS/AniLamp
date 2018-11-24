import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Animator {
    public float MAX_LENGTH, MAX_WIDTH;
    public Vec3 previousPosition = new Vec3(0, 0, 0);
    public Vec3 currentPosition = new Vec3(0, 0, 0);
    public Vec3 direction = new Vec3(1, 0, 0);
    public Mat4 translateMatrix = new Mat4(1);
    public boolean ANIMATION  = false;

    private double getCurrentTime() {
        return System.currentTimeMillis() / 1000.0;
    }

    public Animator(float MAX_LENGTH, float MAX_WIDTH) {
        this.MAX_LENGTH = MAX_LENGTH;
        this.MAX_WIDTH = MAX_WIDTH;
    }

    public void generateRandomPosition() {
        double randomX = Math.random() * MAX_LENGTH - MAX_LENGTH / 2;
        double randomZ = Math.random() * MAX_WIDTH - MAX_WIDTH / 2;
        currentPosition = new Vec3((float) randomX, 0, (float) randomZ);
    }

    public Mat4 generateTranslateMatrix() {
        if (ANIMATION) {
            generateRandomPosition();
            ANIMATION = false;
            translateMatrix = Mat4Transform.translate(currentPosition.x - previousPosition.x, 0, currentPosition.z - previousPosition.z);
        }
        return translateMatrix;
    }

    public Mat4 jumpZ() {
        Mat4 jump_z = new Mat4(1);
        if (ANIMATION) {
            double currentTime = getCurrentTime();
            jump_z =  Mat4Transform.translate(0, 5 * (float) Math.sin(currentTime), 0);
        }
        return jump_z;
    }
}
