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
    private Light light1, light2;
    private MovingLight lightBulb;
    private Mat4 lightBulbSelfTranslate;
    private Table table;
    private Wall wall;
    private Lamp lamp;
    private SGNode lampRoot;
    private NameNode lampHeadName;
    private TransformNode lampTranslate,
            lampLowerJointYRotate, lampLowerJointZRotate,
            lampUpperJointYRotate, lampUpperJointZRotate,
            lampHeadJointYRotate, lampHeadJointZRotate,
            lampHeadYRotate, lampHeadZRotate,
            lampTailXRotate, lampTailYRotate, lampTailZRotate;
    private Animator animator;
    private double startTime = 0;

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
//        gl3.glFrontFace(GL.GL_CCW);    // default is 'CCW'
//        gl3.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
//        gl3.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
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

        /**
         * static lights
         */
        /*
        light1
         */
        light1 = new Light(gl3);
        light1.setPosition(new Vec3(3f,2f,1f));
        light1.setCamera(camera);

        /*
        light2
         */
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
        lampRoot = new NameNode("lamp root");
        lampTranslate = new TransformNode("lamp transform", Mat4Transform.translate(LAMP_POSITION_X, LAMP_POSITION_Y, LAMP_POSITION_Z));
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
        Mesh lampBaseMesh = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        Shader lampBaseShader = new Shader(gl3, "shader/vs_floor.txt", "shader/fs_floor.txt");
        Material lampBaseMaterial = new Material(
                new Vec3(0.1f, 0.5f, 0.1f),
                new Vec3(0.2f, 0.5f, 0.4f),
                new Vec3(0.3f, 0.3f, 0.3f), 32.0f
        );
        Model lamp_base = new Model(gl3, camera, light1, light2, lightBulb, lampBaseShader, lampBaseMaterial, new Mat4(1), lampBaseMesh);
        Mat4 lampBaseModelMatrix = Mat4Transform.scale(LAMP_BASE_LENGTH, LAMP_BASE_HEIGHT, LAMP_BASE_WIDTH);
        TransformNode lampBaseScale = new TransformNode("lamp base scale", lampBaseModelMatrix);
        NameNode lampBaseName = new NameNode("lamp base");
        Mat4 lampBaseModelMatrix2 = Mat4Transform.rotateAroundY(45);
        TransformNode lampBaseScale2 = new TransformNode("lamp base scale", lampBaseModelMatrix2);
        ModelNode lampBaseNode_1 = new ModelNode("lamp base 1", lamp_base);
        ModelNode lampBaseNode_2 = new ModelNode("lamp base 2", lamp_base);

        /**
         *lamp joints
         */
        float LAMP_JOINT_DIAMETER = 0.6f;
        Mesh lampJointMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Model lamp_joint = new Model(gl3, camera, light1, light2, lightBulb, lampBaseShader, lampBaseMaterial, new Mat4(1), lampJointMesh);
        Mat4 lampJointModelMatrix = Mat4Transform.scale(LAMP_JOINT_DIAMETER, LAMP_JOINT_DIAMETER, LAMP_JOINT_DIAMETER);
        TransformNode lampJointScale = new TransformNode("lamp joint scale", lampJointModelMatrix);

        /*
        lamp lower joint
         */
        Mat4 lampLowerJointMatrix = Mat4Transform.translate(0, 0.4f, 0);
        TransformNode lampLowerJointTranslate = new TransformNode("lamp lower joint translate", lampLowerJointMatrix);
        NameNode lampLowerJointName = new NameNode("lamp lower joint");
        Mat4 lampLowerJointYRotateMatrix = Mat4Transform.rotateAroundY((float) lowerPressYInitialDegree);
        Mat4 lampLowerJointZRotateMatrix = Mat4Transform.rotateAroundZ((float) lowerPressZInitialDegree);
        lampLowerJointYRotate = new TransformNode("lamp lower joint y rotate", lampLowerJointYRotateMatrix);
        lampLowerJointZRotate = new TransformNode("lamp lower joint z rotate", lampLowerJointZRotateMatrix);
        ModelNode lampLowerJointNode = new ModelNode("lamp lower joint", lamp_joint);

        /**
         * lamp arms
         */
        float LAMP_ARM_LENGTH = LAMP_JOINT_DIAMETER * 0.5f;
        float LAMP_ARM_WIDTH = LAMP_ARM_LENGTH;
        float LAMP_ARM_HEIGHT = 3.5f;
        Mesh lampArmMesh = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        Model lamp_arm = new Model(gl3, camera, light1, light2, lightBulb, lampBaseShader, lampBaseMaterial, new Mat4(1), lampArmMesh);
        Mat4 lampArmModelMatrix = Mat4Transform.scale(LAMP_ARM_LENGTH, LAMP_ARM_HEIGHT, LAMP_ARM_WIDTH);
        TransformNode lampArmScale = new TransformNode("lamp arm scale", lampArmModelMatrix);

        /*
        lamp lower arm
         */
        Mat4 lampLowerArmMatrix = Mat4Transform.translate(0, 0.4f, 0);
        TransformNode lampLowerArmTranslate = new TransformNode("lamp lower arm translate", lampLowerArmMatrix);
        NameNode lampLowerArmName = new NameNode("lamp lower arm");
        ModelNode lampLowerArmNode = new ModelNode("lamp lower arm", lamp_arm);

        /*
        lamp upper joint
         */
        Mat4 lampUpperJointMatrix = Mat4Transform.translate(0, LAMP_ARM_HEIGHT, 0);
        TransformNode lampUpperJointTranslate = new TransformNode("lamp upper joint translate", lampUpperJointMatrix);
        NameNode lampUpperJointName = new NameNode("lamp upper joint");
        Mat4 lampUpperJointYRotateMatrix = Mat4Transform.rotateAroundY((float) upperPressYInitialDegree);
        Mat4 lampUpperJointZRotateMatrix = Mat4Transform.rotateAroundZ((float) upperPressZInitialDegree);
        lampUpperJointYRotate = new TransformNode("lamp upper joint y rotate", lampUpperJointYRotateMatrix);
        lampUpperJointZRotate = new TransformNode("lamp upper joint z rotate", lampUpperJointZRotateMatrix);
        ModelNode lampUpperJointNode = new ModelNode("lamp upper joint", lamp_joint);

        /*
        lamp tail
         */
        float LAMP_TAIL_SCALE_X = 0.65f;
        float LAMP_TAIL_SCALE_Y = 0.25f;
        float LAMP_TAIL_SCALE_Z = 0.25f;
        Mesh lampTailMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Model lamp_tail = new Model(gl3, camera, light1, light2, lightBulb, lampBaseShader, lampBaseMaterial, new Mat4(1), lampTailMesh);
        Mat4 lampTailModelMatrix = Mat4Transform.scale(LAMP_TAIL_SCALE_X, LAMP_TAIL_SCALE_Y, LAMP_TAIL_SCALE_Z);
        lampTailModelMatrix = Mat4.multiply(Mat4Transform.translate(-LAMP_TAIL_SCALE_X, 0, 0), lampTailModelMatrix);
        TransformNode lampTailTransform = new TransformNode("lamp tail", lampTailModelMatrix);
        Mat4 lampTailXRotateMatrix = Mat4Transform.rotateAroundX(0);
        Mat4 lampTailYRotateMatrix = Mat4Transform.rotateAroundY(0);
        Mat4 lampTailZRotateMatrix = Mat4Transform.rotateAroundZ(0);
        lampTailXRotate = new TransformNode("lamp tail x rotate", lampTailXRotateMatrix);
        lampTailYRotate = new TransformNode("lamp tail y rotate", lampTailYRotateMatrix);
        lampTailZRotate = new TransformNode("lamp tail z rotate", lampTailZRotateMatrix);
        NameNode lampTailName = new NameNode("lamp tail");
        ModelNode lampTailNode = new ModelNode("lamp tail", lamp_tail);

        /*
        lamp upper arm
         */
        Mat4 lampUpperArmMatrix = Mat4Transform.translate(0, LAMP_ARM_HEIGHT / 2, 0);
        lampUpperArmMatrix = Mat4.multiply(lampUpperArmMatrix, lampArmModelMatrix);
        TransformNode lampUpperArmTranslate = new TransformNode("lamp upper arm translate", lampUpperArmMatrix);
        NameNode lampUpperArmName = new NameNode("lamp upper arm");
        ModelNode lampUpperArmNode = new ModelNode("lamp upper arm", lamp_arm);

        /**
         * lamp head
         */
        float LAMP_HEAD_JOINT_DIAMETER = 0.65f;
        float LAMP_HEAD_XZ_SCALE = 2.8f;
        float LAMP_HEAD_Y_SCALE = 1f;
        Model head_joint = new Model(gl3, camera, light1, light2, lightBulb, lampBaseShader, lampBaseMaterial,new Mat4(1), lampJointMesh);
        Mesh lampHeadMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Model lamp_head = new Model(gl3, camera, light1, light2, lightBulb, lampBaseShader, lampBaseMaterial, new Mat4(1), lampHeadMesh);

        /*
        lamp head joint
         */
        Mat4 lampHeadJointMatrix = Mat4Transform.translate(0, LAMP_ARM_HEIGHT, 0);
        Mat4 lampHeadJointSelfMatrix = Mat4Transform.scale(LAMP_HEAD_JOINT_DIAMETER, LAMP_HEAD_JOINT_DIAMETER, LAMP_HEAD_JOINT_DIAMETER);
        TransformNode lampHeadJointTranslate = new TransformNode("lamp head joint translate", lampHeadJointMatrix);
        TransformNode lampHeadJointSelfScale = new TransformNode("lamp head joint self scale", lampHeadJointSelfMatrix);
        NameNode lampHeadJointName = new NameNode("lamp head joint");
        Mat4 lampHeadJointYRotateMatrix = Mat4Transform.rotateAroundY((float) headJointYInitialDegree);
        Mat4 lampHeadJointZRotateMatrix = Mat4Transform.rotateAroundZ((float) headJointZInitialDegree);
        lampHeadJointYRotate = new TransformNode("lamp head joint y rotate", lampHeadJointYRotateMatrix);
        lampHeadJointZRotate = new TransformNode("lamp head joint z rotate", lampHeadJointZRotateMatrix);
        ModelNode lampHeadJointNode = new ModelNode("lamp head joint", head_joint);

        /*
        lamp head main
         */
        Mat4 lampHeadMatrix = Mat4Transform.translate(1f, LAMP_HEAD_JOINT_DIAMETER / 2, 0);
        Mat4 lampHeadSelfMatrix = Mat4Transform.scale(LAMP_HEAD_XZ_SCALE, LAMP_HEAD_Y_SCALE, LAMP_HEAD_XZ_SCALE);
        TransformNode lampHeadTranslate = new TransformNode("lamp head translate", lampHeadMatrix);
        TransformNode lampHeadSelfScale = new TransformNode("lamp head self scale", lampHeadSelfMatrix);
        lampHeadName = new NameNode("lamp head");
        Mat4 lampHeadYRotateMatrix = Mat4Transform.rotateAroundY(0);
        Mat4 lampHeadZRotateMatrix = Mat4Transform.rotateAroundZ(0);
        lampHeadYRotate = new TransformNode("lamp head joint y rotate", lampHeadYRotateMatrix);
        lampHeadZRotate = new TransformNode("lamp head joint z rotate", lampHeadZRotateMatrix);
        ModelNode lampHeadNode = new ModelNode("lamp head joint", lamp_head);

        /**
        lamp head decoration
         */
        /*
        lamp head back
         */
        float LAMP_HEAD_BACK_DIAMETER = 1.15f;
        float LAMP_HEAD_BACK_Y_SCALE = 0.6f;
        Mesh lampHeadBackMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Model lamp_head_back = new Model(gl3, camera, light1, light2, lightBulb, lampBaseShader, lampBaseMaterial, new Mat4(1), lampHeadBackMesh, textureId0);
        Mat4 lampHeadBackModelMatrix = Mat4Transform.scale(LAMP_HEAD_BACK_DIAMETER, LAMP_HEAD_BACK_DIAMETER * LAMP_HEAD_BACK_Y_SCALE, LAMP_HEAD_BACK_DIAMETER);
        lampHeadBackModelMatrix = Mat4.multiply(Mat4Transform.translate(0, LAMP_HEAD_BACK_DIAMETER * LAMP_HEAD_BACK_Y_SCALE / 1.5f, 0), lampHeadBackModelMatrix);
        TransformNode lampHeadBackTransform = new TransformNode("lamp head transform", lampHeadBackModelMatrix);
        NameNode lampHeadBackName = new NameNode("lamp head back");
        ModelNode lampHeadBackNode = new ModelNode("lamp head back", lamp_head_back);

        /*
        lamp head ear
         */
        float LAMP_HEAD_EAR_X_SCALE = 2.5f;
        float LAMP_HEAD_EAR_Y_SCALE = 0.5f;
        float LAMP_HEAD_EAR_Z_SCALE = 1;
        float LAMP_HEAD_EAR_X_POSITION = 1.5f;
        float LAMP_HEAD_EAR_Y_POSITION = 0.35f;
        float LAMP_HEAD_EAR_Z_POSITION = 0.8f;
        Mesh lampHeadEarMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Model lamp_head_left_ear = new Model(gl3, camera, light1, light2, lightBulb, lampBaseShader, lampBaseMaterial, new Mat4(1), lampHeadEarMesh);
        Mat4 lampHeadEarLeftModelMatrix = Mat4Transform.scale(LAMP_HEAD_EAR_X_SCALE, LAMP_HEAD_EAR_Y_SCALE, LAMP_HEAD_EAR_Z_SCALE);
        lampHeadEarLeftModelMatrix = Mat4.multiply(Mat4Transform.translate(LAMP_HEAD_EAR_X_POSITION, LAMP_HEAD_EAR_Y_POSITION, -LAMP_HEAD_EAR_Z_POSITION), lampHeadEarLeftModelMatrix);
        TransformNode lampHeadEarLeftTransform = new TransformNode("lamp head ear left transform", lampHeadEarLeftModelMatrix);
        NameNode lampHeadEarLeftName = new NameNode("lamp head ear left");
        ModelNode lampHeadEarLeftNode = new ModelNode("lamp head ear left", lamp_head_left_ear);
        Model lamp_head_right_ear = new Model(gl3, camera, light1, light2, lightBulb, lampBaseShader, lampBaseMaterial, new Mat4(1), lampHeadEarMesh);
        Mat4 lampHeadEarRightModelMatrix = Mat4Transform.scale(LAMP_HEAD_EAR_X_SCALE, LAMP_HEAD_EAR_Y_SCALE, LAMP_HEAD_EAR_Z_SCALE);
        lampHeadEarRightModelMatrix = Mat4.multiply(Mat4Transform.translate(LAMP_HEAD_EAR_X_POSITION, LAMP_HEAD_EAR_Y_POSITION, LAMP_HEAD_EAR_Z_POSITION), lampHeadEarRightModelMatrix);
        TransformNode lampHeadEarRightTransform = new TransformNode("lamp head ear right transform", lampHeadEarRightModelMatrix);
        NameNode lampHeadEarRightName = new NameNode("lamp head ear right");
        ModelNode lampHeadEarRightNode = new ModelNode("lamp head ear right", lamp_head_right_ear);

        /*
        lamp scene graph
         */
        lampRoot.addChild(lampTranslate);
            lampTranslate.addChild(lampBaseScale);
                lampBaseScale.addChild(lampBaseName);
                    lampBaseName.addChild(lampBaseNode_1);
                    lampBaseName.addChild(lampBaseScale2);
                        lampBaseScale2.addChild(lampBaseNode_2);
            lampTranslate.addChild(lampJointScale);
                lampJointScale.addChild(lampLowerJointName);
                    lampLowerJointName.addChild(lampLowerJointTranslate);
                        lampLowerJointTranslate.addChild(lampLowerJointYRotate);
                            lampLowerJointYRotate.addChild(lampLowerJointZRotate);
                                lampLowerJointZRotate.addChild(lampLowerJointNode);
                                lampLowerJointZRotate.addChild(lampLowerArmName);
                                    lampLowerArmName.addChild(lampArmScale);
                                        lampArmScale.addChild(lampLowerArmTranslate);
                                            lampLowerArmTranslate.addChild(lampLowerArmNode);
                                    lampLowerArmName.addChild(lampUpperJointTranslate);
                                        lampUpperJointTranslate.addChild(lampUpperJointYRotate);
                                            lampUpperJointYRotate.addChild(lampUpperJointZRotate);
                                                lampUpperJointZRotate.addChild(lampUpperJointName);
                                                    lampUpperJointName.addChild(lampUpperJointNode);
                                                    lampUpperJointName.addChild(lampUpperArmName);
                                                        lampUpperArmName.addChild(lampUpperArmTranslate);
                                                            lampUpperArmTranslate.addChild(lampUpperArmNode);
                                                        lampUpperArmName.addChild(lampHeadJointTranslate);
                                                            lampHeadJointTranslate.addChild(lampHeadJointYRotate);
                                                                lampHeadJointYRotate.addChild(lampHeadJointZRotate);
                                                                    lampHeadJointZRotate.addChild(lampHeadJointName);
                                                                        lampHeadJointName.addChild(lampHeadJointSelfScale);
                                                                            lampHeadJointSelfScale.addChild(lampHeadJointNode);
                                                                        lampHeadJointName.addChild(lampHeadTranslate);
                                                                            lampHeadTranslate.addChild(lampHeadYRotate);
                                                                                lampHeadYRotate.addChild(lampHeadZRotate);
                                                                                    lampHeadZRotate.addChild(lampHeadName);
                                                                                        lampHeadName.addChild(lampHeadSelfScale);
                                                                                            lampHeadSelfScale.addChild(lampHeadNode);
                                        lampUpperJointTranslate.addChild(lampTailXRotate);
                                            lampTailXRotate.addChild(lampTailYRotate);
                                                lampTailYRotate.addChild(lampTailZRotate);
                                                    lampTailZRotate.addChild(lampTailName);
                                                        lampTailName.addChild(lampTailTransform);
                                                            lampTailTransform.addChild(lampTailNode);
                                                                        lampHeadJointName.addChild(lampHeadTranslate);
                                                                            lampHeadTranslate.addChild(lampHeadBackTransform);
                                                                                lampHeadBackTransform.addChild(lampHeadBackName);
                                                                                    lampHeadBackName.addChild(lampHeadBackNode);
                                                                        lampHeadJointName.addChild(lampHeadEarLeftTransform);
                                                                            lampHeadEarLeftTransform.addChild(lampHeadEarLeftName);
                                                                                lampHeadEarLeftName.addChild(lampHeadEarLeftNode);
                                                                        lampHeadJointName.addChild(lampHeadEarRightTransform);
                                                                            lampHeadEarRightTransform.addChild(lampHeadEarRightName);
                                                                                lampHeadEarRightName.addChild(lampHeadEarRightNode);

        lampRoot.update();
