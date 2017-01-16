package cn.turbosu.wordworld;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by TurboSu on 16/11/28.
 */
public class DataBuffer {
    FloatBuffer buffer;

    public DataBuffer() {

    }

    public DataBuffer(float[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
        bb.order(ByteOrder.nativeOrder());
        buffer = bb.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);
    }

    static DataBuffer newBuffer(float[] data) {
        return new DataBuffer(data);
    }

    public FloatBuffer get() {
        return buffer;
    }

}
