package cn.turbosu.wordworld;

import android.graphics.Bitmap;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TurboSu on 17/1/13.
 */
public class Model {
    DataBuffer vertices;
    DataBuffer normals;
    int verticesNum;

    List<DataBuffer> color;
    List<Bitmap> texture;

    Boolean colorOn = false;
    Boolean textureOn = false;

    int currentColorId = 0;
    int colorTotalNum = 0;

    int currentTextureId = 0;
    int textureTotalNum = 0;

    Vector3f pos;
    float[] model;
    Program program;

    public Model(float[] vertices, float[] normals) {
        this.vertices = new DataBuffer(vertices);
        this.normals = new DataBuffer(normals);
        verticesNum = vertices.length / 3;
        model = new float[16];
    }

    void turnOnColor() {
        colorOn = true;
        textureOn = false;
    }

    void turnOnTexture() {
        colorOn = false;
        textureOn = true;
    }

    void nextColor() {
        currentColorId = (currentColorId + 1) % colorTotalNum;
        turnOnColor();
    }

    void setColorById(int colorId) {
        if (colorId >= colorTotalNum) {
            Log.e(Flags.TAG, "Color ID out of boundary.");
            throw new RuntimeException("Color ID out of boundary.");
        }
        currentColorId = colorId;
        turnOnColor();
    }

    void setColorBy(DataBuffer newColor) {
        color = new ArrayList<>();
        colorTotalNum = 0;
        addColor(newColor);
        turnOnColor();
    }

    void addColor(DataBuffer newColor) {
        color.add(newColor);
        colorTotalNum++;
        turnOnColor();
    }


    void nextTexture() {
        currentTextureId = (currentTextureId + 1) % textureTotalNum;
        turnOnTexture();
    }

    void setTextureById(int TextureId) {
        if (TextureId >= textureTotalNum) {
            Log.e(Flags.TAG, "Texture ID out of boundary.");
            throw new RuntimeException("Texture ID out of boundary.");
        }
        currentTextureId = TextureId;
        turnOnTexture();
    }

    void setTexture(Bitmap newTexture) {
        texture = new ArrayList<>();
        textureTotalNum = 0;
        addTexture(newTexture);
        turnOnTexture();
    }

    void addTexture(Bitmap newTexture) {
        texture.add(newTexture);
        textureTotalNum++;
        turnOnTexture();
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
        //program.draw(this, lightPosInEyeSpace, modelView, coordsPerVertex, modelViewProjection);
    }
}
