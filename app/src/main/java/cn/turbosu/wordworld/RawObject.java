package cn.turbosu.wordworld;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.List;

/**
 * Created by TurboSu on 16/11/28.
 */
public class RawObject {

    DataBuffer vertices;
    DataBuffer[] colors;
    DataBuffer normals;
    int verticesNum;
    int colorId;
    int colorNum;
    Vector3f pos;
    float[] model;
    Program program;

    public RawObject(float[] vertices, float[][] colors, float[] normals) {
        this.vertices = new DataBuffer(vertices);
        colorNum = colors.length;
        this.colors = new DataBuffer[colorNum];
        for (int i = 0; i < colorNum; i++)
            this.colors[i] = new DataBuffer(colors[i]);
        this.normals = new DataBuffer(normals);
        colorId = 0;
        verticesNum = vertices.length / 3;
        model = new float[16];
    }

    public RawObject(float[] vertices, float[] colors, float[] normals) {
        this.vertices = new DataBuffer(vertices);
        colorNum = 1;
        this.colors = new DataBuffer[colorNum];
        this.colors[0] = new DataBuffer(colors);
        this.normals = new DataBuffer(normals);
        colorId = 0;
        verticesNum = vertices.length / 3;
        model = new float[16];
    }

    void nextColor() {
        colorId = (colorId + 1) % colorNum;
    }

    DataBuffer getColor() {
        return colors[colorId];
    }

    void setPos(float x, float y, float z) {
        this.pos = new Vector3f(x, y, z);
    }

    void replace() {
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, pos.x, pos.y, pos.z);
    }

    void setProgram(Program program) {
        this.program = program;
    }

    void draw(float[] lightPosInEyeSpace, float[] view, int coordsPerVertex, float[] perspective) {
        float[] modelViewProjection = new float[16];
        float[] modelView = new float[16];
        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        program.draw(this, lightPosInEyeSpace, modelView, coordsPerVertex, modelViewProjection);
    }
}
