package cn.turbosu.wordworld;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by TurboSu on 16/11/29.
 */
public class ReadIMG {
    public static Bitmap initFontBitmap(String word){
        String font = word;
        Bitmap bitmap = Bitmap.createBitmap(128, 64, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //背景颜色
        canvas.drawColor(Color.GRAY);
        Paint p = new Paint();
        //字体设置
        String fontType = "宋体";
        //Typeface typeface = Typeface.create(fontType, Typeface.BOLD);
        Typeface typeface = Typeface.defaultFromStyle(Typeface.BOLD);

        //消除锯齿
        p.setAntiAlias(true);
        //字体为红色
        p.setColor(Color.WHITE);
        p.setTypeface(typeface);
        p.setTextSize(20);
        //绘制字体
        canvas.drawText(font, 0, 32, p);
        int r,g,b;



        return bitmap;
    }
}
