/* This code is from exercise sheet written by Dr. Steve Maddock */
/*
the abstract class generate objects organised by scene graph
 */

import com.jogamp.opengl.GL3;

abstract class SceneGraphObject {
    public GL3 gl3;
    public Camera camera;
    public Light light1, light2;
    public MovingLight movingLight;

    /**
     * constructor
     * @param gl3 GL3 parameter
     * @param camera Camera parameter
     * @param light1 Light parameter
     * @param light2 Light parameter
     * @param movingLight MovingLight parameter
     */
    public SceneGraphObject(GL3 gl3, Camera camera, Light light1, Light light2, MovingLight movingLight) {
        this.gl3 = gl3;
        this.camera = camera;
        this.light1 = light1;
        this.light2 = light2;
        this.movingLight = movingLight;
    }

    /**
     * initialise model
     */
    abstract void initialise();

    /**
     * build scene graph
     */
    abstract void buildTree();

    /**
     * update scene graph
     */
    abstract void update();

    /**
     * execute three functions above
     */
    public void execute() {
        initialise();
        buildTree();
        update();
    }

    /**
     * dispose buffer
     * @param gl3 GL3 element
     */
    abstract void dispose(GL3 gl3);

    /**
     * render the object
     * @param gl3 GL3 parameter
     */
    public abstract void draw(GL3 gl3);
}
