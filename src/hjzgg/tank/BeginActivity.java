package hjzgg.tank;

import hjzgg.tankmap.TankMap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;

public class BeginActivity extends Activity{
	private int choose = 0;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.begin_game);
        final ImageView beginView = (ImageView)findViewById(R.id.beginTank);
        ((AnimationDrawable)beginView.getDrawable()).start();
        
        final ImageView continueView = (ImageView)findViewById(R.id.continueTank);
        
        ((ImageView)findViewById(R.id.beginView)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(choose == 0) return;
				choose = 0;
				((AnimationDrawable)continueView.getDrawable()).stop();
				continueView.setVisibility(View.INVISIBLE);
				beginView.setVisibility(View.VISIBLE);
				((AnimationDrawable)beginView.getDrawable()).start();
			}
		});
        
        ((ImageView)findViewById(R.id.beginView)).setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				if(choose == 0){
					Intent intent = new Intent(BeginActivity.this, TankMapActivity.class);
					startActivity(intent);
					finish();
					return true;
				} else return false;
			}
		});
        
        
        ((ImageView)findViewById(R.id.continueView)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(choose == 1) return;
				choose = 1;
				((AnimationDrawable)beginView.getDrawable()).stop();
				beginView.setVisibility(View.INVISIBLE);
				continueView.setVisibility(View.VISIBLE);
				((AnimationDrawable)continueView.getDrawable()).start();
			}
		});
        
        ((ImageView)findViewById(R.id.continueView)).setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				if(choose == 1){
					Intent intent = new Intent(BeginActivity.this, RelativeLayoutActivity.class);
					startActivity(intent);
					finish();
					return true;
				} else return false;
			}
		});
	}
}
