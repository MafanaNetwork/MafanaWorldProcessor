package me.tahacheji.mafana.util;

public class MathUtil {

    public int[] applyRotations(double rotateX, double rotateY, double rotateZ, int x, int y, int z) {
        double[] rotatedCoords = rotateX(rotateX, x, y, z);
        rotatedCoords = rotateY(rotateY, rotatedCoords[0], rotatedCoords[1], rotatedCoords[2]);
        rotatedCoords = rotateZ(rotateZ, rotatedCoords[0], rotatedCoords[1], rotatedCoords[2]);

        return new int[]{(int) Math.round(rotatedCoords[0]), (int) Math.round(rotatedCoords[1]), (int) Math.round(rotatedCoords[2])};
    }

    public double[] rotateX(double angle, int x, int y, int z) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        return new double[]{x, cos * y - sin * z, sin * y + cos * z};
    }

    public double[] rotateY(double angle, double x, double y, double z) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        return new double[]{cos * x + sin * z, y, -sin * x + cos * z};
    }

    public double[] rotateZ(double angle, double x, double y, double z) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        return new double[]{cos * x - sin * y, sin * x + cos * y, z};
    }

}
