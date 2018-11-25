import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Animator {
    public float MAX_LENGTH, MAX_WIDTH;
    public Vec3 previousPosition = new Vec3(0, 0, 0);
    public Vec3 currentPosition = new Vec3(0, 0, 0);
    public Vec3 previousDirection = new Vec3(1, 0, 0);
    public Vec3 currentDirection = new Vec3(1, 0 ,0);
    public Vec3 crossProduct;
    public double lowerJointTargetRotateDegree = 0;
    public double lowerJointCurrentRotateDegree;
    public Mat4 previousTranslateMatrix = new Mat4(1);
    public Mat4 currentTranslateMatrix = new Mat4(1);
    public boolean ANIMATION_GENERATION = false;
    public boolean ANIMATION_PREP = false;

    private double getCurrentTime() {
        return System.currentTimeMillis() / 1000.0;
    }

    public Animator(float MAX_LENGTH, float MAX_WIDTH) {
        this.MAX_LENGTH = MAX_LENGTH;
        this.MAX_WIDTH = MAX_WIDTH;
    }

    public Mat4 generateRandomJump() {
        if (ANIMATION_GENERATION) {
            generateRandomPosition();
            previousTranslateMatrix = currentTranslateMatrix;
            currentTranslateMatrix = Mat4Transform.translate(currentPosition.x, 0, currentPosition.z);
            generateLowerJointRotateDegree();
            ANIMATION_GENERATION = false;
            ANIMATION_PREP = true;
        }
        return currentTranslateMatrix;
    }

    public void generateRandomPosition() {
        double randomX = Math.random() * MAX_LENGTH - MAX_LENGTH / 2;
        double randomZ = Math.random() * MAX_WIDTH - MAX_WIDTH / 2;
        currentPosition = new Vec3((float) randomX, 0, (float) randomZ);
    }

    public void generateLowerJointRotateDegree() {
        if (ANIMATION_GENERATION) {
            System.out.println("previous pos  = " + previousPosition);
            System.out.println("current pos   = " + currentPosition);
            System.out.println("previous dir  = " + previousDirection);
            currentDirection = Vec3.subtract(currentPosition, previousPosition);
//        currentDirection = Vec3.subtract(currentPosition, previousPosition);
            System.out.println("current dir n = " + Vec3.subtract(currentPosition, previousPosition));
            System.out.println("current dir   = " + currentDirection);
            double cosineDegree = Vec3.dotProduct(previousDirection, currentDirection) / (Vec3.magnitude(previousDirection) * Vec3.magnitude(currentDirection));
//        System.out.println("previous.x > 0?" + (previousDirection.x > 0));
            System.out.println("cosine degree = " + cosineDegree);
            lowerJointTargetRotateDegree = Math.toDegrees(Math.acos(cosineDegree));
            System.out.println("degree        = " + lowerJointTargetRotateDegree);
            crossProduct = Vec3.crossProduct(previousDirection, currentDirection);
            System.out.println("cross product = " + crossProduct);
            previousPosition = currentPosition;
            previousDirection = currentDirection;
        }
//        return lowerJointTargetRotateDegree;
    }

    /**
     * this function will be executed 60 times per second so we do not need loop
     * @param startTime
     * @return
     */
    public double updateRotateDegree(double startTime) {
        if (ANIMATION_PREP) {
//            System.out.println("start time" + startTime);

            double velocity = 0.5;
            if (velocity * (getCurrentTime() - startTime) * 60 <= lowerJointTargetRotateDegree) {
                System.out.println("velocity * (getCurrentTime() - startTime)" + velocity * (getCurrentTime() - startTime));
                if (crossProduct.y >= 0) {
                    lowerJointCurrentRotateDegree = lowerJointCurrentRotateDegree + velocity;
                } else {
                    lowerJointCurrentRotateDegree = lowerJointCurrentRotateDegree - velocity;
                }
            }

            System.out.println("rotate degree = " + lowerJointCurrentRotateDegree);


        }


        return lowerJointCurrentRotateDegree;
    }

    public Mat4 jumpY() {
        Mat4 jump_z = new Mat4(1);
        if (ANIMATION_GENERATION) {
            double currentTime = getCurrentTime();
            jump_z =  Mat4Transform.translate(0, 5 * (float) Math.sin(currentTime), 0);
        }
        return jump_z;
    }
}
