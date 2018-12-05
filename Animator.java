/* I declare that this code is my own work */
/* Author <Junxiang Chen> <jchen115@sheffield.ac.uk> */
/*
the class control the animation of the lamp
 */

import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

/**
 * constructor
 */
public class Animator {
    private float MAX_LENGTH, MAX_WIDTH, MAX_DISTANCE;
    /*
    initial degrees and lengths
     */
    private double lowerJointZInitialDegree,
            upperJointZInitialDegree,
            lowerJointYInitialDegree,
            upperJointYInitialDegree,
            headJointZInitialDegree,
            headJointYInitialDegree,
            LAMP_BASE_LENGTH;

    /*
    positions and directions of the lamp
     */
    private Vec3 previousPosition = new Vec3(0, 0, 0);
    private Vec3 currentPosition = new Vec3(0, 0, 0);
    private Vec3 previousDirection = new Vec3(1, 0, 0);
    private Vec3 currentDirection = new Vec3(1, 0, 0);
    private Vec3 crossProduct;

    /*
    translate matrix that will apply to the lamp for moving
     */
    public Mat4 previousTranslateMatrix = new Mat4(1);
    public Mat4 currentTranslateMatrix = new Mat4(1);

    /*
    set the max degree that the lamp arms can rotate,
    ensure the lamp will perform plausible poses
     */
    private double lowerJointDeltaRotateDegree = 0;
    private double LOWER_PRESS_MAX_DELTA_DEGREE = 60;
    private double LOWER_STRETCH_MAX_DELTA_DEGREE = 45;
    private double UPPER_PRESS_MAX_DELTA_DEGREE = 60;
    private double UPPER_STRETCH_MAX_DELTA_DEGREE = 75;
    private double HEAD_UP_MAX_DELTA_DEGREE = 90;

    /*
    target degrees that the arms (joints) will rotate each time
     */
    private double lowerPressTargetDegree;
    private double lowerStretchTargetDegree;
    private double upperPressTargetDegree;
    private double upperStretchTargetDegree;
    private double upperJointYTargetDegree;
    private double headJointZTargetDegree;
    private double headJointYTargetDegree;
    private double rotateYDegreeCount = 0;

    /*
    current degree on each arm (joint)
     */
    public double lowerJointZCurrentRotateDegree;
    public double upperJointZCurrentRotateDegree;
    public double lowerJointYCurrentRotateDegree = 0;
    public double upperJointYCurrentRotateDegree;
    public double headJointZCurrentRotateDegree;
    public double headJointYCurrentRotateDegree;

    /*
    velocity of rotation and jump
     */
    private double lowerJointRotateZVelocity = 2.4;
    private double upperJointRotateZVelocity = 2.4;
    private double lowerJointRotateYVelocity = 0;
    private double upperJointRotateYVelocity = 1;
    private double headJointRotateZVelocity = 2;
    private double headJointRotateYVelocity = 2;
    private double tmpMaxVelocity = 0;
    private double jumpHorizonVelocity = 0.24;

    /*
    the ratio compare to the max length, height and rotate degree
     */
    private double ratio;
    private double postLowerPressRatio = 0.6;
    private double postLowerStretchRatio = 0.8;

    /*
    animation states
     */
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

    /*
    the time that a jump starts
     */
    private double startJumpTime;

    /*
    ranges that set to the lamp animation to ensure it will not jump to some region and intersect three other objects
     */
    private double[] obj_1_range;
    private double[] range1;
    private double[] obj_2_range;
    private double[] range2;
    private double[] obj_3_range;
    private double[] range3;

    /**
     * some variables that never used, but keep there in case of the more complex animation need in the future development
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

    /**
     * generate current time in the format of second
     * @return time
     */
    private double getCurrentSecond() {
        return System.currentTimeMillis() / 1000.0;
    }

