package cn.turbosu.wordworld;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by TurboSu on 16/11/28.
 */
public class RawBuffer {
    FloatBuffer buffer;

    public RawBuffer() {

    }

    public RawBuffer(float[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
        bb.order(ByteOrder.nativeOrder());
        buffer = bb.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);
    }

    static RawBuffer newBuffer(float[] data) {
        return new RawBuffer(data);
    }

    public FloatBuffer get() {
        return buffer;
    }

}
