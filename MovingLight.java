import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import gmaths.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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

    private float[] setLightSize() {
        float[] vertices = Sphere.vertices.clone();
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = vertices[i] * 0.65f;
        }
        return vertices;
    }

    private float constant;
    private float linear;
    private float quadratic;
    private float cutOff;
    private float outerCutOff;

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
        gl3.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
        gl3.glEnableVertexAttribArray(0);

        gl3.glGenBuffers(1, elementBufferId, 0);
        IntBuffer ib = Buffers.newDirectIntBuffer(indices);
        gl3.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
        gl3.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
        gl3.glBindVertexArray(0);
    }

    public void setWorldPosition() {
        Vec4 position = new Vec4(this.localPosition, 1);
        Vec4 result = Vec4.multiplyMatrix(this.worldMatrix, position);
        this.worldPosition = result.toVec3();
    }

    public Vec3 getWorldPosition() {
        return worldPosition;
    }

    public void setWorldMatrix(Mat4 worldMatrix) {
        this.worldMatrix = worldMatrix;
    }

    public Mat4 getWorldMatrix() {
        return worldMatrix;
    }

    public void setWorldDirection() {
        Vec4 result = Vec4.multiplyMatrix(this.worldMatrix, new Vec4(localDirection, 0));
        result.x = result.x * 0.285f;
        result.z = result.z * -0.001f;
        this.worldDirection = result.toVec3();
    }

    public Vec3 getWorldDirection() {
        return worldDirection;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getConstant() {
        return this.constant;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getLinear() {
        return this.linear;
    }

    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }

    public float getQuadratic() {
        return this.quadratic;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = (float) Math.cos(Math.toRadians(cutOff));
    }

    public float getCutOff() {
        return this.cutOff;
    }

    public void setOuterCutOff(float outerCutOff) {
        this.outerCutOff = (float) Math.cos(Math.toRadians(outerCutOff));
    }

    public float getOuterCutOff() {
        return this.outerCutOff;
    }

    public void dispose(GL3 gl3) {
        gl3.glDeleteBuffers(1, vertexBufferId, 0);
        gl3.glDeleteVertexArrays(1, vertexArrayId, 0);
        gl3.glDeleteBuffers(1, elementBufferId, 0);
    }

    public void render(GL3 gl3) {
        setWorldPosition();
        setWorldDirection();
        Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), worldMatrix));

        shader.use(gl3);
        shader.setFloatArray(gl3, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        gl3.glBindVertexArray(vertexArrayId[0]);
        gl3.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl3.glBindVertexArray(0);
    }
}
