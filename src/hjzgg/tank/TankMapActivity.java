package hjzgg.tank;

import hjzgg.tankmap.TankMap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

public class TankMapActivity extends Activity{
	private int index = 0;
	private ImageView  imageView = null;
	private ImageSwitcher imageSwitcher = null;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tank_map);
        imageSwitcher = (ImageSwitcher)findViewById(R.id.imageSwitcher1);
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        imageSwitcher.setFactory(new ViewFactory() {
			public View makeView() {
				imageView = new ImageView(TankMapActivity.this);
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				imageView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(TankMapActivity.this, TankBattleActivity.class);
						for(int i=0; i < TankMap.tankMap[index].length; ++i)
							intent.putExtra("hjzgg"+i, TankMap.tankMap[index][i]);
						startActivity(intent);
						TankMapActivity.this.finish();
					}
				});
				return imageView;
			}
		});
        imageSwitcher.setImageResource(TankMap.imageId[index]);
        
        Button up = (Button)findViewById(R.id.button1);
        Button down = (Button)findViewById(R.id.button2);
        up.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(index > 0) index--;
				else index = TankMap.imageId.length - 1;
				imageSwitcher.setImageResource(TankMap.imageId[index]);
			}
		});
        
        down.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(index < TankMap.imageId.length-1) index++;
				else index = 0;
				imageSwitcher.setImageResource(TankMap.imageId[index]);
			}
		});
	}
}
