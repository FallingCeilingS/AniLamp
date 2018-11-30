import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Wall extends SceneGraphObject {
    private NameNode wallRoot, wallBottomName, wallBottomLeftName, wallBottomRightName,
            wallMiddleLeftName, wallMiddleRightName, wallTopName, wallTopLeftName, wallTopRightName;
    private TransformNode wallTransform, wallScale, wallBottomLeftTranslate, wallBottomRightTranslate,
            wallMiddleTransform, wallMiddleLeftTranslate, wallMiddleRightTranslate,
            wallTopTransform, wallTopLeftTranslate, wallTopRightTranslate;
    private ModelNode wallBottomNode, wallBottomLeftNode, wallBottomRightNode, wallLeftNode, wallRightNode,
            wallTopNode, wallTopLeftNode, wallTopRightNode;

    private float WALL_LENGTH;
    private float WALL_HEIGHT;
    private float WALL_WIDTH;
    private float WALL_X_POSITION;
    private float WALL_Y_POSITION;
    private float WALL_Z_POSITION;

    public Wall(GL3 gl3, Camera camera, Light light1, Light light2, MovingLight movingLight,
                float WALL_LENGTH, float WALL_HEIGHT, float WALL_WIDTH,
                float WALL_X_POSITION, float WALL_Y_POSITION, float WALL_Z_POSITION) {
        super(gl3, camera, light1, light2, movingLight);
        this.WALL_LENGTH = WALL_LENGTH;
        this.WALL_HEIGHT = WALL_HEIGHT;
        this.WALL_WIDTH = WALL_WIDTH;
        this.WALL_X_POSITION = WALL_X_POSITION;
        this.WALL_Y_POSITION = WALL_Y_POSITION;
        this.WALL_Z_POSITION = WALL_Z_POSITION;
    }

    @Override
    void initialise() {
        wallRoot = new NameNode("wall root");
        wallTransform = new TransformNode("wall transform", Mat4Transform.translate(WALL_X_POSITION, WALL_Y_POSITION, WALL_Z_POSITION));
        Mesh wallMesh = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        Shader wallShader = new Shader(gl3, "shader/vs_floor.txt", "shader/fs_table_body.txt");
        Material wallMaterial = new Material(
                new Vec3(0.6f, 0.4f, 0.61f),
                new Vec3(0.8f, 0.4f, 0.81f),
                new Vec3(0.3f, 0.3f, 0.3f), 32.0f
        );

        Model wall_bottom = new Model(gl3, camera, light1, light2, movingLight, wallShader, wallMaterial, new Mat4(1), wallMesh);
        Mat4 wallModelMatrix = Mat4Transform.scale(WALL_LENGTH, WALL_HEIGHT, WALL_WIDTH);
        wallScale = new TransformNode("table bottom and top scale", wallModelMatrix);
        wallBottomName = new NameNode("wall bottom");
        wallBottomNode = new ModelNode("wall bottom", wall_bottom);
        Model wall_bottom_left = new Model(gl3, camera, light1, light2, movingLight, wallShader, wallMaterial, new Mat4(1), wallMesh);
        wallBottomLeftName = new NameNode("wall bottom left name");
        Mat4 wallBottomLeftTranslateMatrix = Mat4Transform.translate(-1, 0, 0);
        wallBottomLeftTranslate = new TransformNode("wall bottom left translate", wallBottomLeftTranslateMatrix);
        wallBottomLeftNode = new ModelNode("wall bottom left node", wall_bottom_left);
        Model wall_bottom_right = new Model(gl3, camera, light1, light2, movingLight, wallShader, wallMaterial, new Mat4(1), wallMesh);
        wallBottomRightName = new NameNode("wall bottom right name");
        Mat4 wallBottomRightTranslateMatrix = Mat4Transform.translate(1, 0, 0);
        wallBottomRightTranslate = new TransformNode("wall bottom right translate", wallBottomRightTranslateMatrix);
        wallBottomRightNode = new ModelNode("wall bottom right node", wall_bottom_right);

        Mat4 wallMiddleModelMatrix = Mat4Transform.scale(WALL_LENGTH, WALL_HEIGHT, WALL_WIDTH);
        wallMiddleModelMatrix = Mat4.multiply(Mat4Transform.translate(0, WALL_HEIGHT, 0), wallMiddleModelMatrix);
        wallMiddleTransform = new TransformNode("wall middle left and right translate", wallMiddleModelMatrix);
        Model wall_middle_left = new Model(gl3, camera, light1, light2, movingLight, wallShader, wallMaterial, new Mat4(1), wallMesh);
        wallMiddleLeftName = new NameNode("wall middle left");
        Mat4 wallMiddleLeftTranslateMatrix = Mat4Transform.translate(-1, 0,0 );
        wallMiddleLeftTranslate = new TransformNode("wall middle left translate", wallMiddleLeftTranslateMatrix);
        wallLeftNode = new ModelNode("wall middle left model", wall_middle_left);
        Model wall_middle_right = new Model(gl3, camera, light1, light2, movingLight, wallShader, wallMaterial, new Mat4(1), wallMesh);
        wallMiddleRightName = new NameNode("wall middle right");
        Mat4 wallMiddleRightTranslateMatrix = Mat4Transform.translate(1, 0, 0);
        wallMiddleRightTranslate = new TransformNode("wall middle right translate", wallMiddleRightTranslateMatrix);
        wallRightNode = new ModelNode("wall middle right model", wall_middle_right);

        Model wall_top = new Model(gl3, camera, light1, light2, movingLight, wallShader, wallMaterial, new Mat4(1), wallMesh);
        Mat4 wallTopModelMatrix = Mat4Transform.scale(WALL_LENGTH, WALL_HEIGHT, WALL_WIDTH);
        wallTopModelMatrix = Mat4.multiply(Mat4Transform.translate(0, WALL_HEIGHT * 2, 0) ,wallTopModelMatrix);
        wallTopTransform = new TransformNode("table bottom and top scale", wallTopModelMatrix);
        wallTopName = new NameNode("wall top");
        wallTopNode = new ModelNode("wall top", wall_top);
        Model wall_top_left = new Model(gl3, camera, light1, light2, movingLight, wallShader, wallMaterial, new Mat4(1), wallMesh);
        wallTopLeftName = new NameNode("wall top left name");
        Mat4 wallTopLeftTranslateMatrix = Mat4Transform.translate(-1, 0, 0);
        wallTopLeftTranslate = new TransformNode("wall top left translate", wallTopLeftTranslateMatrix);
        wallTopLeftNode = new ModelNode("wall top left node", wall_top_left);
        Model wall_top_right = new Model(gl3, camera, light1, light2, movingLight, wallShader, wallMaterial, new Mat4(1), wallMesh);
        wallTopRightName = new NameNode("wall top right name");
        Mat4 wallTopRightTranslateMatrix = Mat4Transform.translate(1, 0, 0);
        wallTopRightTranslate = new TransformNode("wall top right translate", wallTopRightTranslateMatrix);
        wallTopRightNode = new ModelNode("wall bottom right node", wall_top_right);
    }

    @Override
    void buildTree() {
        wallRoot.addChild(wallTransform);
        wallTransform.addChild(wallScale);
        wallScale.addChild(wallBottomName);
        wallBottomName.addChild(wallBottomNode);
        wallScale.addChild(wallBottomLeftName);
        wallBottomLeftName.addChild(wallBottomLeftTranslate);
        wallBottomLeftTranslate.addChild(wallBottomLeftNode);
        wallScale.addChild(wallBottomRightName);
        wallBottomRightName.addChild(wallBottomRightTranslate);
        wallBottomRightTranslate.addChild(wallBottomRightNode);
        wallTransform.addChild(wallMiddleTransform);
        wallMiddleTransform.addChild(wallMiddleLeftName);
        wallMiddleLeftName.addChild(wallMiddleLeftTranslate);
        wallMiddleLeftTranslate.addChild(wallLeftNode);
        wallMiddleTransform.addChild(wallMiddleRightName);
        wallMiddleRightName.addChild(wallMiddleRightTranslate);
        wallMiddleRightTranslate.addChild(wallRightNode);
        wallTransform.addChild(wallTopTransform);
        wallTopTransform.addChild(wallTopName);
        wallTopName.addChild(wallTopNode);
        wallTopTransform.addChild(wallTopLeftName);
        wallTopLeftName.addChild(wallTopLeftTranslate);
        wallTopLeftTranslate.addChild(wallTopLeftNode);
        wallTopTransform.addChild(wallTopRightName);
        wallTopRightName.addChild(wallTopRightTranslate);
        wallTopRightTranslate.addChild(wallTopRightNode);
    }

    @Override
    void update() {
        wallRoot.update();
    }

    @Override
    public void draw(GL3 gl3) {
        wallRoot.draw(gl3);
    }
}