    /**
     * constructor
     * @param MAX_LENGTH table length
     * @param MAX_WIDTH table width
     * @param lowerJointZInitialDegree initial degree in Z of lower joint
     * @param upperJointZInitialDegree initial degree in Z of upper joint
     * @param lowerJointYInitialDegree initial degree in Y of lower joint
     * @param upperJointYInitialDegree initial degree in Y of upper joint
     * @param headJointZInitialDegree initial degree in Z of head joint
     * @param headJointYInitialDegree initial degree in Y of head joint
     * @param LAMP_BASE_LENGTH the length of lamp base
     * @param obj_1_range the range of 1st object on the table
     * @param obj_2_range the range of 2nd object on the table
     * @param obj_3_range the range of 3rd object on the table
     */
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
            double[] obj_2_range,
            double[] obj_3_range
    ) {
        this.MAX_LENGTH = MAX_LENGTH;
        this.MAX_WIDTH = MAX_WIDTH;
        /*
        the max distance a lamp can jump in the table, set as the diagonal of the table
         */
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
        this.obj_3_range = obj_3_range;
        this.range3 = generateRange(this.obj_3_range);
    }

    /**
     * generate the range of coordinates that the lamp shouldn't jump in
     * @param obj_range the range of nth object on the table
     * @return the range of coordinates that the lamp shouldn't jump in
     */
    private double[] generateRange(double[] obj_range) {
        return new double[]{
                obj_range[0] - 0.5 * obj_range[2] - LAMP_BASE_LENGTH,
                obj_range[0] + 0.5 * obj_range[2] + LAMP_BASE_LENGTH,
                obj_range[1] - 0.5 * obj_range[3] - LAMP_BASE_LENGTH,
                obj_range[1] + 0.5 * obj_range[3] + LAMP_BASE_LENGTH
        };
    }

    /**
     * generate the random pose, including different degrees on each joint
     */
    public void generateRandomPose() {
        if (ANIMATION_RANDOM_GENERATE) {
            double randomLowerZ = Math.random() * 60;
            double randomUpperZ = Math.random() * (-90) - 30;
            double randomUpperY = Math.random() * 180 - 90;
            double randomHeadJY = Math.random() * 120 - 60;
            double randomHeadJZ = Math.random() * 90;
//            System.out.println("random lower rotate z" + randomLowerZ);
//            System.out.println("random upper rotate z" + randomUpperZ);
//            System.out.println("random upper rotate y" + randomUpperY);
//            System.out.println("random head joint rotate y" + randomHeadJY);
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

    /**
     * this function will be executed 60 times per second so we do not need loop
     * generate lower joint random motion
     */
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

    /**
     * control Y rotate for upper joint
     */
    private void upperJointYRotate() {
        if (upperJointYCurrentRotateDegree >= upperJointYTargetDegree) {
            upperJointYCurrentRotateDegree = upperJointYCurrentRotateDegree - upperJointRotateYVelocity;
        } else {
            upperJointYCurrentRotateDegree = upperJointYCurrentRotateDegree + upperJointRotateYVelocity;
        }
    }

    /**
     * control Y rotate for head joint
     */
    private void headJointYRotate() {
        if (headJointYCurrentRotateDegree >= headJointYTargetDegree) {
            headJointYCurrentRotateDegree = headJointYCurrentRotateDegree - headJointRotateYVelocity;
        } else {
            headJointYCurrentRotateDegree = headJointYCurrentRotateDegree + headJointRotateYVelocity;
        }
    }

    /**
     * control Z rotate for head joint
     */
    private void headJointZRotate() {
        if (headJointZCurrentRotateDegree >= headJointZTargetDegree) {
            headJointZCurrentRotateDegree = headJointZCurrentRotateDegree - headJointRotateZVelocity;
        } else {
            headJointZCurrentRotateDegree = headJointZCurrentRotateDegree + headJointRotateZVelocity;
        }
    }

    /**
     * reset the random pose
     */
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

    /**
     * generate random target angle
     */
    public void generateRandomTargetAngle() {
        if (ANIMATION_GENERATION) {
            generateRandomPosition();
            currentTranslateMatrix = Mat4Transform.translate(currentPosition.x, 0, currentPosition.z);
            generateLowerJointYRotateDegree();
            ANIMATION_GENERATION = false;
            ANIMATION_PREP_Y_ROTATE = true;
        }
    }

    /**
     * generate random position for jumping
     */
    private void generateRandomPosition() {
        /*
        limit the lamp will jump on the table
         */
        double randomX = Math.random() * (MAX_LENGTH - LAMP_BASE_LENGTH) - (MAX_LENGTH - LAMP_BASE_LENGTH) / 2;
        double randomZ = Math.random() * (MAX_WIDTH - LAMP_BASE_LENGTH) - (MAX_WIDTH - LAMP_BASE_LENGTH) / 2;
        /*
        control the lamp that will not intersect other objects in the table
         */
        while (
                judgeRange(range1, randomX, randomZ) |
                        judgeRange(range2, randomX, randomZ) |
                        judgeRange(range3, randomX, randomZ)
        ) {
            randomX = Math.random() * (MAX_LENGTH - LAMP_BASE_LENGTH) - (MAX_LENGTH - LAMP_BASE_LENGTH) / 2;
            randomZ = Math.random() * (MAX_WIDTH - LAMP_BASE_LENGTH) - (MAX_WIDTH - LAMP_BASE_LENGTH) / 2;
        }
        currentPosition = new Vec3((float) randomX, 0, (float) randomZ);
        generateAngles();
    }

    /**
     *judge whether the random position is in the specific region that will intersect objects
     */
    private boolean judgeRange(double[] range, double randomX, double randomZ) {
        return (randomX >= range[0] & randomX <= range[1] & randomZ >= range[2] & randomZ <= range[3]);
    }

    /**
     * generate lower joint Y rotate degree before jumping
     */
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

    /*
    generate target angles before jumping
     */
    private void generateAngles() {
        ratio = Vec3.magnitude(Vec3.subtract(previousPosition, currentPosition)) / MAX_DISTANCE;
        lowerPressTargetDegree = LOWER_PRESS_MAX_DELTA_DEGREE * ratio + lowerJointZInitialDegree;
        lowerStretchTargetDegree = lowerJointZInitialDegree - LOWER_STRETCH_MAX_DELTA_DEGREE * ratio;
        upperPressTargetDegree = upperJointZInitialDegree - UPPER_PRESS_MAX_DELTA_DEGREE * ratio;
        upperStretchTargetDegree = UPPER_STRETCH_MAX_DELTA_DEGREE * ratio + upperJointZInitialDegree;
        headJointZTargetDegree = HEAD_UP_MAX_DELTA_DEGREE * ratio + headJointZInitialDegree;
    }

    /*
    ease in and ease out of rotation
     */
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
     * update lower joint Y rotate degree before jumping
     * @param startTime the time user press the button
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

    /**
     * control the press (Z rotate) of lower joint
     */
    private void lowerJointPress() {
        if (lowerJointZCurrentRotateDegree < lowerPressTargetDegree) {
            /*
             * ease in/out
             * not used but keep codes in comments in case of further usage
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

    /**
     * control the stretch (Z rotate) of lower joint
     */
    private void lowerJointStretch() {
        if (lowerJointZCurrentRotateDegree > lowerStretchTargetDegree) {
            /*
             * ease in/out
             * not used but keep codes in comments in case of further usage
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

    /**
     * control the press (Z rotate) of upper joint
     */
    private void upperJointPress() {
        if (upperJointZCurrentRotateDegree > upperPressTargetDegree) {
            /*
             * ease in/out
             * not used but keep codes in comments in case of further usage
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

    /**
     * control the stretch (Z rotate) of upper joint
     */
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

    /**
     * control the up motion (Z rotate) of head joint
     */
    private void headUp() {
        if (headJointZCurrentRotateDegree <= headJointZTargetDegree) {
//            System.out.println("up");
            headJointZCurrentRotateDegree = headJointZCurrentRotateDegree + headJointRotateZVelocity;
        }
    }

    /**
     * control the down motion (Z rotate) of head joint
     */
    private void headDown() {
        if (headJointZCurrentRotateDegree >= headJointZInitialDegree) {
//            System.out.println("down");
            headJointZCurrentRotateDegree = headJointZCurrentRotateDegree - headJointRotateZVelocity;
        }
    }

    /**
     * this function will be executed 60 times per second so we do not need loop
     * update the animation of press and stretch (Z rotate) of jumping
     */
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

    /**
     * this function will be executed 60 times per second so we do not need loop
     * update the animation of jumping
     */
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
