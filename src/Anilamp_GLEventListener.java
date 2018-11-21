import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import gmaths.Vec4;

public class Anilamp_GLEventListener implements GLEventListener {
    private Camera camera;
    private Model floor, table_body;
    private Light light;
    private MovingLight movingLight;
    private SGNode tableRoot;
    private ModelNode tableLegLT;
    private TransformNode translateTableLegLT;
    private float currentTime;

    private double getCurrentTime() {
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

        light = new Light(gl3);
        light.setCamera(camera);

        Mesh floorMesh = new Mesh(gl3, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader floorShader = new Shader(gl3, "shader/vs_floor.txt", "shader/fs_floor.txt");
        Material floorMaterial = new Material(
                new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f
        );
        Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
        floor = new Model(gl3, camera, light, floorShader, floorMaterial, modelMatrix, floorMesh, textureId0);

        Mesh tableBodyMesh = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        Shader tableBodyShader = new Shader(gl3, "shader/vs_floor.txt", "shader/fs_floor.txt");
        table_body = new Model(gl3, camera, light, tableBodyShader, floorMaterial, modelMatrix, tableBodyMesh);
        tableRoot = new NameNode("root");
        TransformNode tablePositionTranslate = new TransformNode("table transform", Mat4Transform.translate(0, 0, 0));
        NameNode tableBody = new NameNode("body");
        ModelNode tableBodyNode = new ModelNode("table body", table_body);
        Mat4 lt = Mat4Transform.scale(0.1f, 0.1f, 0.1f);
        lt = Mat4.multiply(Mat4Transform.translate(0, 6f, 0), lt);
        translateTableLegLT = new TransformNode("transform table lt", lt);
        tableLegLT = new ModelNode("table leg lt", table_body);

        tableRoot.addChild(tablePositionTranslate);
        tablePositionTranslate.addChild(tableBody);
        tableBody.addChild(tableBodyNode);
        tableBodyNode.addChild(translateTableLegLT);
        translateTableLegLT.addChild(tableLegLT);


        tableBody.update();
//        tableBody.print(0, false);
        System.out.println(tableLegLT.worldTransform.toString());

        Mat4 modelMatrix2 = new Mat4(1);
        modelMatrix2 = Mat4.multiply(Mat4Transform.scale(4f, 4f, 4f), modelMatrix2);
        Mat4 worldT = Mat4.multiply(tableLegLT.worldTransform, modelMatrix2);
        movingLight = new MovingLight(gl3, worldT);
        movingLight.setCamera(camera);
    }

    public void updateLightPosition() {
        double currentTime = getCurrentTime();
        translateTableLegLT.setTransform(Mat4Transform.translate(0, 5 * (float) Math.sin(currentTime), 0));
        translateTableLegLT.update();
    }

    public void render(GL3 gl3) {
        gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        light.render(gl3);
        floor.render(gl3);
//        tableRoot.draw(gl3);
        updateLightPosition();
        Mat4 modelMatrix2 = new Mat4(1);
////        modelMatrix2 = Mat4.multiply(Mat4Transform.scale(1f, 1f, 1f), modelMatrix2);
//        Mat4 worldT = Mat4.multiply(translateTableLegLT.worldTransform, modelMatrix2);
        movingLight.setWorldMatrix(tableLegLT.worldTransform);
        movingLight.render(gl3);
    }
}
