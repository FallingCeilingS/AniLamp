/* This code is from exercise sheet written by Dr. Steve Maddock */

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
    private float[] vertices;
    private int[] indices;
    private int vertexStride = 8;
    private int vertexXYZFloats = 3;
    private int vertexNormalFloats = 3;
    private int vertexTexFloats = 2;
    private int[] vertexBufferId = new int[1];
    private int[] vertexArrayId = new int[1];
    private int[] elementBufferId = new int[1];

    public Mesh(GL3 gl3, float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
        fillBuffers(gl3);
    }

    public void fillBuffers(GL3 gl3) {
        gl3.glGenVertexArrays(1, vertexArrayId, 0);
        gl3.glBindVertexArray(vertexArrayId[0]);
        gl3.glGenBuffers(1, vertexBufferId, 0);
        gl3.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
        FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(vertices);
        gl3.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, floatBuffer, GL.GL_STATIC_DRAW);
        int stride = vertexStride;
        int numXYZFloats = vertexXYZFloats;
        int offset = 0;
        gl3.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, Float.BYTES * stride, offset);
        gl3.glEnableVertexAttribArray(0);
        int numNormalFloats = vertexNormalFloats;
        offset = offset + Float.BYTES * numXYZFloats;
        gl3.glVertexAttribPointer(1, numNormalFloats, GL.GL_FLOAT, false, Float.BYTES * stride, offset);
        gl3.glEnableVertexAttribArray(1);
        int numTexFloats = vertexTexFloats;
        offset = offset + Float.BYTES * numNormalFloats;
        gl3.glVertexAttribPointer(2, numTexFloats, GL.GL_FLOAT, false, Float.BYTES * stride, offset);
        gl3.glEnableVertexAttribArray(2);
        gl3.glGenBuffers(1, elementBufferId, 0);
        gl3.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
        IntBuffer intBuffer = Buffers.newDirectIntBuffer(indices);
        gl3.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Float.BYTES * indices.length, intBuffer, GL.GL_STATIC_DRAW);
        gl3.glBindVertexArray(0);
    }

    public void dispose(GL3 gl3) {
        gl3.glDeleteBuffers(1, vertexBufferId, 0);
        gl3.glDeleteBuffers(1, vertexArrayId, 0);
        gl3.glDeleteBuffers(1, elementBufferId, 0);
    }

    public void render(GL3 gl3) {
        gl3.glBindVertexArray(vertexArrayId[0]);
        gl3.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl3.glBindVertexArray(0);
    }
}
