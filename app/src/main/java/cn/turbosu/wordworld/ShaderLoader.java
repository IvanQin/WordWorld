package cn.turbosu.wordworld;

import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by TurboSu on 17/1/13.
 */
public class ShaderLoader {

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type           The type of shader we will be creating.
     * @param resInputStream The resource input stream of the raw text file about to be turned into a shader.
     * @return The shader object handler.
     */
    static int loadGLShader(int type, InputStream resInputStream) {
        String code = readRawTextFile(resInputStream);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(Flags.TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }
        return shader;
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resInputStream The resource input stream of the raw text file about to be turned into a shader.
     * @return The context of the text file, or null in case of error.
     */
    static String readRawTextFile(InputStream resInputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resInputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
