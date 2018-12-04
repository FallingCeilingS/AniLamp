import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class TableObject {
    public double[] range;
    private float OBJ_SCALE_X, OBJ_SCALE_Y, OBJ_SCALE_Z, OBJ_X_POS, OBJ_Z_POS, TABLE_OBJ_Y_POS;
    private Mesh obj;
    private Camera camera;
    private Light light1, light2;
    private MovingLight movingLight;
    private Model object;

    public TableObject(
            float OBJ_SCALE_X, float OBJ_SCALE_Y, float OBJ_SCALE_Z,
            float OBJ_X_POS, float OBJ_Z_POS, float TABLE_OBJ_Y_POS,
            Camera camera, Light light1, Light light2, MovingLight movingLight
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
    }

    public void generateModel(GL3 gl3, String type) {
        if (type.equals("cube")) {
            obj = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        } else if (type.equals("sphere")) {
            obj = new Mesh(gl3, Sphere.vertices.clone(), Sphere.indices.clone());
        }
        Shader shader = new Shader(gl3, "shader/vs_table_body.txt", "shader/fs_table_body.txt");
        Material obj_Material = new Material(
                new Vec3(0.2f, 0.2f, 0.2f),
                new Vec3(0.85f, 0.8f, 0.75f),
                new Vec3(0.2f, 0.2f, 0.2f), 16.0f
        );
        Mat4 obj1ModelMatrix = Mat4Transform.scale(OBJ_SCALE_X, OBJ_SCALE_Y, OBJ_SCALE_Z);
        obj1ModelMatrix = Mat4.multiply(Mat4Transform.translate(OBJ_X_POS, TABLE_OBJ_Y_POS, OBJ_Z_POS), obj1ModelMatrix);
        object = new Model(gl3, camera, light1, light2, movingLight, shader, obj_Material, obj1ModelMatrix, obj);
        range = new double[] {OBJ_X_POS, OBJ_Z_POS, OBJ_SCALE_X, OBJ_SCALE_Z};
    }

    public void render(GL3 gl3) {
        object.render(gl3);
    }
}