//        print scene graph nodes
        lampRoot.print(0, false);

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
//        animator.generateRandomTargetAngle();
//        testCube1.setModelMatrix(animator.currentTranslateMatrix);
//        animator.updateLowerJointYRotateDegree(startTime);
//        lampLowerJointYRotate.setTransform(Mat4Transform.rotateAroundY((float) (animator.lowerJointYCurrentRotateDegree)));
//        animator.updateJointJumpZRotateDegree();
//        lampLowerJointZRotate.setTransform(Mat4Transform.rotateAroundZ((float) (animator.lowerJointZCurrentRotateDegree)));
//        lampUpperJointZRotate.setTransform(Mat4Transform.rotateAroundZ((float) (animator.upperJointZCurrentRotateDegree)));
//        animator.updateJump();
//        lampTranslate.setTransform(animator.previousTranslateMatrix);
//        animator.generateRandomPose();
//        animator.generateLowerJointRandomMotion();
//        lampUpperJointYRotate.setTransform(Mat4Transform.rotateAroundY((float) (animator.upperJointYCurrentRotateDegree)));
//        lampHeadJointZRotate.setTransform(Mat4Transform.rotateAroundZ((float) animator.headJointZCurrentRotateDegree));
//        lampHeadJointYRotate.setTransform(Mat4Transform.rotateAroundY((float) animator.headJointYCurrentRotateDegree));
//        lampTailYRotate.setTransform(Mat4Transform.rotateAroundY(10 * (float) Math.sin(20 * getStartSecond())));
//        lampTailZRotate.setTransform(Mat4Transform.rotateAroundZ(5 * (float) Math.sin(20 * getStartSecond())));

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
        lamp.lampHeadJointZRotate.setTransform(Mat4Transform.rotateAroundZ((float) animator.headJointZCurrentRotateDegree));
        lamp.lampHeadJointYRotate.setTransform(Mat4Transform.rotateAroundY((float) animator.headJointYCurrentRotateDegree));
        lamp.lampTailYRotate.setTransform(Mat4Transform.rotateAroundY(10 * (float) Math.sin(20 * getStartSecond())));
        lamp.lampTailZRotate.setTransform(Mat4Transform.rotateAroundZ(5 * (float) Math.sin(20 * getStartSecond())));

//        lightBulb.setWorldMatrix(Mat4.multiply(lampHeadName.worldTransform, lightBulbSelfTranslate));
        lightBulb.setWorldMatrix(Mat4.multiply(lamp.lampHeadName.worldTransform, lightBulbSelfTranslate));

//        System.out.println("light bulb world position" + lightBulb.getWorldPosition());
//        System.out.println("light bulb world direction" + lightBulb.getWorldDirection());
        lampRoot.update();
        lamp.update();
    }

    public void render(GL3 gl3) {
        gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        light1.render(gl3);
        light2.render(gl3);
        floor.render(gl3);
        table.draw(gl3);
        wall.draw(gl3);
        env.render(gl3);
        updateMotion();
//        lampRoot.draw(gl3);
        lamp.draw(gl3);
        lightBulb.render(gl3);
        testCube1.render(gl3);
    }
}
