import com.jogamp.opengl.GL3;

abstract class SceneGraphObject {
    public GL3 gl3;
    public Camera camera;
    public Light light1, light2;
    public MovingLight movingLight;

    public SceneGraphObject(GL3 gl3, Camera camera, Light light1, Light light2, MovingLight movingLight) {
        this.gl3 = gl3;
        this.camera = camera;
        this.light1 = light1;
        this.light2 = light2;
        this.movingLight = movingLight;
    }

    abstract void initialise();
    abstract void buildTree();
    abstract void update();
    public void execute() {
        initialise();
        buildTree();
        update();
    }
    abstract void dispose(GL3 gl3);
    public abstract void draw(GL3 gl3);
}
