/*
 * Copyright 2014 Google Inc. All Rights Reserved.

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

/**
 * Contains vertex, normal and color data.
 */
public final class WorldLayoutData {

  public static final float[] SQUARE_COORDS = new float[]{

          -2f, -1f, 0f,
          -2f, -3f, 0f,
          2f, -1f, 0f,
          2f,-3f, 0f,

  };
  public static final float[] SQUARE_TEX_COORDS = new float[]{

          0f, 0f,
          0f, 1f,
          1f, 0f,
          1f, 1f,




  };
  public static final float[] SQUARE_COLORS = new float[]{
          0.0f, 0.5273f, 0.2656f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.8359375f, 0.17578125f, 0.125f, 1.0f,
          0.0f, 0.5273f, 0.2656f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.8359375f, 0.17578125f, 0.125f, 1.0f,
  };
  public static final float[] SQUARE_NORMALS = new float[]{
          0f, 0.0f, 1.0f,
          0f, 0.0f, 1.0f,
          0f, 0.0f, 1.0f,
          0f, 0.0f, 1.0f,
          0f, 0.0f, 1.0f,
          0f, 0.0f, 1.0f,
          0f, 0.0f, 1.0f,
          0f, 0.0f, 1.0f,

  };

  public static final float[] CUBE_COORDS = new float[] {
          0f,0f,0f,
          0f,1f,0f,
          0f,0f,1f,

          0f,0f,0f,
          1f,0f,0f,
          0f,1f,0f,

          0f,0f,0f,
          1f,0f,0f,
          0f,0f,1f,

          1f,0f,0f,
          0f,1f,0f,
          0f,0f,1f,

  };


  public static final float[][] CUBE_COLORS = new float[][] {
          {
                  0.0f, 0.5273f, 0.2656f, 1.0f,
                  0.0f, 0.3398f, 0.9023f, 1.0f,
                  0.8359375f,  0.17578125f,  0.125f, 1.0f,


                  0.0f, 0.5273f, 0.2656f, 1.0f,
                  1.0f,  0.6523f, 0.0f, 1.0f,
                  0.0f, 0.3398f, 0.9023f, 1.0f,


                  0.0f, 0.5273f, 0.2656f, 1.0f,
                  1.0f,  0.6523f, 0.0f, 1.0f,
                  0.8359375f,  0.17578125f,  0.125f, 1.0f,


                  1.0f,  0.6523f, 0.0f, 1.0f,
                  0.0f, 0.3398f, 0.9023f, 1.0f,
                  0.8359375f,  0.17578125f,  0.125f, 1.0f,
          },

          {

                  0.4f, 0.5273f, 0.2656f, 1.0f,
                  0.0f, 0.1098f, 0.9023f, 1.0f,
                  0.8359375f,  0.88578125f,  0.125f, 1.0f,


                  0.4f, 0.5273f, 0.2656f, 1.0f,
                  1.0f,  0.6523f, 0.5f, 1.0f,
                  0.0f, 0.1098f, 0.9023f, 1.0f,


                  0.4f, 0.5273f, 0.2656f, 1.0f,
                  1.0f,  0.6523f, 0.5f, 1.0f,
                  0.8359375f,  0.88578125f,  0.125f, 1.0f,


                  1.0f,  0.6523f, 0.5f, 1.0f,
                  0.0f, 0.1098f, 0.9023f, 1.0f,
                  0.8359375f,  0.88578125f,  0.125f, 1.0f,
          },
          {

                  0.8f, 0.2f, 0.7f, 1.0f,
                  0.3f, 0.2f, 0.12f, 1.0f,
                  0.3f,  0.7f,  0.125f, 1.0f,


                  0.8f, 0.2f, 0.7f, 1.0f,
                  1.0f,  0.6523f, 0.5f, 1.0f,
                  0.3f, 0.2f, 0.12f, 1.0f,


                  0.8f, 0.2f, 0.7f, 1.0f,
                  1.0f,  0.6523f, 0.5f, 1.0f,
                  0.3f,  0.7f,  0.125f, 1.0f,


                  1.0f,  0.6523f, 0.5f, 1.0f,
                  0.3f, 0.2f, 0.12f, 1.0f,
                  0.3f,  0.7f,  0.125f, 1.0f,
          },

  };

  public static final float[] CUBE_FOUND_COLORS = new float[] {
          0f, 0.5273f, 0.2656f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.8359375f,  0.17578125f,  0.125f, 1.0f,


          0f, 0.5273f, 0.2656f, 1.0f,
          1.0f,  0.6523f, 0.0f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,


          0f, 0.5273f, 0.2656f, 1.0f,
          1.0f,  0.6523f, 0.0f, 1.0f,
          0.8359375f,  0.17578125f,  0.125f, 1.0f,


          1.0f,  0.6523f, 0.0f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.8359375f,  0.17578125f,  0.125f, 1.0f,
  };

  public static final float[] CUBE_NORMALS = new float[] {
          -1.0f, 0.0f, 0.0f,
          -1.0f, 0.0f, 0.0f,
          -1.0f, 0.0f, 0.0f,

          0.0f, 0.0f, -1.0f,
          0.0f, 0.0f, -1.0f,
          0.0f, 0.0f, -1.0f,

          0.0f, -1.0f, 0.0f,
          0.0f, -1.0f, 0.0f,
          0.0f, -1.0f, 0.0f,

          1.0f, 1.0f, 1.0f,
          1.0f, 1.0f, 1.0f,
          1.0f, 1.0f, 1.0f,
  };

  // The grid lines on the floor are rendered procedurally and large polygons cause floating point
  // precision problems on some architectures. So we split the floor into 4 quadrants.
  public static final float[] FLOOR_COORDS = new float[] {
          // +X, +Z quadrant
          200, 0, 0,
          0, 0, 0,
          0, 0, 200,
          200, 0, 0,
          0, 0, 200,
          200, 0, 200,

          // -X, +Z quadrant
          0, 0, 0,
          -200, 0, 0,
          -200, 0, 200,
          0, 0, 0,
          -200, 0, 200,
          0, 0, 200,

          // +X, -Z quadrant
          200, 0, -200,
          0, 0, -200,
          0, 0, 0,
          200, 0, -200,
          0, 0, 0,
          200, 0, 0,

          // -X, -Z quadrant
          0, 0, -200,
          -200, 0, -200,
          -200, 0, 0,
          0, 0, -200,
          -200, 0, 0,
          0, 0, 0,
  };

  public static final float[] FLOOR_NORMALS = new float[] {
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
          0.0f, 1.0f, 0.0f,
  };

  public static final float[] FLOOR_COLORS = new float[] {
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,
          0.0f, 0.0f, 0.0f, 1.0f,

         /* 0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,
          0.0f, 0.3398f, 0.9023f, 1.0f,*/
  };
}
