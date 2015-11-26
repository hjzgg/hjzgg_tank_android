package hjzgg.game_self;

import hjzgg.tank.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

public class MyImageView extends View{
	private int height, width;
	private int resId = R.drawable.null_black;
	
	public void setResId(int resId){
		this.resId = resId;
	}
	
	public MyImageView(Context context, int width, int height) {
		super(context);
		this.height = height;
		this.width = width;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), resId);
		canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, width, height), paint);
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);//»æÖÆ¿ÕÐÄ¾ØÐÎ
		canvas.drawRect(new Rect(0, 0, width-1, height-1), paint);
		if(bitmap.isRecycled())
			bitmap.recycle();
	}
}
