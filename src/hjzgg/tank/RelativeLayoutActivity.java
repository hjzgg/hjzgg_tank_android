package hjzgg.tank;

import hjzgg.game_self.MyImageView;
import hjzgg.tank.R;
import hjzgg.tank.TankBattleActivity;
import hjzgg.use.ID;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageButton;

public class RelativeLayoutActivity extends Activity {
    /** Called when the activity is first created. */
	private int[][] sta = new int[ID.staRow][ID.staCol];
	private MyImageView[][] miv = new MyImageView[ID.staRow][ID.staCol];
	private int resId = R.drawable.null_black;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        staInit();
        setContentView(R.layout.self_game);
		AbsoluteLayout centerView =(AbsoluteLayout)findViewById(R.id.centerView);
		final int size = px2dip(this, 35);
        for(int i=0; i<18; ++i)
        	for(int j=0; j<26; ++j){
        		final int x = i, y = j;
        		miv[i][j] = new MyImageView(this, size, size);
        		miv[i][j].setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(sta[x][y] != R.drawable.home && sta[x][y] != R.drawable.tank_appear){
							((MyImageView)v).setResId(resId);
							((MyImageView)v).invalidate();
							sta[x][y] = resId;
						}
					}
				});
        		//不要忘记定义组件的显示参数，否则采用的是默认的
        		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(size, size, j*size, i*size);
        		centerView.addView(miv[i][j], params);
        	}
        MyImageView imgView = new MyImageView(this, size*4, size*3);
        imgView.setResId(R.drawable.home);
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(size*4, size*3, 12*size, 15*size);
        centerView.addView(imgView, params);
        ((ImageButton)findViewById(R.id.grass_btn)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resId = R.drawable.grass;
			}
		}) ;
        
        ((ImageButton)findViewById(R.id.null_btn)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resId = R.drawable.null_black;
			}
		}) ;
        
        ((ImageButton)findViewById(R.id.stellWall_btn)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resId = R.drawable.steel_wall;
			}
		}) ;
        
        ((ImageButton)findViewById(R.id.wall_btn)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resId = R.drawable.wall;
			}
		}) ;
        
        ((ImageButton)findViewById(R.id.water_btn)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resId = R.drawable.water;
			}
		}) ;
        
        ((Button)findViewById(R.id.begin_self_game)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(RelativeLayoutActivity.this, TankBattleActivity.class);
				for(int i=0; i<sta.length; ++i)
					intent.putExtra("hjzgg"+i, sta[i]);
				startActivity(intent);//转移Activity
				RelativeLayoutActivity.this.finish();
			}
		});
        
        ((Button)findViewById(R.id.self_game_reset)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				for(int i=0; i<sta.length; ++i)
					for(int j=0; j<sta[i].length; ++j){
						sta[i][j] = 0;
						miv[i][j].setResId(R.drawable.null_black);
						miv[i][j].invalidate();
					}
				staInit();
			}
		});
    }
    
    public void staInit(){
		//家所在的位置不能填充
		sta[15][12]=sta[15][13]=sta[15][14]=sta[15][15]=
		sta[16][12]=sta[16][13]=sta[16][14]=sta[16][15]=
		sta[17][12]=sta[17][13]=sta[17][14]=sta[17][15]=R.drawable.home;
		
		//坦克出现的位置不能填充
		sta[0][0]=sta[0][1]=sta[1][1]=sta[1][0]=R.drawable.tank_appear;
		sta[0][24]=sta[0][25]=sta[1][25]=sta[1][24]=R.drawable.tank_appear;
		
		sta[16][9]=sta[17][9]=sta[17][8]=sta[16][8]=R.drawable.tank_appear;
	}
    
    private int px2dip(Context context, int pxValue){
    	final float density = context.getResources().getDisplayMetrics().density;
    	return (int)(pxValue*density + 0.5f);
    }
}