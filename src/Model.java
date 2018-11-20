import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Vec3;

public class Model {
    private Camera camera;
    private Light light;
    private Shader shader;
    private Material material;
    private Mat4 modelMatrix;
    private Mesh mesh;

    private int[] textureId1;
    private int[] textureId2;

    public Model(
            GL3 gl3, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh,
            int[] textureId1, int[] textureId2
    ) {
        this.camera = camera;
        this.light = light;
        this.shader = shader;
        this.material = material;
        this.modelMatrix = modelMatrix;
        this.mesh = mesh;
        this.textureId1 = textureId1;
        this.textureId2 = textureId2;
    }

    public Model(
            GL3 gl3, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh,
            int[] textureId1
    ) {
        this(gl3, camera, light, shader, material, modelMatrix, mesh, textureId1, null);
    }

    public Model(
            GL3 gl3, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh
    ) {
        this(gl3, camera, light, shader, material, modelMatrix, mesh, null, null);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    public void setModelMatrix(Mat4 modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public void dispose(GL3 gl3) {
        mesh.dispose(gl3);
        if (textureId1 != null) {
            gl3.glDeleteBuffers(1, textureId1, 0);
        }
        if (textureId2 != null) {
            gl3.glDeleteBuffers(1, textureId2, 0);
        }
    }

    public void render(GL3 gl3, Mat4 modelMatrix) {
//        Mat4 mvpMatrix = Mat4.multiply(
//                camera.getPerspectiveMatrix(),
//                Mat4.multiply(
//                        camera.getViewMatrix(),
//                        modelMatrix
//                )
//                );
        mesh.render(gl3);
        shader.use(gl3);
        shader.setFloatArray(gl3, "model", modelMatrix.toFloatArrayForGLSL());
        shader.setFloatArray(gl3, "mvpMatrix", modelMatrix.toFloatArrayForGLSL());
        shader.setVec3(gl3, "viewPos", new Vec3(2, 3, -1));
        shader.setVec3(gl3, "light.position", light.getPosition());
        shader.setVec3(gl3, "light.ambient", light.getMaterial().getAmbient());
        shader.setVec3(gl3, "light.diffuse", light.getMaterial().getDiffuse());
        shader.setVec3(gl3, "light.specular", light.getMaterial().getSpecular());
        shader.setVec3(gl3, "material.ambient", material.getAmbient());
        shader.setVec3(gl3, "material.diffuse", material.getDiffuse());
        shader.setVec3(gl3, "material.specular", material.getSpecular());
        shader.setFloat(gl3, "material.shininess", material.getShininess());

        if (textureId1 != null) {
            shader.setInt(gl3, "first_texture", 0);
            gl3.glActiveTexture(GL.GL_TEXTURE0);
            gl3.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
        }
        if (textureId2 != null) {
            shader.setInt(gl3, "second_texture", 1);
            gl3.glActiveTexture(GL.GL_TEXTURE1);
            gl3.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
        }
    }

    public void render(GL3 gl3) {
        render(gl3, modelMatrix);
    }
}
