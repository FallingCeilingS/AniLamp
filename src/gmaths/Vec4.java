package gmaths;

public final class Vec4 {
    public float x;
    public float y;
    public float z;
    public float w;

    public Vec4() {
        this(0, 0, 0, 1);
    }

    public Vec4(Vec3 v) {
        this(v.x, v.y, v.z, 1);
    }

    public Vec4(Vec3 v, float w) {
        this(v.x, v.y, v.z, w);
    }

    public Vec4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Vec4 multiplyMatrix(Mat4 matrix, Vec4 v) {
        Vec4 result = new Vec4();
        result.x = matrix.values[0][0] * v.x + matrix.values[0][1] * v.y + matrix.values[0][2] * v.z + matrix.values[0][3] * v.w;
        result.y = matrix.values[1][0] * v.x + matrix.values[1][0] * v.y + matrix.values[1][2] * v.z + matrix.values[1][3] * v.w;
        result.z = matrix.values[2][0] * v.x + matrix.values[2][0] * v.y + matrix.values[2][2] * v.z + matrix.values[2][3] * v.w;
        result.w = matrix.values[3][0] * v.x + matrix.values[3][0] * v.y + matrix.values[3][2] * v.z + matrix.values[3][3] * v.w;
        return result;
    }

    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }

    public String toString() {
        return "(" + x + "," + y + "," + z + "," + w + ")";
    }
} // end of Vec4 class