/* I declare that this code is my own work */
/* Author <Junxiang Chen> <jchen115@sheffield.ac.uk> */
/*
moving light (light bulb)
 */

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import gmaths.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * constructor
 */
public class MovingLight {
    private Material material;
    public Shader shader;
    private Camera camera;
    private Vec3 localPosition;
    private Vec3 worldPosition;
    private Vec3 localDirection;
    private Vec3 worldDirection;
    private Mat4 worldMatrix;
    private int[] vertexBufferId = new int[1];
    private int[] vertexArrayId = new int[1];
    private int[] elementBufferId = new int[1];

    private float[] vertices = setLightSize();
    private int[] indices = Sphere.indices;
    private int vertexStride = 3;
    private int vertexXYZFloats = 3;

    /**
     * set light bulb size
     *
     * @return vertices
     */
    private float[] setLightSize() {
        float[] vertices = Sphere.vertices.clone();
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = vertices[i] * 0.65f;
        }
        return vertices;
    }

    /*
    parameters of spotlight
     */
    private float constant;
    private float linear;
    private float quadratic;
    private float cutOff;
    private float outerCutOff;

    /**
     * constructor
     *
     * @param gl3            GL3 parameter
     * @param localPosition  local position (local coordinate)
     * @param worldMatrix    world matrix (transform local coordinate to world)
     * @param localDirection local direction of light
     */
    public MovingLight(GL3 gl3, Vec3 localPosition, Mat4 worldMatrix, Vec3 localDirection) {
        material = new Material();
        material.setAmbient(0.5f, 0.5f, 0.5f);
        material.setDiffuse(0.8f, 0.8f, 0.8f);
        material.setSpecular(0.8f, 0.8f, 0.8f);
        this.localPosition = localPosition;
        this.worldMatrix = worldMatrix;
        this.localDirection = localDirection;
        shader = new Shader(gl3, "shader/vs_light_01.txt", "shader/fs_light_01.txt");
        fillBuffers(gl3);
    }

    /**
     * fill buffers
     *
     * @param gl3 GL3 parameter
     */
    public void fillBuffers(GL3 gl3) {
        gl3.glGenVertexArrays(1, vertexArrayId, 0);
        gl3.glBindVertexArray(vertexArrayId[0]);
        gl3.glGenBuffers(1, vertexBufferId, 0);
        gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
        FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);

        gl3.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);

        int stride = vertexStride;
        int numXYZFloats = vertexXYZFloats;
        int offset = 0;
        gl3.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride * Float.BYTES, offset);
        gl3.glEnableVertexAttribArray(0);

        gl3.glGenBuffers(1, elementBufferId, 0);
        IntBuffer ib = Buffers.newDirectIntBuffer(indices);
        gl3.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
        gl3.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
        gl3.glBindVertexArray(0);
    }

    /**
     * set world position
     */
    public void setWorldPosition() {
        Vec4 position = new Vec4(this.localPosition, 1);
        Vec4 result = Vec4.multiplyMatrix(this.worldMatrix, position);
        this.worldPosition = result.toVec3();
    }

    /**
     * get position in world coordinate
     *
     * @return position in world coordinate
     */
    public Vec3 getWorldPosition() {
        return worldPosition;
    }

    /**
     * set world matrix
     *
     * @param worldMatrix Mat4 parameter
     */
    public void setWorldMatrix(Mat4 worldMatrix) {
        this.worldMatrix = worldMatrix;
    }

    /**
     * get matrix that transform local coordinate to world
     *
     * @return
     */
    public Mat4 getWorldMatrix() {
        return worldMatrix;
    }

    /**
     * set direction from local coordinate to world
     */
    public void setWorldDirection() {
        Vec4 result = Vec4.multiplyMatrix(this.worldMatrix, new Vec4(localDirection, 0));
//        result.x = result.x * 0.285f;
//        result.z = result.z * -0.001f;
        result.z = result.z * (-0.05f);
        this.worldDirection = result.toVec3();
    }

    /**
     * get direction in world coordination
     *
     * @return direction under world coordination
     */
    public Vec3 getWorldDirection() {
        return worldDirection;
    }

    /**
     * set material of light
     *
     * @param material Material parameter
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * get material information
     *
     * @return Material parameter
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * set camera
     *
     * @param camera Camera parameter
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * set constant
     *
     * @param constant float parameter
     */
    public void setConstant(float constant) {
        this.constant = constant;
    }

    /**
     * get constant
     *
     * @return float parameter
     */
    public float getConstant() {
        return this.constant;
    }

    /**
     * set linear
     *
     * @param linear float parameter
     */
    public void setLinear(float linear) {
        this.linear = linear;
    }

    /**
     * get linear
     *
     * @return float parameter
     */
    public float getLinear() {
        return this.linear;
    }

    /**
     * set quadratic
     *
     * @param quadratic float parameter
     */
    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }

    /**
     * get quadratic
     *
     * @return float parameter
     */
    public float getQuadratic() {
        return this.quadratic;
    }

    /**
     * set inner cut off
     *
     * @param cutOff float parameter
     */
    public void setCutOff(float cutOff) {
        this.cutOff = (float) Math.cos(Math.toRadians(cutOff));
    }

    /**
     * get inner cut off
     *
     * @return float parameter
     */
    public float getCutOff() {
        return this.cutOff;
    }

    /**
     * set outer cut off
     *
     * @param outerCutOff float parameter
     */
    public void setOuterCutOff(float outerCutOff) {
        this.outerCutOff = (float) Math.cos(Math.toRadians(outerCutOff));
    }

    /**
     * get outer cut off
     *
     * @return float parameter
     */
    public float getOuterCutOff() {
        return this.outerCutOff;
    }

    /**
     * dispose buffer
     *
     * @param gl3 GL3 parameter
     */
    public void dispose(GL3 gl3) {
        gl3.glDeleteBuffers(1, vertexBufferId, 0);
        gl3.glDeleteVertexArrays(1, vertexArrayId, 0);
        gl3.glDeleteBuffers(1, elementBufferId, 0);
    }

    /**
     * render light
     *
     * @param gl3
     */
    public void render(GL3 gl3) {
        setWorldPosition();
        setWorldDirection();
        Mat4 mvpMatrix = Mat4.multiply(
                camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), worldMatrix)
        );

        shader.use(gl3);
        shader.setFloatArray(gl3, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        gl3.glBindVertexArray(vertexArrayId[0]);
        gl3.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl3.glBindVertexArray(0);
    }
}
