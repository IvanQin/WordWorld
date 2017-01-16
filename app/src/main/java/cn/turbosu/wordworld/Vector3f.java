package cn.turbosu.wordworld;

/**
 * Created by TurboSu on 16/11/28.
 */
public class Vector3f {

    float x,y,z;
    public Vector3f(float x,float y,float z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    double getPhi() {
        double xz = Math.sqrt(x * x + z * z);
        return Math.atan2(y, xz) * 180 / Math.PI;
    }
}
