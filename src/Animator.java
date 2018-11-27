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
    public double lowerJointYCurrentRotateDegree;
    public double lowerJointRotateYVelocity = 0;
    private double lowerJointInitialDegree, upperJointInitialDegree;
    private double LOWER_PRESS_MAX_DELTA_DEGREE = 60;
    private double LOWER_STRETCH_MAX_DELTA_DEGREE = 30;
    private double UPPER_PRESS_MAX_DELTA_DEGREE = 60;
    private double UPPER_STRETCH_MAX_DELTA_DEGREE = 75;
    private double lowerPressTargetDegree;
    private double lowerStretchTargetDegree;
    public double lowerJointZCurrentRotateDegree;
    private double lowerJointRotateZVelocity = 1;
    private double upperJointRotateZVelocity = 1;
    private double upperPressTargetDegree;
    private double upperStretchTargetDegree;
    public double upperJointZCurrentRotateDegree;
    private double tmpMaxVelocity = 0;
    private double rotateYDegreeCount = 0;
    private double rotateZPressDegreeCount = 0;
    private double rotateZStretchDegreeCount = 0;
    private double ratio;
    private double jumpHorizonVelocity = 0.05;
    private double jumpVerticalVelocity = 1;
    public Mat4 previousTranslateMatrix = new Mat4(1);
    public Mat4 currentTranslateMatrix = new Mat4(1);
    public boolean ANIMATION_GENERATION = false;
    public boolean ANIMATION_PREP_Y_ROTATE = false;
    public boolean ANIMATION_PREP_PRESS = false;
    public boolean ANIMATION_PREP_STRETCH = false;
    public boolean ANIMATION_JUMP = false;
    public double startJumpTime;
    public double startPressTime;
    public double startStretchTime;

    private double getCurrentSecond() {
        return System.currentTimeMillis() / 1000.0;
    }

    public Animator(float MAX_LENGTH, float MAX_WIDTH, double lowerJointInitialDegree, double upperJointInitialDegree) {
        this.MAX_LENGTH = MAX_LENGTH;
        this.MAX_WIDTH = MAX_WIDTH;
        this.MAX_DISTANCE = (float) Math.sqrt(Math.pow((double) this.MAX_LENGTH, 2) + Math.pow((double) this.MAX_WIDTH, 2));
        this.lowerJointInitialDegree = lowerJointInitialDegree;
        this.upperJointInitialDegree = upperJointInitialDegree;
        this.lowerJointZCurrentRotateDegree = lowerJointInitialDegree;
        this.upperJointZCurrentRotateDegree = upperJointInitialDegree;
    }

    public void generateRandomTargetAngle() {
        if (ANIMATION_GENERATION) {
            generateRandomPosition();
//            previousTranslateMatrix = currentTranslateMatrix;
            currentTranslateMatrix = Mat4Transform.translate(currentPosition.x, 0, currentPosition.z);
            generateLowerJointYRotateDegree();
            ANIMATION_GENERATION = false;
            ANIMATION_PREP_Y_ROTATE = true;
        }
    }

    public void generateRandomPosition() {
        double randomX = Math.random() * MAX_LENGTH - MAX_LENGTH / 2;
        double randomZ = Math.random() * MAX_WIDTH - MAX_WIDTH / 2;
        currentPosition = new Vec3((float) randomX, 0, (float) randomZ);
        generatePressAngle();
    }

    public void generateLowerJointYRotateDegree() {
        if (ANIMATION_GENERATION) {
            System.out.println("previous pos  = " + previousPosition);
            System.out.println("current pos   = " + currentPosition);
//            System.out.println("previous dir  = " + previousDirection);
            currentDirection = Vec3.normalize(Vec3.subtract(currentPosition, previousPosition));
//            System.out.println("current dir n = " + Vec3.subtract(currentPosition, previousPosition));
            System.out.println("current dir   = " + currentDirection);
            double cosineDegree = Vec3.dotProduct(previousDirection, currentDirection);
//            System.out.println("cosine degree = " + cosineDegree);
            lowerJointDeltaRotateDegree = Math.toDegrees(Math.acos(cosineDegree));
            System.out.println("degree        = " + lowerJointDeltaRotateDegree);
            crossProduct = Vec3.crossProduct(previousDirection, currentDirection);
//            System.out.println("cross product = " + crossProduct);
        }
    }

    public void generatePressAngle() {
        ratio = Vec3.magnitude(Vec3.subtract(previousPosition, currentPosition)) / MAX_DISTANCE;
        System.out.println("ratio"  + ratio);
        lowerPressTargetDegree = LOWER_PRESS_MAX_DELTA_DEGREE * ratio + lowerJointInitialDegree;
        lowerStretchTargetDegree = lowerJointInitialDegree - LOWER_STRETCH_MAX_DELTA_DEGREE * ratio;
        upperPressTargetDegree = upperJointInitialDegree - UPPER_PRESS_MAX_DELTA_DEGREE * ratio;
        upperStretchTargetDegree = UPPER_STRETCH_MAX_DELTA_DEGREE * ratio + upperJointInitialDegree;
    }

    public double easeAnimation(double startTime, double count, double delta, double ratio) {
        double v;
        if (count <= delta / 1.98888888888) {
            v = (getCurrentSecond() - startTime) * ratio;
            tmpMaxVelocity = v;
        } else {
            v = 2.00002 * tmpMaxVelocity - (getCurrentSecond() - startTime) * ratio;
            if (v < 0) {
                v = 0.05;
            }
        }
        return v;
    }

    /**
     * this function will be executed 60 times per second so we do not need loop
     * @param startTime
     * @return void
     */
    public void updateLowerJointYRotateDegree(double startTime) {
        if (ANIMATION_PREP_Y_ROTATE) {
            if (rotateYDegreeCount <= lowerJointDeltaRotateDegree) {
                lowerJointRotateYVelocity = easeAnimation(startTime, rotateYDegreeCount, lowerJointDeltaRotateDegree, 2);
                rotateYDegreeCount = rotateYDegreeCount + lowerJointRotateYVelocity;
//                System.out.println("rotate y velocity                        = " + lowerJointRotateYVelocity);
                if (crossProduct.y >= 0) {
                    lowerJointYCurrentRotateDegree = lowerJointYCurrentRotateDegree + lowerJointRotateYVelocity;
                } else {
                    lowerJointYCurrentRotateDegree = lowerJointYCurrentRotateDegree - lowerJointRotateYVelocity;
                }
//                TODO: turning head
            } else {
                ANIMATION_PREP_Y_ROTATE = false;
                rotateYDegreeCount = 0;
                ANIMATION_PREP_PRESS = true;
                startPressTime = getCurrentSecond();
            }
//            System.out.println("rotate y degree                          = " + lowerJointYCurrentRotateDegree);
        }
    }

    public void lowerJointPress() {
        if (lowerJointZCurrentRotateDegree < lowerPressTargetDegree) {
//            lowerJointRotateZVelocity = easeAnimation(startPressTime, rotateZPressDegreeCount,LOWER_PRESS_MAX_DELTA_DEGREE * ratio, 2);
            rotateZPressDegreeCount = rotateZPressDegreeCount + lowerJointRotateZVelocity;
            lowerJointZCurrentRotateDegree = lowerJointZCurrentRotateDegree + lowerJointRotateZVelocity;
//            System.out.println("rotate z press velocity                  = " + lowerJointRotateZVelocity);
//            System.out.println("rotate z press degree                    = " + lowerJointZCurrentRotateDegree);
            System.out.println("count                                    = " + rotateZPressDegreeCount);
        } else {
            if (ANIMATION_PREP_PRESS) {
                ANIMATION_PREP_PRESS = false;
                ANIMATION_PREP_STRETCH = true;
                rotateZPressDegreeCount = 0;
                startStretchTime = getCurrentSecond();
            }
        }
    }

    public void lowerJointStretch() {
        if (lowerJointZCurrentRotateDegree > lowerStretchTargetDegree) {
//            lowerJointRotateZVelocity = easeAnimation(startStretchTime, rotateZStretchDegreeCount,LOWER_STRETCH_MAX_DELTA_DEGREE * ratio, 1);
            lowerJointRotateZVelocity = 1;
            rotateZStretchDegreeCount = rotateZStretchDegreeCount + lowerJointRotateZVelocity;
            lowerJointZCurrentRotateDegree = lowerJointZCurrentRotateDegree - lowerJointRotateZVelocity;
        } else {
            rotateZStretchDegreeCount = 0;
        }
    }

    public void upperJointPress() {
        if (upperJointZCurrentRotateDegree > upperPressTargetDegree) {
            upperJointZCurrentRotateDegree = upperJointZCurrentRotateDegree - lowerJointRotateZVelocity;
        }
    }

    public void upperJointStretch() {
        if (upperJointZCurrentRotateDegree < upperStretchTargetDegree) {
            upperJointZCurrentRotateDegree = upperJointZCurrentRotateDegree + lowerJointRotateZVelocity;
            if (!ANIMATION_JUMP) {
                if (upperJointZCurrentRotateDegree >= upperJointInitialDegree) {
                    ANIMATION_JUMP = true;
//                    ANIMATION_PREP_STRETCH = false;
                    startJumpTime = getCurrentSecond();
                }
            }
        } else {
            ANIMATION_PREP_STRETCH = false;
            rotateYDegreeCount = 0;
        }
    }

    public void updateJointJumpZRotateDegree() {
        if (ANIMATION_PREP_PRESS) {
            lowerJointPress();
            upperJointPress();
        } else if (ANIMATION_PREP_STRETCH) {
            lowerJointStretch();
            upperJointStretch();
        }
    }

    public void updateJump() {
        if (ANIMATION_JUMP) {
            previousPosition.x = previousPosition.x + (float) jumpHorizonVelocity * currentDirection.x;
            previousPosition.z = previousPosition.z + (float) jumpHorizonVelocity * currentDirection.z;
            double distance = jumpHorizonVelocity * (getCurrentSecond() - startJumpTime) * 60;
            previousPosition.y = - 0.25f * (float) distance * (float) (distance - MAX_DISTANCE * ratio);
            if (previousPosition.y <= 0) {
                previousPosition.y = 0;
            }
//            System.out.println("previous pos update" + previousPosition);
            previousTranslateMatrix = Mat4Transform.translate(new Vec3(previousPosition));
//            System.out.println("current time = " + startJumpTime);
//            System.out.println("time         = " + (getCurrentSecond() - startJumpTime));
//            System.out.println("previous translate matrix\n" + previousTranslateMatrix.toString());
            if (previousPosition.y == 0 && distance >= MAX_DISTANCE * ratio) {
                System.out.println("stop");
                ANIMATION_JUMP = false;
                previousPosition = currentPosition;
                previousDirection = currentDirection;
            }
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
