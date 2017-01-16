package cn.turbosu.wordworld;

import android.opengl.GLES20;

/**
 * Created by TurboSu on 16/11/28.
 */
public class Program {

    int program;
    int positionParam;
    int normalParam;
    int colorParam;
    int modelParam;
    int modelViewParam;
    int modelViewProjectionParam;
    int lightPosParam;

    int get(){
        return program;
    }

    public static ProgramBuilder newBuilder(){
        return new ProgramBuilder(GLES20.glCreateProgram());
    }

    void draw(RawObject object, float[] lightPosInEyeSpace, float[] modelView, int coordsPerVertex, float[] modelViewProjection) {

        GLES20.glUseProgram(program);

        GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelParam, 1, false, object.model, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);

        // Set the position of the cube
        GLES20.glVertexAttribPointer(
                positionParam, coordsPerVertex, GLES20.GL_FLOAT, false, 0, object.vertices.get());

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, object.normals.get());
        GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0,object.getColor().get());

        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glEnableVertexAttribArray(normalParam);
        GLES20.glEnableVertexAttribArray(colorParam);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, object.verticesNum);

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
        Program build(){
            Program thisProgram = new Program();
            thisProgram.program=this.program;
            GLES20.glLinkProgram(program);
            GLES20.glUseProgram(program);

            thisProgram.positionParam = GLES20.glGetAttribLocation(program, "a_Position");
            thisProgram.normalParam = GLES20.glGetAttribLocation(program, "a_Normal");
            thisProgram.colorParam = GLES20.glGetAttribLocation(program, "a_Color");

            thisProgram.modelParam = GLES20.glGetUniformLocation(program, "u_Model");
            thisProgram.modelViewParam = GLES20.glGetUniformLocation(program, "u_MVMatrix");
            thisProgram.modelViewProjectionParam = GLES20.glGetUniformLocation(program, "u_MVP");
            thisProgram.lightPosParam = GLES20.glGetUniformLocation(program, "u_LightPos");

            return thisProgram;
        }
    }
}
