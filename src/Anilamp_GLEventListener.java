import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Anilamp_GLEventListener implements GLEventListener {
    private Camera camera;
    private Model floor, env, testCube1;
    private Material light1Material, light2Material, lightBulbMaterial, nullMaterial;
    private Light light1, light2;
    private MovingLight lightBulb;
    private Mat4 lightBulbSelfTranslate;
    private Table table;
    private Wall wall;
    private Lamp lamp;
    private Animator animator;
    private double startTime = 0;

    public boolean LIGHT1_ON = true;
    public boolean LIGHT2_ON = true;
    public boolean LAMP_ON = true;

    private double getStartSecond() {
        return System.currentTimeMillis() / 1000.0;
    }

    public Anilamp_GLEventListener(Camera camera) {
        this.camera = camera;
        this.camera.setPosition(new Vec3(4f,12f,18f));
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL3 gl3 = glAutoDrawable.getGL().getGL3();
        System.err.println("Chosen glCapabilities: " + glAutoDrawable.getChosenGLCapabilities());
        gl3.glClearColor(0.1f, 0.2f, 0.4f, 1.0f);
        gl3.glClearDepth(1.0f);
        gl3.glEnable(GL.GL_DEPTH_TEST);
        gl3.glDepthFunc(GL.GL_LESS);
        gl3.glFrontFace(GL.GL_CCW);    // default is 'CCW'
        gl3.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
        gl3.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
        initialise(gl3);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        GL3 gl3 = glAutoDrawable.getGL().getGL3();
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL3 gl3 = glAutoDrawable.getGL().getGL3();
        render(gl3);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        GL3 gl3 = glAutoDrawable.getGL().getGL3();
        gl3.glViewport(i, i1, i2, i3);
        float aspect = (float) i2 / (float) i3;
        camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    }

    public void initialise(GL3 gl3) {
        int[] textureId0 = TextureLibrary.loadTexture(gl3, "textures/chequerboard.jpg");

        nullMaterial = new Material();
        nullMaterial.setAmbient(0, 0, 0);
        nullMaterial.setDiffuse(0, 0, 0);
        nullMaterial.setSpecular(0, 0, 0);

        /**
         * static lights
         */
        /*
        light1
         */
        light1Material = new Material();
        light1Material.setAmbient(0.5f, 0.6f, 0.5f);
        light1Material.setDiffuse(0.8f, 0.8f, 0.8f);
        light1Material.setSpecular(0.8f, 0.8f, 0.8f);
        light1 = new Light(gl3);
        light1.setPosition(new Vec3(3f,2f,1f));
        light1.setCamera(camera);

        /*
        light2
         */
        light2Material = new Material();
        light2Material.setAmbient(0.5f, 0.5f, 0.5f);
        light2Material.setDiffuse(0.8f, 0.8f, 0.8f);
        light2Material.setSpecular(0.8f, 0.7f, 0.8f);
        light2 = new Light(gl3);
        light2.setCamera(camera);
        light2.setPosition(new Vec3(-3f,2f,1f));

        /**
         * moving light
         */
        /*
        light bulb
         */
        Vec3 lightBulbLocalPosition = new Vec3(0, -0.8f, 0);
        lightBulbSelfTranslate = Mat4Transform.translate(new Vec3(0, -0.5f, 0));
        Vec3 lightBulbLocalDirection = new Vec3(0f, -1f, 0f);
        lightBulbMaterial = new Material();
        lightBulbMaterial.setAmbient(0.5f, 0.5f, 0.5f);
        lightBulbMaterial.setDiffuse(0.8f, 0.8f, 0.8f);
        lightBulbMaterial.setSpecular(0.8f, 0.8f, 0.8f);
        lightBulb = new MovingLight(gl3, lightBulbLocalPosition, lightBulbSelfTranslate, lightBulbLocalDirection);
        lightBulb.setWorldPosition();
        lightBulb.setWorldDirection();
        lightBulb.setCamera(camera);

        /**
         * floor
         */
        float floor_Y = -8.328f;

        Mesh floorMesh = new Mesh(gl3, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader floorShader = new Shader(gl3, "shader/vs_floor.txt", "shader/fs_floor.txt");
        Material floorMaterial = new Material(
                new Vec3(0.0f, 0.5f, 0.81f),
                new Vec3(0.0f, 0.5f, 0.81f),
                new Vec3(0.3f, 0.3f, 0.3f), 32.0f
        );
        Mat4 floorModelMatrix = Mat4Transform.scale(60f,1f,40f);
        floorModelMatrix = Mat4.multiply(Mat4Transform.translate(0, floor_Y, 0), floorModelMatrix);
        floor = new Model(gl3, camera, light1, light2, lightBulb, floorShader, floorMaterial, floorModelMatrix, floorMesh, textureId0);

        /**
         * table
         */
        float TABLE_X_POSITION = 0;
        float TABLE_Y_POSITION = -0.328f;
        float TABLE_Z_POSITION = 0;

        /*
        table body
         */
        float TABLE_BODY_LENGTH = 20;
        float TABLE_BODY_WIDTH = 10;
        float TABLE_BODY_HEIGHT = 0.4f;

        /*
        table legs
         */
        float TABLE_LEG_LENGTH = 0.6f;
        float TABLE_LEG_WIDTH = TABLE_LEG_LENGTH;
        float TABLE_LEG_HEIGHT = 8;

        table = new Table(gl3, camera, light1, light2, lightBulb,
                TABLE_X_POSITION, TABLE_Y_POSITION, TABLE_Z_POSITION,
                TABLE_BODY_LENGTH, TABLE_BODY_WIDTH, TABLE_BODY_HEIGHT,
                TABLE_LEG_LENGTH, TABLE_LEG_WIDTH, TABLE_LEG_HEIGHT);
        table.execute();

        /**
         * wall
         */
        float WALL_LENGTH = 16;
        float WALL_HEIGHT = 0.6f + TABLE_LEG_HEIGHT + TABLE_BODY_HEIGHT;
        float WALL_WIDTH = 0.8f;
        float WALL_X_POSITION = 0;
        float WALL_Y_POSITION = floor_Y + WALL_HEIGHT / 2;
        float WALL_Z_POSITION = -(TABLE_BODY_WIDTH / 2 + WALL_WIDTH / 2);

        wall = new Wall(gl3, camera, light1, light2, lightBulb, WALL_LENGTH, WALL_HEIGHT, WALL_WIDTH, WALL_X_POSITION, WALL_Y_POSITION, WALL_Z_POSITION);
        wall.execute();

        /**
         * outside environment
         */
        float env_Z = -200f;

        Mesh envMesh = new Mesh(gl3, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader envShader = new Shader(gl3, "shader/vs_floor.txt", "shader/fs_floor.txt");
        Material envMaterial = new Material(
                new Vec3(1.0f, 0.2f, 0.2f),
                new Vec3(1.0f, 0.2f, 0.2f),
                new Vec3(0.3f, 0.3f, 0.3f), 32.0f
        );
        Mat4 envModelMatrix = Mat4Transform.scale(400f,1f,160f);
        envModelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), envModelMatrix);
        envModelMatrix = Mat4.multiply(Mat4Transform.translate(0, -4, env_Z), envModelMatrix);
        env = new Model(gl3, camera, light1, light2, lightBulb, envShader, envMaterial, envModelMatrix, envMesh, textureId0);


        /**
         * lamp
         */
        float LAMP_POSITION_X = 0;
        float LAMP_POSITION_Y = 0;
        float LAMP_POSITION_Z = 0;
        double lowerPressZInitialDegree = 30;
        double upperPressZInitialDegree = -75;
        double lowerPressYInitialDegree = 0;
        double upperPressYInitialDegree = 0;
        double headJointZInitialDegree = 50;
        double headJointYInitialDegree = 0;

        /*
        lamp base
         */
        float LAMP_BASE_LENGTH = 1.25f;
        float LAMP_BASE_WIDTH = LAMP_BASE_LENGTH;
        float LAMP_BASE_HEIGHT = 0.25f;

        /**
         *lamp joints
         */
        float LAMP_JOINT_DIAMETER = 0.6f;

        /*
        lamp lower joint
         */

        /**
         * lamp arms
         */
        float LAMP_ARM_LENGTH = LAMP_JOINT_DIAMETER * 0.5f;
        float LAMP_ARM_WIDTH = LAMP_ARM_LENGTH;
        float LAMP_ARM_HEIGHT = 3.5f;

        /*
        lamp lower arm
         */

        /*
        lamp upper joint
         */

        /*
        lamp tail
         */
        float LAMP_TAIL_SCALE_X = 0.65f;
        float LAMP_TAIL_SCALE_Y = 0.25f;
        float LAMP_TAIL_SCALE_Z = 0.25f;

        /*
        lamp upper arm
         */

        /**
         * lamp head
         */
        float LAMP_HEAD_JOINT_DIAMETER = 0.65f;
        float LAMP_HEAD_XZ_SCALE = 2.8f;
        float LAMP_HEAD_Y_SCALE = 1f;

        /*
        lamp head joint
         */

        /*
        lamp head main
         */

        /**
        lamp head decoration
         */
        /*
        lamp head back
         */
        float LAMP_HEAD_BACK_DIAMETER = 1.15f;
        float LAMP_HEAD_BACK_Y_SCALE = 0.6f;

        /*
        lamp head ear
         */
        float LAMP_HEAD_EAR_X_SCALE = 2.5f;
        float LAMP_HEAD_EAR_Y_SCALE = 0.5f;
        float LAMP_HEAD_EAR_Z_SCALE = 1;
        float LAMP_HEAD_EAR_X_POSITION = 1.5f;
        float LAMP_HEAD_EAR_Y_POSITION = 0.35f;
        float LAMP_HEAD_EAR_Z_POSITION = 0.8f;

        lamp = new Lamp(
                gl3, camera, light1, light2, lightBulb,
                LAMP_POSITION_X, LAMP_POSITION_Y, LAMP_POSITION_Z,
                LAMP_ARM_HEIGHT
        );
        lamp.initialise();
        lamp.generateLampBase(LAMP_BASE_LENGTH, LAMP_BASE_HEIGHT, LAMP_BASE_WIDTH);
        lamp.generateLampJoints(LAMP_JOINT_DIAMETER, lowerPressYInitialDegree, lowerPressZInitialDegree, upperPressYInitialDegree, upperPressZInitialDegree);
        lamp.generateArms(LAMP_ARM_LENGTH, LAMP_ARM_WIDTH);
        lamp.generateHead(LAMP_HEAD_JOINT_DIAMETER, LAMP_HEAD_XZ_SCALE, LAMP_HEAD_Y_SCALE, headJointYInitialDegree, headJointZInitialDegree);
        lamp.generateTail(LAMP_TAIL_SCALE_X, LAMP_TAIL_SCALE_Y, LAMP_TAIL_SCALE_Z);
        lamp.generateDecoration(LAMP_HEAD_BACK_DIAMETER, LAMP_HEAD_BACK_Y_SCALE,
        LAMP_HEAD_EAR_X_SCALE, LAMP_HEAD_EAR_Y_SCALE, LAMP_HEAD_EAR_Z_SCALE,
        LAMP_HEAD_EAR_X_POSITION, LAMP_HEAD_EAR_Y_POSITION, LAMP_HEAD_EAR_Z_POSITION);
        lamp.buildTree();
        lamp.update();

        /**
         * test/reference cube
         */
        Mesh testCubeMesh = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        Mat4 testCube1ModelMatrix = Mat4Transform.translate(-TABLE_BODY_LENGTH / 2, 0, -TABLE_BODY_WIDTH / 2);
        testCube1 = new Model(gl3, camera, light1, light2, lightBulb, floorShader, floorMaterial, testCube1ModelMatrix, testCubeMesh, textureId0);

        /*
        animation
         */
        animator = new Animator(
                TABLE_BODY_LENGTH, TABLE_BODY_WIDTH,
                lowerPressZInitialDegree, upperPressZInitialDegree, lowerPressYInitialDegree,
                upperPressYInitialDegree, headJointZInitialDegree, headJointYInitialDegree,
                LAMP_BASE_LENGTH
        );
    }

    public void setLightOnOff() {
        if (LIGHT1_ON) {
            light1.setMaterial(light1Material);
        } else {
            light1.setMaterial(nullMaterial);
        }

        if (LIGHT2_ON) {
            light2.setMaterial(light2Material);
        } else {
            light2.setMaterial(nullMaterial);
        }

        if (LAMP_ON) {
            lightBulb.setMaterial(lightBulbMaterial);
        } else {
            lightBulb.setMaterial(nullMaterial);
        }
    }

    public void setRandomPoseBegin() {
        animator.ANIMATION_RANDOM_GENERATE = true;
    }

    public void resetPose() {
        animator.resetRandomPose();
    }

    public void setAnimationBegin() {
        resetPose();
        animator.ANIMATION_GENERATION = true;
        startTime = getStartSecond();
    }

    public void updateMotion() {
        animator.generateRandomTargetAngle();
        testCube1.setModelMatrix(animator.currentTranslateMatrix);
        animator.updateLowerJointYRotateDegree(startTime);
        lamp.lampLowerJointYRotate.setTransform(Mat4Transform.rotateAroundY((float) (animator.lowerJointYCurrentRotateDegree)));
        animator.updateJointJumpZRotateDegree();
        lamp.lampLowerJointZRotate.setTransform(Mat4Transform.rotateAroundZ((float) (animator.lowerJointZCurrentRotateDegree)));
        lamp.lampUpperJointZRotate.setTransform(Mat4Transform.rotateAroundZ((float) (animator.upperJointZCurrentRotateDegree)));
        animator.updateJump();
        lamp.lampTranslate.setTransform(animator.previousTranslateMatrix);
        animator.generateRandomPose();
        animator.generateLowerJointRandomMotion();
        lamp.lampUpperJointYRotate.setTransform(Mat4Transform.rotateAroundY((float) (animator.upperJointYCurrentRotateDegree)));
        lamp.lampHeadJointZRotate.setTransform(Mat4Transform.rotateAroundZ((float) (animator.headJointZCurrentRotateDegree)));
        lamp.lampHeadJointYRotate.setTransform(Mat4Transform.rotateAroundY((float) (animator.headJointYCurrentRotateDegree)));
        lamp.lampTailYRotate.setTransform(Mat4Transform.rotateAroundY(10 * (float) Math.sin(20 * getStartSecond())));
        lamp.lampTailZRotate.setTransform(Mat4Transform.rotateAroundZ(5 * (float) Math.sin(20 * getStartSecond())));

        lightBulb.setWorldMatrix(Mat4.multiply(lamp.lampHeadName.worldTransform, lightBulbSelfTranslate));
//        System.out.println("light bulb world position" + lightBulb.getWorldPosition());
//        System.out.println("light bulb world direction" + lightBulb.getWorldDirection());
        lamp.update();
    }

    public void render(GL3 gl3) {
        gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        setLightOnOff();
        if (LIGHT1_ON) {
            light1.render(gl3);
        }
        if (LIGHT2_ON) {
            light2.render(gl3);
        }
        floor.render(gl3);
        table.draw(gl3);
        wall.draw(gl3);
        env.render(gl3);
        updateMotion();
        lamp.draw(gl3);
        if (LAMP_ON) {
            lightBulb.render(gl3);
        }
        testCube1.render(gl3);
    }
}
