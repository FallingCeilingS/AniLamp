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
    // ***************************************************
    /* THE DATA
     */
    // anticlockwise/counterclockwise ordering

    private float[] vertices = new float[] {  // x,y,z
            -0.5f, -0.5f, -0.5f,  // 0
            -0.5f, -0.5f,  0.5f,  // 1
            -0.5f,  0.5f, -0.5f,  // 2
            -0.5f,  0.5f,  0.5f,  // 3
            0.5f, -0.5f, -0.5f,  // 4
            0.5f, -0.5f,  0.5f,  // 5
            0.5f,  0.5f, -0.5f,  // 6
            0.5f,  0.5f,  0.5f   // 7
    };

    private int[] indices =  new int[] {
            0,1,3, // x -ve
            3,2,0, // x -ve
            4,6,7, // x +ve
            7,5,4, // x +ve
            1,5,7, // z +ve
            7,3,1, // z +ve
            6,4,0, // z -ve
            0,2,6, // z -ve
            0,4,5, // y -ve
            5,1,0, // y -ve
            2,3,7, // y +ve
            7,6,2  // y +ve
    };
    private int vertexStride = 3;
    private int vertexXYZFloats = 3;

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
        result.z = result.z * -0.005f;
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
