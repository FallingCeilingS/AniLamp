import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Animator {
    private float MAX_LENGTH, MAX_WIDTH, MAX_DISTANCE;
    private Vec3 previousPosition = new Vec3(0, 0, 0);
    private Vec3 currentPosition = new Vec3(0, 0, 0);
    private Vec3 previousDirection = new Vec3(1, 0, 0);
    private Vec3 currentDirection = new Vec3(1, 0 ,0);
    private Vec3 crossProduct;
    public double lowerJointDeltaRotateDegree = 0;
    public double lowerJointCurrentRotateDegree;
    public double lowerJointRotateYVelocity = 0;
    public double tmpMaxVelocity = 0;
    public double rotateDegreeCount = 0;
    public Mat4 previousTranslateMatrix = new Mat4(1);
    public Mat4 currentTranslateMatrix = new Mat4(1);
    public boolean ANIMATION_GENERATION = false;
    public boolean ANIMATION_PREP_01 = false;
    public boolean ANIMATION_PREP_02 = false;
    public double currentTime;

    private double getCurrentSecond() {
        return System.currentTimeMillis() / 1000.0;
    }

    public Animator(float MAX_LENGTH, float MAX_WIDTH) {
        this.MAX_LENGTH = MAX_LENGTH;
        this.MAX_WIDTH = MAX_WIDTH;
        this.MAX_DISTANCE = (float) Math.sqrt(Math.pow((double) this.MAX_LENGTH, 2) + Math.pow((double) this.MAX_WIDTH, 2));
    }

    public void generateRandomTarget() {
        if (ANIMATION_GENERATION) {
            generateRandomPosition();
            previousTranslateMatrix = currentTranslateMatrix;
            currentTranslateMatrix = Mat4Transform.translate(currentPosition.x, 0, currentPosition.z);
            generateLowerJointYRotateDegree();
            ANIMATION_GENERATION = false;
            ANIMATION_PREP_01 = true;
        }
    }

    public void generateRandomPosition() {
        double randomX = Math.random() * MAX_LENGTH - MAX_LENGTH / 2;
        double randomZ = Math.random() * MAX_WIDTH - MAX_WIDTH / 2;
        currentPosition = new Vec3((float) randomX, 0, (float) randomZ);
    }

    public void generateLowerJointYRotateDegree() {
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
            lowerJointDeltaRotateDegree = Math.toDegrees(Math.acos(cosineDegree));
            System.out.println("degree        = " + lowerJointDeltaRotateDegree);
            crossProduct = Vec3.crossProduct(previousDirection, currentDirection);
            System.out.println("cross product = " + crossProduct);
            previousPosition = currentPosition;
            previousDirection = currentDirection;
        }
    }

    public double easeAnimation(double startTime, double delta, double ratio) {
        double v = 0;
        if (rotateDegreeCount <= delta / 1.98888888888) {
            v = (getCurrentSecond() - startTime) * ratio;
            tmpMaxVelocity = v;
        } else {
            v = 2.00002 * tmpMaxVelocity - (getCurrentSecond() - startTime) * ratio;
            if (v < 0) {
                v = 0.05;
            }
        }
        rotateDegreeCount = rotateDegreeCount + v;
        System.out.println("rotate velocity                    " + lowerJointRotateYVelocity);
        return v;
    }

    /**
     * this function will be executed 60 times per second so we do not need loop
     * @param startTime
     * @return
     */
    public void updateLowerJointYRotateDegree(double startTime) {
        if (ANIMATION_PREP_01) {
//            System.out.println("start time" + startTime);
            if (rotateDegreeCount <= lowerJointDeltaRotateDegree) {
                System.out.println("(getCurrentSecond() - startTime) = " + (getCurrentSecond() - startTime));
                lowerJointRotateYVelocity = easeAnimation(startTime, lowerJointDeltaRotateDegree, 2);
                if (crossProduct.y >= 0) {
                    lowerJointCurrentRotateDegree = lowerJointCurrentRotateDegree + lowerJointRotateYVelocity;
                } else {
                    lowerJointCurrentRotateDegree = lowerJointCurrentRotateDegree - lowerJointRotateYVelocity;
                }
                currentTime = getCurrentSecond();
//                TODO: turning head
            } else {
                ANIMATION_PREP_01 = false;
                rotateDegreeCount = 0;
                ANIMATION_PREP_02 = true;
            }
            System.out.println("rotate degree                    =    " + lowerJointCurrentRotateDegree);
        }
    }

    public void updateJointZRotateDegree() {
        if (ANIMATION_PREP_02) {

        }
    }

    public Mat4 jumpY() {
        Mat4 jump_z = new Mat4(1);
        if (ANIMATION_GENERATION) {
            double currentTime = getCurrentSecond();
            jump_z =  Mat4Transform.translate(0, 5 * (float) Math.sin(currentTime), 0);
        }
        return jump_z;
    }
}
