/* This code is from exercise sheet written by Dr. Steve Maddock */

import gmaths.Vec3;

public class Material {
    public static final Vec3 DEFAULT_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);
    public static final Vec3 DEFAULT_DIFFUSE = new Vec3(0.8f, 0.8f, 0.8f);
    public static final Vec3 DEFAULT_SPECULAR = new Vec3(0.5f, 0.5f, 0.5f);
    public static final Vec3 DEFAULT_EMISSION = new Vec3(0.0f, 0.0f, 0.0f);
    public static final float DEFAULT_SHININESS = 32;

    private Vec3 ambient;
    private Vec3 diffuse;
    private Vec3 specular;
    private Vec3 emission;
    private float shininess;

    public Material() {
        ambient = new Vec3(DEFAULT_AMBIENT);
        diffuse = new Vec3(DEFAULT_DIFFUSE);
        specular = new Vec3(DEFAULT_SPECULAR);
        emission = new Vec3(DEFAULT_EMISSION);
        shininess = DEFAULT_SHININESS;
    }

    public Material(Vec3 ambient, Vec3 diffuse, Vec3 specular, float shininess) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.emission = new Vec3(DEFAULT_EMISSION);
        this.shininess = shininess;
    }

    public void setAmbient(float r, float g, float b) {
        ambient.x = r;
        ambient.y = g;
        ambient.z = b;
    }

    public void setAmbient(Vec3 rgb) {
        setAmbient(rgb.x, rgb.y, rgb.z);
    }

    public Vec3 getAmbient() {
        return new Vec3(ambient);
    }

    public void setDiffuse(float r, float g, float b) {
        diffuse.x = r;
        diffuse.y = g;
        diffuse.z = b;
    }

    public void setDiffuse(Vec3 rgb) {
        setDiffuse(rgb.x, rgb.y, rgb.z);
    }

    public Vec3 getDiffuse() {
        return new Vec3(diffuse);
    }

    public void setSpecular(float r, float g, float b) {
        specular.x = r;
        specular.y = g;
        specular.z = b;
    }

    public void setSpecular(Vec3 rgb) {
        setSpecular(rgb.x, rgb.y, rgb.z);
    }

    public Vec3 getSpecular() {
        return new Vec3(specular);
    }

    public void setEmission(float r, float g, float z) {
        emission.x = r;
        emission.y = g;
        emission.z = z;
    }

    public void setEmission(Vec3 rgb) {
        setEmission(rgb.x, rgb.y, rgb.z);
    }

    public Vec3 getEmission() {
        return new Vec3(emission);
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public float getShininess() {
        return shininess;
    }

    public String toString() {
        return "a: " + ambient +
                "d: " + diffuse +
                "s: " + specular +
                "e: " + emission +
                "shininess: " + shininess;
    }
}
