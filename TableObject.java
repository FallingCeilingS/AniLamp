import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;

public class TableObject {
    public double[] range;
    private float OBJ_SCALE_X, OBJ_SCALE_Y, OBJ_SCALE_Z, OBJ_X_POS, OBJ_Z_POS, TABLE_OBJ_Y_POS;
    private Mesh obj;
    private Camera camera;
    private Light light1, light2;
    private MovingLight movingLight;
    private Shader shader;
    private Material material;
    private Model object;
    private int[] textureId_Obj01, textureId_Obj02;

    public TableObject(
            float OBJ_SCALE_X, float OBJ_SCALE_Y, float OBJ_SCALE_Z,
            float OBJ_X_POS, float OBJ_Z_POS, float TABLE_OBJ_Y_POS,
            Camera camera, Light light1, Light light2, MovingLight movingLight, Material material, Shader shader,
            int[] textureId_Obj01, int[] textureId_Obj02
    ) {
        this.OBJ_SCALE_X = OBJ_SCALE_X;
        this.OBJ_SCALE_Y = OBJ_SCALE_Y;
        this.OBJ_SCALE_Z = OBJ_SCALE_Z;
        this.OBJ_X_POS = OBJ_X_POS;
        this.OBJ_Z_POS = OBJ_Z_POS;
        this.TABLE_OBJ_Y_POS = TABLE_OBJ_Y_POS;
        this.camera = camera;
        this.light1 = light1;
        this.light2 = light2;
        this.movingLight = movingLight;
        this.material = material;
        this.shader = shader;
        this.textureId_Obj01 = textureId_Obj01;
        this.textureId_Obj02 = textureId_Obj02;
    }

    public void generateModel(GL3 gl3, String type) {
        if (type.equals("cube")) {
            obj = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        } else if (type.equals("sphere")) {
            obj = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        }
        Mat4 objModelMatrix = Mat4Transform.scale(OBJ_SCALE_X, OBJ_SCALE_Y, OBJ_SCALE_Z);
        if (type.equals("sphere")) {
            objModelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(180), objModelMatrix);
        }
        objModelMatrix = Mat4.multiply(Mat4Transform.translate(OBJ_X_POS, TABLE_OBJ_Y_POS, OBJ_Z_POS), objModelMatrix);
        object = new Model(
                gl3, camera, light1, light2, movingLight, shader, material, objModelMatrix, obj, textureId_Obj01, textureId_Obj02
        );
        range = new double[]{OBJ_X_POS, OBJ_Z_POS, OBJ_SCALE_X, OBJ_SCALE_Z};
    }

    public void dispose(GL3 gl3) {
        object.dispose(gl3);
    }

    public void render(GL3 gl3) {
        object.render(gl3);
    }
}
