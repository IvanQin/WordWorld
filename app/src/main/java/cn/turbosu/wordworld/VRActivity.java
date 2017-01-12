/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.turbosu.wordworld;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.vr.sdk.audio.GvrAudioEngine;
import com.google.vr.sdk.base.AndroidCompat;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * A Google VR sample application.
 * </p><p>
 * The TreasureHunt scene consists of a planar ground grid and a floating
 * "treasure" cube. When the user looks at the cube, the cube will turn gold.
 * While gold, the user can activate the Cardboard trigger, which will in turn
 * randomly reposition the cube.
 */
public class VRActivity extends GvrActivity implements GvrView.StereoRenderer {


    private static final String TAG = "WordWorldActivity";

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.3f;

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;

    private static final int COORDS_PER_VERTEX = 3;

    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 2.0f, 0.0f, 1.0f};

    // Convenience vector for extracting the position from a matrix via multiplication.
    private static final float[] POS_MATRIX_MULTIPLY_VEC = {0, 0, 0, 1.0f};

    private static final float MIN_MODEL_DISTANCE = 3.0f;
    private static final float MAX_MODEL_DISTANCE = 7.0f;

    private static final String OBJECT_SOUND_FILE = "cube_sound.wav";
    private static final String SUCCESS_SOUND_FILE = "success.wav";

    private final float[] lightPosInEyeSpace = new float[4];

    //private RawObject cube;
    private RawObject floor;
    private TexObject englishWord;
    private TexObject chineseWord;

    private Word word;
    private boolean colorLock = false;


    private float[] camera;
    private float[] view;
    private float[] headView;


    private float[] tempPosition;
    private float[] headRotation;

    private float objectDistance = MAX_MODEL_DISTANCE / 2.0f;
    private float floorDepth = 20f;

    private Vibrator vibrator;

    private GvrAudioEngine gvrAudioEngine;
    private volatile int sourceId = GvrAudioEngine.INVALID_ID;
    private volatile int successSourceId = GvrAudioEngine.INVALID_ID;

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type  The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The shader object handler.
     */
    private int loadGLShader(int type, int resId) {
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     */
    private static void checkGLError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    /**
     * Sets the view to our GvrView and initializes the transformation matrices we will use
     * to render our scene.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGvrView();

        camera = new float[16];
        view = new float[16];
        tempPosition = new float[4];
        // Model first appears directly in front of user.

        headRotation = new float[4];
        headView = new float[16];
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize 3D audio engine.
        // gvrAudioEngine = new GvrAudioEngine(this, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);
    }

    public void initializeGvrView() {
        setContentView(R.layout.common_ui);

        GvrView gvrView = (GvrView) findViewById(R.id.gvr_view);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

        gvrView.setRenderer(this);
        gvrView.setTransitionViewEnabled(true);

        if (gvrView.setAsyncReprojectionEnabled(true)) {
            // Async reprojection decouples the app framerate from the display framerate,
            // allowing immersive interaction even at the throttled clockrates set by
            // sustained performance mode.
            AndroidCompat.setSustainedPerformanceMode(this, true);
        }

        setGvrView(gvrView);
    }

    @Override
    public void onPause() {
        // gvrAudioEngine.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //gvrAudioEngine.resume();
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
    }

    /**
     * Creates the buffers we use to store information about the 3D world.
     * <p/>
     * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
     * Hence we use ByteBuffers.
     *
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well.
/*
        cube = new RawObject(WorldLayoutData.CUBE_COORDS, WorldLayoutData.CUBE_COLORS, WorldLayoutData.CUBE_NORMALS);
        cube.setPos(0.0f, 0.0f, MAX_MODEL_DISTANCE / 2.0f);
*/
        word = WordList.getNext();

        englishWord = new TexObject(WorldLayoutData.SQUARE_COORDS, WorldLayoutData.SQUARE_TEX_COORDS, ReadIMG.initFontBitmap(word.getEnglish()), WorldLayoutData.SQUARE_NORMALS);
        englishWord.setPos(0.0f, 3.0f, -MAX_MODEL_DISTANCE / 2.0f);

        chineseWord = new TexObject(WorldLayoutData.SQUARE_COORDS, WorldLayoutData.SQUARE_TEX_COORDS, ReadIMG.initFontBitmap(word.getChinese()), WorldLayoutData.SQUARE_NORMALS);
        chineseWord.setPos(MAX_MODEL_DISTANCE / 2.0f, 3.0f, 0.0f);

        floor = new RawObject(WorldLayoutData.FLOOR_COORDS, WorldLayoutData.FLOOR_COLORS, WorldLayoutData.FLOOR_NORMALS);
        floor.setPos(0, -floorDepth, 0); // Floor appears below user.

        freshMessage.start(englishWord, chineseWord);
        int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        int gridShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
        int passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);

        int texVertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.tex_vertex);
        int texFragShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.tex_fragment);

        /*cube.setProgram(
                new Program().newBuilder().attacheShader(vertexShader).attacheShader(passthroughShader).build()
        );

        checkGLError("Cube program");
        checkGLError("Cube program params");
        */
        englishWord.setProgram(
                new TexProgram().newBuilder().attacheShader(texVertexShader).attacheShader(texFragShader).build()
        );

        chineseWord.setProgram(
                new TexProgram().newBuilder().attacheShader(texVertexShader).attacheShader(texFragShader).build()
        );

        checkGLError("Cube program");
        checkGLError("Cube program params");

        floor.setProgram(
                new Program().newBuilder().attacheShader(vertexShader).attacheShader(gridShader).build()
        );

        checkGLError("Floor program");
        checkGLError("Floor program params");


        floor.replace();

        // Avoid any delays during start-up due to decoding of sound files.


        updateModelPosition();

        checkGLError("onSurfaceCreated");
    }

    /**
     * Updates the cube model position.
     */
    protected void updateModelPosition() {
        //cube.replace();
        englishWord.replace();
        chineseWord.replace();
        // Update the sound location to match it with the new cube position.

        checkGLError("updateCubePosition");
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The context of the text file, or null in case of error.
     */
    private String readRawTextFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        setCubeRotation();

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);


        headTransform.getHeadView(headView, 0);

        double x = -headView[2];
        double y = -headView[6];
        double z = -headView[10];

        double xz = Math.sqrt(x * x + z * z);

        double arc = Math.atan2(y, xz) * 180 / Math.PI;

        if (arc < -65 && colorLock == false) {
            //cube.nextColor();
            Flags.needNewWorld = true;
            /*
            word = WordList.getNext();
            englishWord.setTexture(ReadIMG.initFontBitmap(word.getEnglish()));
            chineseWord.setTexture(ReadIMG.initFontBitmap(word.getChinese()));
            */
            colorLock = true;
        }
        if (arc > -10 && colorLock == true) {
            colorLock = false;
        }

        /*
        // Update the 3d audio engine with the most recent head rotation.
        headTransform.getQuaternion(headRotation, 0);
        gvrAudioEngine.setHeadRotation(
                headRotation[0], headRotation[1], headRotation[2], headRotation[3]);
        // Regular update call to GVR audio engine.
        gvrAudioEngine.update();
        */
        checkGLError("onReadyToDraw");
    }

    boolean p = false;

    protected void setCubeRotation() {
        //Matrix.rotateM(cube.model, 0, TIME_DELTA, 0.5f, 0.5f, 1.0f);
        if (p == false) {
            //Matrix.rotateM(englishWord.model, 0, 90.0f, 0f, 0f, 1.0f);
            Matrix.rotateM(chineseWord.model, 0, -90.0f, 0f, 1.0f, 0f);
            //Matrix.rotateM(chineseWord.model, 0, 90.0f, 0f, 0f, 1.0f);
            p = true;
        }

    }

    /**
     * Draws a frame for an eye.
     *
     * @param eye The eye to render. Includes all required transformations.
     */
    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //checkGLError("colorParam");

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        //cube.draw(lightPosInEyeSpace, view, COORDS_PER_VERTEX, perspective);
        if (Flags.loadImg == false) {
            englishWord.draw(lightPosInEyeSpace, view, COORDS_PER_VERTEX, perspective);
            chineseWord.draw(lightPosInEyeSpace, view, COORDS_PER_VERTEX, perspective);
        }
        floor.draw(lightPosInEyeSpace, view, COORDS_PER_VERTEX, perspective);

    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }


    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");


        // Always give user feedback.
        vibrator.vibrate(50);
    }

    /**
     * Find a new random position for the object.
     * <p/>
     * <p>We'll rotate it around the Y-axis so it's out of sight, and then up or down by a little bit.
     */
    /*
    protected void hideObject() {
        float[] rotationMatrix = new float[16];
        float[] posVec = new float[4];

        // First rotate in XZ plane, between 90 and 270 deg away, and scale so that we vary
        // the object's distance from the user.
        float angleXZ = (float) Math.random() * 180 + 90;
        Matrix.setRotateM(rotationMatrix, 0, angleXZ, 0f, 1f, 0f);
        float oldObjectDistance = objectDistance;
        objectDistance =
                (float) Math.random() * (MAX_MODEL_DISTANCE - MIN_MODEL_DISTANCE) + MIN_MODEL_DISTANCE;
        float objectScalingFactor = objectDistance / oldObjectDistance;
        Matrix.scaleM(rotationMatrix, 0, objectScalingFactor, objectScalingFactor, objectScalingFactor);
        Matrix.multiplyMV(posVec, 0, rotationMatrix, 0, cube.model, 12);

        float angleY = (float) Math.random() * 80 - 40; // Angle in Y plane, between -40 and 40.
        angleY = (float) Math.toRadians(angleY);
        float newY = (float) Math.tan(angleY) * objectDistance;
        cube.setPos(posVec[0], newY, posVec[2]);

        updateModelPosition();
    }
    */

    /**
     * Check if user is looking at object by calculating where the object is in eye-space.
     *
     * @return true if the user is looking at the object.
     */
    private boolean isLookingAtObject() {
        // Convert object space to camera space. Use the headView from onNewFrame.
    /*
    Matrix.multiplyMM(modelView, 0, headView, 0, cube.model, 0);
    Matrix.multiplyMV(tempPosition, 0, modelView, 0, POS_MATRIX_MULTIPLY_VEC, 0);

    float pitch = (float) Math.atan2(tempPosition[1], -tempPosition[2]);
    float yaw = (float) Math.atan2(tempPosition[0], -tempPosition[2]);

    return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;*/
        return false;
    }

}
