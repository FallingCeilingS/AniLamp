/* I declare that this code is my own work */
/* Author <Junxiang Chen> <jchen115@sheffield.ac.uk> */
/*
subclass of SceneGraphObject
 */

import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Lamp extends SceneGraphObject {
    private float LAMP_POSITION_X, LAMP_POSITION_Y, LAMP_POSITION_Z, LAMP_ARM_HEIGHT;
    private Model lamp_base, lamp_joint, lamp_arm, head_joint, lamp_head,
            lamp_tail, lamp_head_back, lamp_head_left_ear, lamp_head_right_ear;
    private NameNode lampBaseName,
            lampLowerJointName, lampLowerArmName, lampUpperJointName, lampUpperArmName,
            lampHeadJointName,
            lampTailName, lampHeadBackName, lampHeadEarLeftName, lampHeadEarRightName;
    private TransformNode lampBaseScale, lampBaseRotate,
            lampJointScale, lampLowerJointTranslate,
            lampArmScale, lampLowerArmTranslate,
            lampUpperJointTranslate,
            lampUpperArmTranslate,
            lampHeadJointTranslate, lampHeadJointSelfScale, lampHeadTranslate, lampHeadSelfScale,
            lampTailTransform, lampHeadBackTransform, lampHeadEarLeftTransform, lampHeadEarRightTransform;
    private ModelNode lampBaseNode_1, lampBaseNode_2,
            lampLowerJointNode, lampLowerArmNode, lampUpperJointNode, lampUpperArmNode,
            lampHeadJointNode, lampHeadNode,
            lampTailNode, lampHeadBackNode, lampHeadEarLeftNode, lampHeadEarRightNode;

    private SGNode lampRoot;
    public NameNode lampHeadName;
    public TransformNode lampTranslate,
            lampLowerJointYRotate, lampLowerJointZRotate,
            lampUpperJointYRotate, lampUpperJointZRotate,
            lampHeadJointYRotate, lampHeadJointZRotate,
            lampHeadYRotate, lampHeadZRotate,
            lampTailXRotate, lampTailYRotate, lampTailZRotate;

    public Lamp(
            GL3 gl3, Camera camera, Light light1, Light light2, MovingLight movingLight,
            float LAMP_POSITION_X, float LAMP_POSITION_Y, float LAMP_POSITION_Z,
            float LAMP_ARM_HEIGHT
    ) {
        super(gl3, camera, light1, light2, movingLight);

        this.LAMP_POSITION_X = LAMP_POSITION_X;
        this.LAMP_POSITION_Y = LAMP_POSITION_Y;
        this.LAMP_POSITION_Z = LAMP_POSITION_Z;

        this.LAMP_ARM_HEIGHT = LAMP_ARM_HEIGHT;
    }

    public void generateLampBase(
            float LAMP_BASE_LENGTH, float LAMP_BASE_HEIGHT, float LAMP_BASE_WIDTH, int[] textureId_LampBase01
            ) {
        Mesh lampBaseMesh = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        Shader lampBaseShader = new Shader(gl3, "shader/vs_lamp.txt", "shader/fs_lamp.txt");
        Material lampBaseMaterial = new Material(
                new Vec3(0.55f, 0.5f, 0.45f),
                new Vec3(0.6f, 0.5f, 0.4f),
                new Vec3(0.1f, 0.1f, 0.1f), 1.0f
        );
        lamp_base = new Model(
                gl3, camera, light1, light2, movingLight, lampBaseShader, lampBaseMaterial, new Mat4(1), lampBaseMesh, textureId_LampBase01
        );
        Mat4 lampBaseModelMatrix = Mat4Transform.scale(LAMP_BASE_LENGTH, LAMP_BASE_HEIGHT, LAMP_BASE_WIDTH);
        lampBaseScale = new TransformNode("lamp base scale", lampBaseModelMatrix);
        lampBaseName = new NameNode("lamp base");
        Mat4 lampBaseModelMatrix2 = Mat4Transform.rotateAroundY(45);
        lampBaseRotate = new TransformNode("lamp base rotate", lampBaseModelMatrix2);
        lampBaseNode_1 = new ModelNode("lamp base 1", lamp_base);
        lampBaseNode_2 = new ModelNode("lamp base 2", lamp_base);
    }

    public void generateLampJoints(
            float LAMP_JOINT_DIAMETER,
            double lowerPressYInitialDegree, double lowerPressZInitialDegree,
            double upperPressYInitialDegree, double upperPressZInitialDegree,
            int[] textureId_LampJoint01
    ) {
        Mesh lampJointMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader lampJointShader = new Shader(gl3, "shader/vs_lamp.txt", "shader/fs_lamp.txt");
        Material lampJointMaterial = new Material(
                new Vec3(0.6f, 0.5f, 0.4f),
                new Vec3(0.6f, 0.5f, 0.4f),
                new Vec3(0.4f, 0.4f, 0.4f), 32.0f
        );
        lamp_joint = new Model(
                gl3, camera, light1, light2, movingLight, lampJointShader, lampJointMaterial, new Mat4(1), lampJointMesh, textureId_LampJoint01
        );
        Mat4 lampJointModelMatrix = Mat4Transform.scale(LAMP_JOINT_DIAMETER, LAMP_JOINT_DIAMETER, LAMP_JOINT_DIAMETER);
        lampJointScale = new TransformNode("lamp joint scale", lampJointModelMatrix);

        /*
        lamp lower joint
         */
        Mat4 lampLowerJointMatrix = Mat4Transform.translate(0, 0.4f, 0);
        lampLowerJointTranslate = new TransformNode("lamp lower joint translate", lampLowerJointMatrix);
        lampLowerJointName = new NameNode("lamp lower joint");
        Mat4 lampLowerJointYRotateMatrix = Mat4Transform.rotateAroundY((float) lowerPressYInitialDegree);
        Mat4 lampLowerJointZRotateMatrix = Mat4Transform.rotateAroundZ((float) lowerPressZInitialDegree);
        lampLowerJointYRotate = new TransformNode("lamp lower joint y rotate", lampLowerJointYRotateMatrix);
        lampLowerJointZRotate = new TransformNode("lamp lower joint z rotate", lampLowerJointZRotateMatrix);
        lampLowerJointNode = new ModelNode("lamp lower joint", lamp_joint);

        /*
        lamp upper joint
         */
        Mat4 lampUpperJointMatrix = Mat4Transform.translate(0, LAMP_ARM_HEIGHT, 0);
        lampUpperJointTranslate = new TransformNode("lamp upper joint translate", lampUpperJointMatrix);
        lampUpperJointName = new NameNode("lamp upper joint");
        Mat4 lampUpperJointYRotateMatrix = Mat4Transform.rotateAroundY((float) upperPressYInitialDegree);
        Mat4 lampUpperJointZRotateMatrix = Mat4Transform.rotateAroundZ((float) upperPressZInitialDegree);
        lampUpperJointYRotate = new TransformNode("lamp upper joint y rotate", lampUpperJointYRotateMatrix);
        lampUpperJointZRotate = new TransformNode("lamp upper joint z rotate", lampUpperJointZRotateMatrix);
        lampUpperJointNode = new ModelNode("lamp upper joint", lamp_joint);
    }

    public void generateArms(float LAMP_ARM_LENGTH, float LAMP_ARM_WIDTH, int[] textureId_LampArm01) {
        Mesh lampArmMesh = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        Shader lampArmShader = new Shader(gl3, "shader/vs_lamp.txt", "shader/fs_lamp.txt");
        Material lampArmMaterial = new Material(
                new Vec3(0.35f, 0.35f, 0.40f),
                new Vec3(0.5f, 0.55f, 0.6f),
                new Vec3(1.0f, 1.0f, 1.0f), 256.0f
        );
        lamp_arm = new Model(
                gl3, camera, light1, light2, movingLight, lampArmShader, lampArmMaterial, new Mat4(1), lampArmMesh, textureId_LampArm01
        );
        Mat4 lampArmModelMatrix = Mat4Transform.scale(LAMP_ARM_LENGTH, LAMP_ARM_HEIGHT, LAMP_ARM_WIDTH);
        lampArmScale = new TransformNode("lamp arm scale", lampArmModelMatrix);

        /*
        lamp lower arm
         */
        Mat4 lampLowerArmMatrix = Mat4Transform.translate(0, 0.4f, 0);
        lampLowerArmTranslate = new TransformNode("lamp lower arm translate", lampLowerArmMatrix);
        lampLowerArmName = new NameNode("lamp lower arm");
        lampLowerArmNode = new ModelNode("lamp lower arm", lamp_arm);

        /*
        lamp upper arm
         */
        Mat4 lampUpperArmMatrix = Mat4Transform.translate(0, LAMP_ARM_HEIGHT / 2, 0);
        lampUpperArmMatrix = Mat4.multiply(lampUpperArmMatrix, lampArmModelMatrix);
        lampUpperArmTranslate = new TransformNode("lamp upper arm translate", lampUpperArmMatrix);
        lampUpperArmName = new NameNode("lamp upper arm");
        lampUpperArmNode = new ModelNode("lamp upper arm", lamp_arm);
    }

    public void generateHead(
            float LAMP_HEAD_JOINT_DIAMETER, float LAMP_HEAD_XZ_SCALE, float LAMP_HEAD_Y_SCALE,
            double headJointYInitialDegree, double headJointZInitialDegree,
            int[] textureId_LampHeadJoint01, int[] textureId_LampHead01
    ) {
        /*
        lamp head joint
         */
        Mesh lampHeadJointMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader lampHeadJointShader = new Shader(gl3, "shader/vs_lamp.txt", "shader/fs_lamp.txt");
        Material lampHeadJointMaterial = new Material(
                new Vec3(0.65f, 0.45f, 0.5f),
                new Vec3(0.6f, 0.5f, 0.4f),
                new Vec3(0.2f, 0.2f, 0.2f), 32.0f
        );
        head_joint = new Model(
                gl3, camera, light1, light2, movingLight, lampHeadJointShader, lampHeadJointMaterial, new Mat4(1), lampHeadJointMesh, textureId_LampHeadJoint01
        );
        Shader lampHeadShader = new Shader(gl3, "shader/vs_lamp.txt", "shader/fs_lamp.txt");
        Material lampHeadMaterial = new Material(
                new Vec3(0.6f, 0.55f, 0.5f),
                new Vec3(0.6f, 0.45f, 0.4f),
                new Vec3(0.3f, 0.3f, 0.3f), 64.0f
        );

        /*
        lamp head main
         */
        Mesh lampHeadMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        lamp_head = new Model(
                gl3, camera, light1, light2, movingLight, lampHeadShader, lampHeadMaterial, new Mat4(1), lampHeadMesh, textureId_LampHead01
        );

        Mat4 lampHeadJointMatrix = Mat4Transform.translate(0, LAMP_ARM_HEIGHT, 0);
        Mat4 lampHeadJointSelfMatrix = Mat4Transform.scale(
                LAMP_HEAD_JOINT_DIAMETER, LAMP_HEAD_JOINT_DIAMETER, LAMP_HEAD_JOINT_DIAMETER
        );
        lampHeadJointTranslate = new TransformNode("lamp head joint translate", lampHeadJointMatrix);
        lampHeadJointSelfScale = new TransformNode("lamp head joint self scale", lampHeadJointSelfMatrix);
        lampHeadJointName = new NameNode("lamp head joint");
        Mat4 lampHeadJointYRotateMatrix = Mat4Transform.rotateAroundY((float) headJointYInitialDegree);
        Mat4 lampHeadJointZRotateMatrix = Mat4Transform.rotateAroundZ((float) headJointZInitialDegree);
        lampHeadJointYRotate = new TransformNode("lamp head joint y rotate", lampHeadJointYRotateMatrix);
        lampHeadJointZRotate = new TransformNode("lamp head joint z rotate", lampHeadJointZRotateMatrix);
        lampHeadJointNode = new ModelNode("lamp head joint", head_joint);

        Mat4 lampHeadMatrix = Mat4Transform.translate(1f, LAMP_HEAD_JOINT_DIAMETER / 2, 0);
        Mat4 lampHeadSelfMatrix = Mat4Transform.scale(LAMP_HEAD_XZ_SCALE, LAMP_HEAD_Y_SCALE, LAMP_HEAD_XZ_SCALE);
        lampHeadTranslate = new TransformNode("lamp head translate", lampHeadMatrix);
        lampHeadSelfScale = new TransformNode("lamp head self scale", lampHeadSelfMatrix);
        lampHeadName = new NameNode("lamp head");
        Mat4 lampHeadYRotateMatrix = Mat4Transform.rotateAroundY(0);
        Mat4 lampHeadZRotateMatrix = Mat4Transform.rotateAroundZ(0);
        lampHeadYRotate = new TransformNode("lamp head y rotate", lampHeadYRotateMatrix);
        lampHeadZRotate = new TransformNode("lamp head z rotate", lampHeadZRotateMatrix);
        lampHeadNode = new ModelNode("lamp head", lamp_head);
    }

    public void generateTail(float LAMP_TAIL_SCALE_X, float LAMP_TAIL_SCALE_Y, float LAMP_TAIL_SCALE_Z, int[] textureId_LampTail01) {
        Mesh lampTailMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader lampTailShader = new Shader(gl3, "shader/vs_lamp.txt", "shader/fs_lamp.txt");
        Material lampTailMaterial = new Material(
                new Vec3(0.45f, 0.35f, 0.25f),
                new Vec3(0.6f, 0.55f, 0.5f),
                new Vec3(0.2f, 0.2f, 0.2f), 8.0f
        );
        lamp_tail = new Model(
                gl3, camera, light1, light2, movingLight, lampTailShader, lampTailMaterial, new Mat4(1), lampTailMesh, textureId_LampTail01
        );
        Mat4 lampTailModelMatrix = Mat4Transform.scale(LAMP_TAIL_SCALE_X, LAMP_TAIL_SCALE_Y, LAMP_TAIL_SCALE_Z);
        lampTailModelMatrix = Mat4.multiply(Mat4Transform.translate(-LAMP_TAIL_SCALE_X, 0, 0), lampTailModelMatrix);
        lampTailTransform = new TransformNode("lamp tail transform", lampTailModelMatrix);
        Mat4 lampTailXRotateMatrix = Mat4Transform.rotateAroundX(0);
        Mat4 lampTailYRotateMatrix = Mat4Transform.rotateAroundY(0);
        Mat4 lampTailZRotateMatrix = Mat4Transform.rotateAroundZ(0);
        lampTailXRotate = new TransformNode("lamp tail x rotate", lampTailXRotateMatrix);
        lampTailYRotate = new TransformNode("lamp tail y rotate", lampTailYRotateMatrix);
        lampTailZRotate = new TransformNode("lamp tail z rotate", lampTailZRotateMatrix);
        lampTailName = new NameNode("lamp tail");
        lampTailNode = new ModelNode("lamp tail", lamp_tail);
    }

    public void generateDecoration(
            float LAMP_HEAD_BACK_DIAMETER, float LAMP_HEAD_BACK_Y_SCALE,
            float LAMP_HEAD_EAR_X_SCALE, float LAMP_HEAD_EAR_Y_SCALE, float LAMP_HEAD_EAR_Z_SCALE,
            float LAMP_HEAD_EAR_X_POSITION, float LAMP_HEAD_EAR_Y_POSITION, float LAMP_HEAD_EAR_Z_POSITION,
            int[] textureId_LampHeadBack01, int[] textureId_LampHeadEar01
    ) {
        /*
        lamp head back
         */
        Mesh lampHeadBackMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader lampHeadBackShader = new Shader(gl3, "shader/vs_lamp.txt", "shader/fs_lamp.txt");
        Material lampHeadBackMaterial = new Material(
                new Vec3(0.6f, 0.4f, 0.35f),
                new Vec3(0.65f, 0.6f, 0.5f),
                new Vec3(0.15f, 0.15f, 0.15f), 8.0f
        );
        lamp_head_back = new Model(
                gl3, camera, light1, light2, movingLight, lampHeadBackShader, lampHeadBackMaterial, new Mat4(1), lampHeadBackMesh, textureId_LampHeadBack01
        );
        Mat4 lampHeadBackModelMatrix = Mat4Transform.scale(
                LAMP_HEAD_BACK_DIAMETER, LAMP_HEAD_BACK_DIAMETER * LAMP_HEAD_BACK_Y_SCALE, LAMP_HEAD_BACK_DIAMETER
        );
        lampHeadBackModelMatrix = Mat4.multiply(
                Mat4Transform.translate(
                        0, LAMP_HEAD_BACK_DIAMETER * LAMP_HEAD_BACK_Y_SCALE / 1.5f, 0
                        )
                , lampHeadBackModelMatrix
        );
        lampHeadBackTransform = new TransformNode("lamp head transform", lampHeadBackModelMatrix);
        lampHeadBackName = new NameNode("lamp head back");
        lampHeadBackNode = new ModelNode("lamp head back", lamp_head_back);

        /*
        lamp head ear
         */
        Mesh lampHeadEarMesh = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader lampHeadEarShader = new Shader(gl3, "shader/vs_lamp.txt", "shader/fs_lamp.txt");
        Material lampHeadEarMaterial = new Material(
                new Vec3(0.6f, 0.5f, 0.1f),
                new Vec3(0.6f, 0.4f, 0.4f),
                new Vec3(0.1f, 0.1f, 0.1f), 8.0f
        );
        lamp_head_left_ear = new Model(
                gl3, camera, light1, light2, movingLight, lampHeadEarShader, lampHeadEarMaterial, new Mat4(1), lampHeadEarMesh, textureId_LampHeadEar01
        );
        Mat4 lampHeadEarLeftModelMatrix = Mat4Transform.scale(
                LAMP_HEAD_EAR_X_SCALE, LAMP_HEAD_EAR_Y_SCALE, LAMP_HEAD_EAR_Z_SCALE
        );
        lampHeadEarLeftModelMatrix = Mat4.multiply(
                Mat4Transform.translate(LAMP_HEAD_EAR_X_POSITION, LAMP_HEAD_EAR_Y_POSITION, -LAMP_HEAD_EAR_Z_POSITION), lampHeadEarLeftModelMatrix
        );
        lampHeadEarLeftTransform = new TransformNode("lamp head ear left transform", lampHeadEarLeftModelMatrix);
        lampHeadEarLeftName = new NameNode("lamp head ear left");
        lampHeadEarLeftNode = new ModelNode("lamp head ear left", lamp_head_left_ear);
        lamp_head_right_ear = new Model(
                gl3, camera, light1, light2, movingLight, lampHeadEarShader, lampHeadEarMaterial, new Mat4(1), lampHeadEarMesh, textureId_LampHeadEar01
        );
        Mat4 lampHeadEarRightModelMatrix = Mat4Transform.scale(
                LAMP_HEAD_EAR_X_SCALE, LAMP_HEAD_EAR_Y_SCALE, LAMP_HEAD_EAR_Z_SCALE
        );
        lampHeadEarRightModelMatrix = Mat4.multiply(
                Mat4Transform.translate(
                        LAMP_HEAD_EAR_X_POSITION, LAMP_HEAD_EAR_Y_POSITION, LAMP_HEAD_EAR_Z_POSITION
                ), lampHeadEarRightModelMatrix
        );
        lampHeadEarRightTransform = new TransformNode("lamp head ear right transform", lampHeadEarRightModelMatrix);
        lampHeadEarRightName = new NameNode("lamp head ear right");
        lampHeadEarRightNode = new ModelNode("lamp head ear right", lamp_head_right_ear);
    }

    @Override
    void initialise() {
        lampRoot = new NameNode("lamp root");
        lampTranslate = new TransformNode(
                "lamp transform", Mat4Transform.translate(LAMP_POSITION_X, LAMP_POSITION_Y, LAMP_POSITION_Z)
        );
    }

    @Override
    void buildTree() {
        lampRoot.addChild(lampTranslate);
            lampTranslate.addChild(lampBaseScale);
                lampBaseScale.addChild(lampBaseName);
                    lampBaseName.addChild(lampBaseNode_1);
                    lampBaseName.addChild(lampBaseRotate);
                        lampBaseRotate.addChild(lampBaseNode_2);
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

        lampRoot.print(0, false);
    }

    @Override
    void update() {
        lampRoot.update();
    }

    @Override
    void dispose(GL3 gl3) {
        Model[] models = new Model[] {
                lamp_base, lamp_joint, lamp_arm, head_joint, lamp_head,
                lamp_tail, lamp_head_back, lamp_head_left_ear, lamp_head_right_ear
        };
        for (Model model: models) {
            model.dispose(gl3);
        }
    }

    @Override
    public void draw(GL3 gl3) {
        lampRoot.draw(gl3);
    }
}
