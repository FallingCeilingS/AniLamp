import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import gmaths.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Light {
    private Material material;
    private Shader shader;
    private Camera camera;
    private Vec3 position;
    private Mat4 modelMatrix;
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

    public Light(GL3 gl3) {
        material = new Material();
        material.setAmbient(0.5f, 0.5f, 0.5f);
        material.setDiffuse(0.8f, 0.8f, 0.8f);
        material.setSpecular(0.8f, 0.8f, 0.8f);
        position = new Vec3(3f,2f,1f);
        modelMatrix = new Mat4(1);
        shader = new Shader(gl3, "vs_light_01.txt", "fs_light_01.txt");
        fillBuffers(gl3);
    }

    private void fillBuffers(GL3 gl) {
        gl.glGenVertexArrays(1, vertexArrayId, 0);
        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glGenBuffers(1, vertexBufferId, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
        FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);

        int stride = vertexStride;
        int numXYZFloats = vertexXYZFloats;
        int offset = 0;
        gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
        gl.glEnableVertexAttribArray(0);

        gl.glGenBuffers(1, elementBufferId, 0);
        IntBuffer ib = Buffers.newDirectIntBuffer(indices);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
        gl.glBindVertexArray(0);
    }

    public void setPosition(Vec3 v) {
        position.x = v.x;
        position.y = v.y;
        position.z = v.z;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public Vec3 getPosition() {
        return position;
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
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(position), modelMatrix);

        Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));

        shader.use(gl3);
        shader.setFloatArray(gl3, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        gl3.glBindVertexArray(vertexArrayId[0]);
        gl3.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl3.glBindVertexArray(0);
    }
}
