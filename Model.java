import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import gmaths.Mat4;

public class Model {
    private Camera camera;
    private Light light1, light2;
    private MovingLight movingLight;
    private Shader shader;
    private Material material;
    private Mat4 modelMatrix;
    private Mesh mesh;

    private double programStart = 0;
    private float offsetX;

    private int[] textureId1;
    private int[] textureId2;

    public Model(
            GL3 gl3, Camera camera, Light light1, Light light2, MovingLight movingLight,
            Shader shader, Material material, Mat4 modelMatrix, Mesh mesh,
            int[] textureId1, int[] textureId2
    ) {
        this.camera = camera;
        this.light1 = light1;
        this.light2 = light2;
        this.movingLight = movingLight;
        this.shader = shader;
        this.material = material;
        this.modelMatrix = modelMatrix;
        this.mesh = mesh;
        this.textureId1 = textureId1;
        this.textureId2 = textureId2;
        this.programStart = System.currentTimeMillis() / 1000.0;
    }

    public Model(
            GL3 gl3, Camera camera, Light light1, Light light2, MovingLight movingLight,
            Shader shader, Material material, Mat4 modelMatrix, Mesh mesh,
            int[] textureId1
    ) {
        this(gl3, camera, light1, light2, movingLight, shader, material, modelMatrix, mesh, textureId1, null);
    }

    public Model(
            GL3 gl3, Camera camera, Light light1, Light light2, MovingLight movingLight,
            Shader shader, Material material, Mat4 modelMatrix, Mesh mesh
    ) {
        this(
                gl3, camera, light1, light2, movingLight,
                shader, material, modelMatrix, mesh, null, null
        );
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setLight1(Light light1) {
        this.light1 = light1;
    }

    public void setLight2(Light light2) {
        this.light2 = light2;
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

    private void setOffsetTexture() {
        double elapsedTime = (System.currentTimeMillis() - programStart) * 0.00008;
        offsetX = (float) (elapsedTime - Math.floor(elapsedTime));
        // System.out.println("offsetX" + offsetX);
    }

    public void render(GL3 gl3, Mat4 modelMatrix) {
        Mat4 mvpMatrix = Mat4.multiply(
                camera.getPerspectiveMatrix(),
                Mat4.multiply(
                        camera.getViewMatrix(),
                        modelMatrix
                )
        );
        setOffsetTexture();
        shader.use(gl3);
        shader.setFloatArray(gl3, "model", modelMatrix.toFloatArrayForGLSL());
        shader.setFloatArray(gl3, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
        shader.setVec3(gl3, "viewPos", camera.getPosition());
        shader.setFloat(gl3, "offset", offsetX, 0);
        shader.setVec3(gl3, "light1.position", light1.getPosition());
        shader.setVec3(gl3, "light1.ambient", light1.getMaterial().getAmbient());
        shader.setVec3(gl3, "light1.diffuse", light1.getMaterial().getDiffuse());
        shader.setVec3(gl3, "light1.specular", light1.getMaterial().getSpecular());
        shader.setVec3(gl3, "light2.position", light2.getPosition());
        shader.setVec3(gl3, "light2.ambient", light2.getMaterial().getAmbient());
        shader.setVec3(gl3, "light2.diffuse", light2.getMaterial().getDiffuse());
        shader.setVec3(gl3, "light2.specular", light2.getMaterial().getSpecular());
        shader.setVec3(gl3, "lightBulb.position", movingLight.getWorldPosition());
        shader.setVec3(gl3, "lightBulb.direction", movingLight.getWorldDirection());
        shader.setVec3(gl3, "lightBulb.ambient", movingLight.getMaterial().getAmbient());
        shader.setVec3(gl3, "lightBulb.diffuse", movingLight.getMaterial().getDiffuse());
        shader.setVec3(gl3, "lightBulb.specular", movingLight.getMaterial().getSpecular());
        shader.setFloat(gl3, "lightBulb.constant", movingLight.getConstant());
        shader.setFloat(gl3, "lightBulb.linear", movingLight.getLinear());
        shader.setFloat(gl3, "lightBulb.quadratic", movingLight.getQuadratic());
        shader.setFloat(gl3, "lightBulb.cutOff", movingLight.getCutOff());
        shader.setFloat(gl3, "lightBulb.outerCutOff", movingLight.getOuterCutOff());
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
        mesh.render(gl3);
    }

    public void render(GL3 gl3) {
        render(gl3, modelMatrix);
    }
}
