import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Animator {
    private float MAX_LENGTH, MAX_WIDTH, MAX_DISTANCE;
    private double lowerJointZInitialDegree,
            upperJointZInitialDegree,
            lowerJointYInitialDegree,
            upperJointYInitialDegree,
            headJointZInitialDegree,
            headJointYInitialDegree,
            LAMP_BASE_LENGTH;

    private Vec3 previousPosition = new Vec3(0, 0, 0);
    private Vec3 currentPosition = new Vec3(0, 0, 0);
    private Vec3 previousDirection = new Vec3(1, 0, 0);
    private Vec3 currentDirection = new Vec3(1, 0, 0);
    private Vec3 crossProduct;

    public Mat4 previousTranslateMatrix = new Mat4(1);
    public Mat4 currentTranslateMatrix = new Mat4(1);

    private double lowerJointDeltaRotateDegree = 0;
    private double LOWER_PRESS_MAX_DELTA_DEGREE = 60;
    private double LOWER_STRETCH_MAX_DELTA_DEGREE = 45;
    private double UPPER_PRESS_MAX_DELTA_DEGREE = 60;
    private double UPPER_STRETCH_MAX_DELTA_DEGREE = 75;
    private double HEAD_UP_MAX_DELTA_DEGREE = 90;

    private double lowerPressTargetDegree;
    private double lowerStretchTargetDegree;
    private double upperPressTargetDegree;
    private double upperStretchTargetDegree;
    private double upperJointYTargetDegree;
    private double headJointZTargetDegree;
    private double headJointYTargetDegree;
    private double rotateYDegreeCount = 0;

    public double lowerJointZCurrentRotateDegree;
    public double upperJointZCurrentRotateDegree;
    public double lowerJointYCurrentRotateDegree = 0;
    public double upperJointYCurrentRotateDegree;
    public double headJointZCurrentRotateDegree;
    public double headJointYCurrentRotateDegree;

    private double lowerJointRotateZVelocity = 2.4;
    private double upperJointRotateZVelocity = 2.4;
    private double lowerJointRotateYVelocity = 0;
    private double upperJointRotateYVelocity = 1;
    private double headJointRotateZVelocity = 2;
    private double headJointRotateYVelocity = 2;
    private double tmpMaxVelocity = 0;
    private double jumpHorizonVelocity = 0.24;

    private double ratio;
    private double postLowerPressRatio = 0.6;
    private double postLowerStretchRatio = 0.8;

    public boolean ANIMATION_GENERATION = false;
    private boolean ANIMATION_PREP_Y_ROTATE = false;
    private boolean ANIMATION_PREP_PRESS = false;
    private boolean ANIMATION_PREP_STRETCH = false;
    private boolean ANIMATION_JUMP = false;
    private boolean ANIMATION_POST_PRESS = false;
    private boolean ANIMATION_POST_STRETCH = false;
    public boolean ANIMATION_RANDOM_GENERATE = false;
    private boolean ANIMATION_RANDOM_MOTION = false;
    private boolean ANIMATION_RANDOM_LOWER_Z_PRESS = false;
    private boolean ANIMATION_RANDOM_LOWER_Z_STRETCH = false;
    private boolean ANIMATION_RANDOM_UPPER_Z_PRESS = false;
    private boolean ANIMATION_RANDOM_UPPER_Z_STRETCH = false;

    private double startJumpTime;

    private double[] obj_1_range;
    private double[] range1;
    private double[] obj_2_range;
    private double[] range2;

    /**
     * some variables that never used
     */
    private double lowerRotateZPressDegreeCount = 0;
    private double upperRotateZPressDegreeCount = 0;
    private double lowerRotateZStretchDegreeCount = 0;
    private double upperRotateZStretchDegreeCount = 0;
    private double postLowerPressDeltaDegree;
    private double postUpperPressDeltaDegree;
    private double postLowerStretchDeltaDegree;
    private double postUpperStretchDeltaDegree;
    private boolean ANIMATION_END = false;
    private double startPressTime;
    private double startStretchTime;

    private double getCurrentSecond() {
        return System.currentTimeMillis() / 1000.0;
    }

    public Animator(
            float MAX_LENGTH,
            float MAX_WIDTH,
            double lowerJointZInitialDegree,
            double upperJointZInitialDegree,
            double lowerJointYInitialDegree,
            double upperJointYInitialDegree,
            double headJointZInitialDegree,
            double headJointYInitialDegree,
            double LAMP_BASE_LENGTH,
            double[] obj_1_range,
            double[] obj_2_range
    ) {
        this.MAX_LENGTH = MAX_LENGTH;
        this.MAX_WIDTH = MAX_WIDTH;
        this.MAX_DISTANCE = (float) Math.sqrt(
                Math.pow((double) this.MAX_LENGTH, 2) + Math.pow((double) this.MAX_WIDTH, 2)
        );
        this.lowerJointZInitialDegree = lowerJointZInitialDegree;
        this.upperJointZInitialDegree = upperJointZInitialDegree;
        this.lowerJointYInitialDegree = lowerJointYInitialDegree;
        this.upperJointYInitialDegree = upperJointYInitialDegree;
        this.headJointZInitialDegree = headJointZInitialDegree;
        this.headJointYInitialDegree = headJointYInitialDegree;

        this.lowerJointZCurrentRotateDegree = lowerJointZInitialDegree;
        this.upperJointZCurrentRotateDegree = upperJointZInitialDegree;
        this.headJointZCurrentRotateDegree = headJointZInitialDegree;
        this.headJointYCurrentRotateDegree = headJointYInitialDegree;
        this.LAMP_BASE_LENGTH = LAMP_BASE_LENGTH;

        this.obj_1_range = obj_1_range;
        this.range1 = generateRange(this.obj_1_range);
        this.obj_2_range = obj_2_range;
        this.range2 = generateRange(this.obj_2_range);
    }

    private double[] generateRange(double[] obj_range) {
        return new double[] {
                obj_range[0] - 0.5 * obj_range[2] - LAMP_BASE_LENGTH,
                obj_range[0] + 0.5 * obj_range[2] + LAMP_BASE_LENGTH,
                obj_range[1] - 0.5 * obj_range[3] - LAMP_BASE_LENGTH,
                obj_range[1] + 0.5 * obj_range[3] + LAMP_BASE_LENGTH
        };
    }

    public void generateRandomPose() {
        if (ANIMATION_RANDOM_GENERATE) {
            double randomLowerZ = Math.random() * 60;
            double randomUpperZ = Math.random() * (-90) - 30;
            double randomUpperY = Math.random() * 180 - 90;
            double randomHeadJY = Math.random() * 120 - 60;
            double randomHeadJZ = Math.random() * 90;
            System.out.println("random lower rotate z" + randomLowerZ);
            System.out.println("random upper rotate z" + randomUpperZ);
            System.out.println("random upper rotate y" + randomUpperY);
            System.out.println("random head joint rotate y" + randomHeadJY);
            if (lowerJointZCurrentRotateDegree <= randomLowerZ) {
                lowerPressTargetDegree = randomLowerZ;
                ANIMATION_RANDOM_MOTION = true;
                ANIMATION_RANDOM_LOWER_Z_PRESS = true;
            } else {
                lowerStretchTargetDegree = randomLowerZ;
                ANIMATION_RANDOM_MOTION = true;
                ANIMATION_RANDOM_LOWER_Z_STRETCH = true;
            }
            if (upperJointZCurrentRotateDegree <= randomUpperZ) {
                upperStretchTargetDegree = randomUpperZ;
                ANIMATION_RANDOM_UPPER_Z_STRETCH = true;
            } else {
                upperPressTargetDegree = randomUpperZ;
                ANIMATION_RANDOM_UPPER_Z_PRESS = true;
            }
            upperJointYTargetDegree = randomUpperY;
            headJointYTargetDegree = randomHeadJY;
            headJointZTargetDegree = randomHeadJZ;
            ANIMATION_RANDOM_GENERATE = false;
        }
    }

    public void generateLowerJointRandomMotion() {
        if (ANIMATION_RANDOM_MOTION) {
            lowerJointRotateZVelocity = 0.5;
            upperJointRotateZVelocity = 0.5;
            if (ANIMATION_RANDOM_LOWER_Z_PRESS) {
                System.out.println("lower press");
                lowerJointPress();
            } else if (ANIMATION_RANDOM_LOWER_Z_STRETCH) {
                System.out.println("lower stretch");
                lowerJointStretch();
            }
            if (ANIMATION_RANDOM_UPPER_Z_STRETCH) {
                System.out.println("upper stretch");
                upperJointStretch();
            } else if (ANIMATION_RANDOM_UPPER_Z_PRESS) {
                System.out.println("upper press");
                upperJointPress();
            }
            System.out.println("upper rotate");
            upperJointYRotate();
            System.out.println("head y rotate");
            headJointYRotate();
            System.out.println("head z rotate");
            headJointZRotate();
        }
    }

    private void upperJointYRotate() {
        if (upperJointYCurrentRotateDegree >= upperJointYTargetDegree) {
            upperJointYCurrentRotateDegree = upperJointYCurrentRotateDegree - upperJointRotateYVelocity;
        } else {
            upperJointYCurrentRotateDegree = upperJointYCurrentRotateDegree + upperJointRotateYVelocity;
        }
    }

    private void headJointYRotate() {
        if (headJointYCurrentRotateDegree >= headJointYTargetDegree) {
            headJointYCurrentRotateDegree = headJointYCurrentRotateDegree - headJointRotateYVelocity;
        } else {
            headJointYCurrentRotateDegree = headJointYCurrentRotateDegree + headJointRotateYVelocity;
        }
    }

    private void headJointZRotate() {
        if (headJointZCurrentRotateDegree >= headJointZTargetDegree) {
            headJointZCurrentRotateDegree = headJointZCurrentRotateDegree - headJointRotateZVelocity;
        } else {
            headJointZCurrentRotateDegree = headJointZCurrentRotateDegree + headJointRotateZVelocity;
        }
    }

    public void resetRandomPose() {
        lowerJointZCurrentRotateDegree = lowerJointZInitialDegree;
        upperJointZCurrentRotateDegree = upperJointZInitialDegree;
        upperJointYCurrentRotateDegree = upperJointYInitialDegree;
        headJointZCurrentRotateDegree = headJointZInitialDegree;
        headJointYCurrentRotateDegree = headJointYInitialDegree;
        lowerJointRotateZVelocity = 2.4;
        upperJointRotateZVelocity = 2.4;
        headJointRotateZVelocity = 2;
    }

    public void generateRandomTargetAngle() {
        if (ANIMATION_GENERATION) {
            generateRandomPosition();
            currentTranslateMatrix = Mat4Transform.translate(currentPosition.x, 0, currentPosition.z);
            generateLowerJointYRotateDegree();
            ANIMATION_GENERATION = false;
            ANIMATION_PREP_Y_ROTATE = true;
        }
    }

    private void generateRandomPosition() {
        double randomX = Math.random() * (MAX_LENGTH - LAMP_BASE_LENGTH) - (MAX_LENGTH - LAMP_BASE_LENGTH) / 2;
        double randomZ = Math.random() * (MAX_WIDTH - LAMP_BASE_LENGTH) - (MAX_WIDTH - LAMP_BASE_LENGTH) / 2;
        while (judgeRange(range1, randomX, randomZ) | judgeRange(range2, randomX, randomZ)) {
            randomX = Math.random() * (MAX_LENGTH - LAMP_BASE_LENGTH) - (MAX_LENGTH - LAMP_BASE_LENGTH) / 2;
            randomZ = Math.random() * (MAX_WIDTH - LAMP_BASE_LENGTH) - (MAX_WIDTH - LAMP_BASE_LENGTH) / 2;
        }
        currentPosition = new Vec3((float) randomX, 0, (float) randomZ);
        generateAngles();
    }

    private boolean judgeRange(double[] range, double randomX, double randomZ) {
        return (randomX >= range[0] & randomX <= range[1] & randomZ >= range[2] & randomZ<= range[3]);
    }

    private void generateLowerJointYRotateDegree() {
        if (ANIMATION_GENERATION) {
            ANIMATION_END = false;
//            System.out.println("previous pos  = " + previousPosition);
//            System.out.println("current pos   = " + currentPosition);
//            System.out.println("previous dir  = " + previousDirection);
            currentDirection = Vec3.normalize(Vec3.subtract(currentPosition, previousPosition));
//            System.out.println("current dir   = " + currentDirection);
            double cosineDegree = Vec3.dotProduct(previousDirection, currentDirection);
//            System.out.println("cosine degree = " + cosineDegree);
            lowerJointDeltaRotateDegree = Math.toDegrees(Math.acos(cosineDegree));
//            System.out.println("degree        = " + lowerJointDeltaRotateDegree);
            crossProduct = Vec3.crossProduct(previousDirection, currentDirection);
//            System.out.println("cross product = " + crossProduct);
        }
    }

    private void generateAngles() {
        ratio = Vec3.magnitude(Vec3.subtract(previousPosition, currentPosition)) / MAX_DISTANCE;
        lowerPressTargetDegree = LOWER_PRESS_MAX_DELTA_DEGREE * ratio + lowerJointZInitialDegree;
        lowerStretchTargetDegree = lowerJointZInitialDegree - LOWER_STRETCH_MAX_DELTA_DEGREE * ratio;
        upperPressTargetDegree = upperJointZInitialDegree - UPPER_PRESS_MAX_DELTA_DEGREE * ratio;
        upperStretchTargetDegree = UPPER_STRETCH_MAX_DELTA_DEGREE * ratio + upperJointZInitialDegree;
        headJointZTargetDegree = HEAD_UP_MAX_DELTA_DEGREE * ratio + headJointZInitialDegree;
    }

    private double easeAnimation(double startTime, double count, double delta, double ratio) {
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
     *
     * @param startTime
     */
    public void updateLowerJointYRotateDegree(double startTime) {
        if (ANIMATION_PREP_Y_ROTATE) {
            if (rotateYDegreeCount <= lowerJointDeltaRotateDegree) {
                lowerJointRotateYVelocity = easeAnimation(
                        startTime, rotateYDegreeCount, lowerJointDeltaRotateDegree, 5
                );
                rotateYDegreeCount = rotateYDegreeCount + lowerJointRotateYVelocity;
                if (crossProduct.y >= 0) {
                    lowerJointYCurrentRotateDegree = lowerJointYCurrentRotateDegree + lowerJointRotateYVelocity;
                } else {
                    lowerJointYCurrentRotateDegree = lowerJointYCurrentRotateDegree - lowerJointRotateYVelocity;
                }
                headUp();
            } else {
                ANIMATION_PREP_Y_ROTATE = false;
                rotateYDegreeCount = 0;
                ANIMATION_PREP_PRESS = true;
                startPressTime = getCurrentSecond();
            }
        }
    }

    private void lowerJointPress() {
        if (lowerJointZCurrentRotateDegree < lowerPressTargetDegree) {
            /*
             * ease in/out
             * not used but keep codes in comments
             *             if (ANIMATION_PREP_PRESS) {
             *                 lowerJointRotateZVelocity = easeAnimation(startPressTime, lowerRotateZPressDegreeCount,LOWER_PRESS_MAX_DELTA_DEGREE * ratio, 2);
             *
             *             } else if (ANIMATION_POST_PRESS) {
             *                 lowerJointRotateZVelocity = easeAnimation(startPressTime, lowerRotateZPressDegreeCount,postLowerPressDeltaDegree, 2);
             *             }
             *             lowerRotateZPressDegreeCount = lowerRotateZPressDegreeCount + lowerJointRotateZVelocity;
             */
            lowerJointZCurrentRotateDegree = lowerJointZCurrentRotateDegree + lowerJointRotateZVelocity;
        } else {
            if (ANIMATION_PREP_PRESS) {
                ANIMATION_PREP_PRESS = false;
                ANIMATION_PREP_STRETCH = true;
                lowerRotateZPressDegreeCount = 0;
                lowerRotateZStretchDegreeCount = 0;
                startStretchTime = getCurrentSecond();
            } else if (ANIMATION_RANDOM_LOWER_Z_PRESS) {
                ANIMATION_RANDOM_LOWER_Z_PRESS = false;
                ANIMATION_RANDOM_MOTION = false;
            }
        }
    }

    private void lowerJointStretch() {
        if (lowerJointZCurrentRotateDegree > lowerStretchTargetDegree) {
            /*
             * ease in/out
             * not used but keep codes in comments
             *             if (ANIMATION_PREP_STRETCH) {
             *                 lowerJointRotateZVelocity = easeAnimation(startStretchTime, lowerRotateZStretchDegreeCount,LOWER_STRETCH_MAX_DELTA_DEGREE * ratio, 2);
             *
             *             } else if (ANIMATION_POST_STRETCH) {
             *                 lowerJointRotateZVelocity = easeAnimation(startStretchTime, lowerRotateZPressDegreeCount, postLowerStretchDeltaDegree, 2);
             *             }
             *             lowerRotateZStretchDegreeCount = lowerRotateZStretchDegreeCount + lowerJointRotateZVelocity;
             */
            lowerJointZCurrentRotateDegree = lowerJointZCurrentRotateDegree - lowerJointRotateZVelocity;
        } else {
            if (ANIMATION_PREP_STRETCH || ANIMATION_POST_STRETCH) {
                lowerRotateZPressDegreeCount = 0;
                lowerRotateZStretchDegreeCount = 0;
            } else if (ANIMATION_RANDOM_LOWER_Z_STRETCH) {
                ANIMATION_RANDOM_LOWER_Z_STRETCH = false;
                ANIMATION_RANDOM_MOTION = false;
            }
        }
    }

    private void upperJointPress() {
        if (upperJointZCurrentRotateDegree > upperPressTargetDegree) {
            /*
             * ease in/out
             * not used but keep codes in comments
             *             if (ANIMATION_PREP_PRESS) {
             *                 upperJointRotateZVelocity = easeAnimation(startPressTime, upperRotateZPressDegreeCount, UPPER_PRESS_MAX_DELTA_DEGREE * ratio, 2);
             *             } else if (ANIMATION_POST_PRESS) {
             *                 upperJointRotateZVelocity = easeAnimation(startPressTime, upperRotateZPressDegreeCount, postUpperPressDeltaDegree, 2);
             *             }
             *             upperRotateZPressDegreeCount = upperRotateZPressDegreeCount + upperJointRotateZVelocity;
             */
            upperJointZCurrentRotateDegree = upperJointZCurrentRotateDegree - upperJointRotateZVelocity;
        } else {
            if (ANIMATION_POST_PRESS) {
                ANIMATION_POST_PRESS = false;
                ANIMATION_POST_STRETCH = true;
                upperRotateZPressDegreeCount = 0;
                upperRotateZStretchDegreeCount = 0;
                startStretchTime = getCurrentSecond();
            } else if (ANIMATION_RANDOM_UPPER_Z_PRESS) {
                ANIMATION_RANDOM_UPPER_Z_PRESS = false;
            }
        }
    }

    private void upperJointStretch() {
        if (upperJointZCurrentRotateDegree < upperStretchTargetDegree) {
            if (ANIMATION_PREP_STRETCH) {
                /*
                 * ease in/out
                 * not used but keep codes in comments
                 *                 System.out.println("prep stretch");
                 *                 upperJointRotateZVelocity = easeAnimation(startStretchTime, upperRotateZStretchDegreeCount, UPPER_STRETCH_MAX_DELTA_DEGREE * ratio, 2);
                 */
                upperJointRotateZVelocity = 2.4;
            } else if (ANIMATION_POST_STRETCH) {
                /*
                 * ease in/out
                 * not used but keep codes in comments
                 *                 System.out.println("post stretch");
                 *                 upperJointRotateZVelocity = easeAnimation(startStretchTime, upperRotateZStretchDegreeCount, postUpperPressDeltaDegree, 2);
                 *                 upperRotateZStretchDegreeCount = upperRotateZStretchDegreeCount + upperJointRotateZVelocity;
                 */
                upperJointRotateZVelocity = 4;
            }
            upperJointZCurrentRotateDegree = upperJointZCurrentRotateDegree + upperJointRotateZVelocity;
            if (!ANIMATION_JUMP && ANIMATION_PREP_STRETCH) {
                System.out.println("jump!");
                if (upperJointZCurrentRotateDegree >= upperJointZInitialDegree) {
                    ANIMATION_JUMP = true;
                    startJumpTime = getCurrentSecond();
                }
            }
        } else {
            if (ANIMATION_PREP_STRETCH) {
                ANIMATION_PREP_STRETCH = false;
                rotateYDegreeCount = 0;
                upperRotateZPressDegreeCount = 0;
                upperRotateZStretchDegreeCount = 0;
            } else if (ANIMATION_POST_STRETCH) {
                ANIMATION_POST_STRETCH = false;
                ANIMATION_END = true;
            } else if (ANIMATION_RANDOM_UPPER_Z_STRETCH) {
                ANIMATION_RANDOM_UPPER_Z_STRETCH = false;
                ANIMATION_RANDOM_MOTION = false;
            }
        }
    }

    private void headUp() {
        if (headJointZCurrentRotateDegree <= headJointZTargetDegree) {
            System.out.println("up");
            headJointZCurrentRotateDegree = headJointZCurrentRotateDegree + headJointRotateZVelocity;
        }
    }

    private void headDown() {
        if (headJointZCurrentRotateDegree >= headJointZInitialDegree) {
            System.out.println("down");
            headJointZCurrentRotateDegree = headJointZCurrentRotateDegree - headJointRotateZVelocity;
        }
    }

    public void updateJointJumpZRotateDegree() {
        if (ANIMATION_PREP_PRESS) {
            lowerJointPress();
            upperJointPress();
        } else if (ANIMATION_PREP_STRETCH) {
            lowerJointStretch();
            upperJointStretch();
        } else if (ANIMATION_POST_PRESS) {
            lowerJointPress();
            upperJointPress();
            headDown();
        } else if (ANIMATION_POST_STRETCH) {
            lowerStretchTargetDegree = lowerJointZInitialDegree;
            lowerJointStretch();
            upperStretchTargetDegree = upperJointZInitialDegree;
            upperJointStretch();
        }
    }

    public void updateJump() {
        if (ANIMATION_JUMP) {
            previousPosition.x = previousPosition.x + (float) jumpHorizonVelocity * currentDirection.x;
            previousPosition.z = previousPosition.z + (float) jumpHorizonVelocity * currentDirection.z;
            double distance = jumpHorizonVelocity * (getCurrentSecond() - startJumpTime) * 60;
            previousPosition.y = -0.15f * (float) distance * (float) (distance - MAX_DISTANCE * ratio);
            if (previousPosition.y <= 0) {
                previousPosition.y = 0;
            }
//            System.out.println("previous pos update" + previousPosition);
            previousTranslateMatrix = Mat4Transform.translate(new Vec3(previousPosition));
//            System.out.println("current time = " + startJumpTime);
//            System.out.println("time         = " + (getCurrentSecond() - startJumpTime));
//            System.out.println("previous translate matrix\n" + previousTranslateMatrix.toString());
            if (previousPosition.y == 0 && distance >= MAX_DISTANCE * ratio) {
                System.out.println("stop!");
                ANIMATION_JUMP = false;
                ANIMATION_POST_PRESS = true;
                startPressTime = getCurrentSecond();
                lowerRotateZPressDegreeCount = 0;
                lowerRotateZStretchDegreeCount = 0;
                upperRotateZPressDegreeCount = 0;
                upperRotateZStretchDegreeCount = 0;
                lowerPressTargetDegree =
                        postLowerPressRatio * LOWER_PRESS_MAX_DELTA_DEGREE * ratio + lowerJointZInitialDegree;
                postLowerPressDeltaDegree = Math.abs(lowerPressTargetDegree - lowerJointZCurrentRotateDegree);
                upperPressTargetDegree =
                        upperJointZInitialDegree - UPPER_PRESS_MAX_DELTA_DEGREE * ratio * postLowerStretchRatio;
                postUpperPressDeltaDegree = Math.abs(upperPressTargetDegree - upperJointZCurrentRotateDegree);
                previousPosition = currentPosition;
                previousDirection = currentDirection;
            }
        }
    }
}
