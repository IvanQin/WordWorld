package cn.turbosu.wordworld;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by TurboSu on 16/11/28.
 */
public class TexProgram {
    int program;
    int positionParam;
    int normalParam;
    int coordParam;
    int modelParam;
    int modelViewParam;
    int modelViewProjectionParam;
    int lightPosParam;
    int textureParam;

    int get(){
        return program;
    }

    public static ProgramBuilder newBuilder(){
        return new ProgramBuilder(GLES20.glCreateProgram());
    }

    void draw(TexObject object, float[] lightPosInEyeSpace, float[] modelView, int coordsPerVertex, float[] modelViewProjection, Bitmap bitmap) {

        GLES20.glUseProgram(program);

        GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);
        GLES20.glUniformMatrix4fv(modelParam, 1, false, object.model, 0);
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);
        GLES20.glUniform1i(textureParam, 0);
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(positionParam, coordsPerVertex, GLES20.GL_FLOAT, false, 0, object.vertices.get());
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, object.normals.get());
        GLES20.glVertexAttribPointer(coordParam, 2, GLES20.GL_FLOAT, false, 0,object.coords.get());

        int textureId=createTexture(bitmap);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glEnableVertexAttribArray(normalParam);
        GLES20.glEnableVertexAttribArray(coordParam);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, object.verticesNum);

    }

    static class ProgramBuilder{
        int program;
        ProgramBuilder attacheShader(int shader){
            GLES20.glAttachShader(program, shader);
            return this;
        }
        public ProgramBuilder (int program){
            this.program = program;
        }
        TexProgram build(){
            TexProgram thisProgram = new TexProgram();
            thisProgram.program=this.program;
            GLES20.glLinkProgram(program);
            GLES20.glUseProgram(program);

            thisProgram.positionParam = GLES20.glGetAttribLocation(program, "a_Position");
            thisProgram.normalParam = GLES20.glGetAttribLocation(program, "a_Normal");
            thisProgram.coordParam = GLES20.glGetAttribLocation(program, "a_Coordinate");

            thisProgram.textureParam = GLES20.glGetUniformLocation(program, "u_Texture");
            thisProgram.modelParam = GLES20.glGetUniformLocation(program, "u_Model");
            thisProgram.modelViewParam = GLES20.glGetUniformLocation(program, "u_MVMatrix");
            thisProgram.modelViewProjectionParam = GLES20.glGetUniformLocation(program, "u_MVP");
            thisProgram.lightPosParam = GLES20.glGetUniformLocation(program, "u_LightPos");

            return thisProgram;
        }
    }

    private int createTexture(Bitmap bitmap) {
        int[] texture=new int[1];

            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];

    }
}
