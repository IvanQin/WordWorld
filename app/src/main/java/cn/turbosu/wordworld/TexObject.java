package cn.turbosu.wordworld;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

/**
 * Created by TurboSu on 16/11/28.
 */
public class TexObject {
    DataBuffer vertices;
    Bitmap texture;
    DataBuffer coords;
    DataBuffer normals;
    int verticesNum;
    int colorId;
    int colorNum;
    Vector3f pos;
    float[] model;
    TexProgram program;



    public TexObject(float[] vertices,float[] coords, Bitmap tex, float[] normals) {
        this.vertices = new DataBuffer(vertices);
        this.coords = new DataBuffer(coords);
        colorNum = 1;
        this.texture = tex;
        this.normals = new DataBuffer(normals);
        colorId = 0;
        verticesNum = vertices.length / 3;
        model = new float[16];
    }



    void setPos(float x, float y, float z) {
        this.pos = new Vector3f(x, y, z);
    }

    void replace() {
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, pos.x, pos.y, pos.z);
    }

    void setProgram(TexProgram program) {
        this.program = program;
    }

    void setTexture(Bitmap tex) {
        Flags.loadImg=true;
        Bitmap oldTex=this.texture;
        this.texture = tex;
        oldTex.recycle();
        Flags.loadImg=false;
    }

    void draw(float[] lightPosInEyeSpace, float[] view, int coordsPerVertex, float[] perspective) {
        float[] modelViewProjection = new float[16];
        float[] modelView = new float[16];
        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        program.draw(this, lightPosInEyeSpace, modelView, coordsPerVertex, modelViewProjection,texture);
    }
}
