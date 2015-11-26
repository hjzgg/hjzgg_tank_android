package hjzgg.thread;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.Toast;

import hjzgg.set.MySet;
import hjzgg.tank.EnemyTankView;
import hjzgg.tank.OtherImageView;
import hjzgg.tank.R;
import hjzgg.tank.Rect;
import hjzgg.tank.TankBattleActivity;
import hjzgg.tank.TankView;


public class EnemyTankThread implements Runnable{
	private TankBattleActivity tankBattleActivity = null;
	private int cnt_ett = 0;
	private boolean flag = true;
	private int[][] tankImage = {
			{0, R.drawable.enemy1_down, R.drawable.enemy1_left, R.drawable.enemy1_right, R.drawable.enemy1_up},
			{0, R.drawable.enemy2_down, R.drawable.enemy2_left, R.drawable.enemy2_right, R.drawable.enemy2_up},
			{0, R.drawable.enemy3_down, R.drawable.enemy3_left, R.drawable.enemy3_right, R.drawable.enemy3_up}
	};
	public EnemyTankThread(TankBattleActivity tankBattleActivity){
		this.tankBattleActivity = tankBattleActivity;
	}
	 
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void run() {
		Random rd = new Random();
		while(flag){
			try{
				Set<TankView> tankSet = MySet.getInstance().getTankSet();
				if(tankSet.size() < 11){//一共20个tank
					int type = Math.abs(rd.nextInt())%3;//产生enemyTank的类型
					int dir = Math.abs(rd.nextInt())%4 + 1;//enemyTank的初始方向
					EnemyTankView etank = null;
					boolean one = false, two = false;
					Iterator<TankView> it = tankSet.iterator();
				    while(it.hasNext()){
				    	AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams)it.next().getLayoutParams();
				    	if(Rect.isCorss(new Rect(0,0,px2dip(tankBattleActivity,70),px2dip(tankBattleActivity,70)), new Rect(lp.x, lp.y, lp.x+lp.width, lp.y+lp.height)))
				    		one = true;//左上角区域被覆盖
				    	if(Rect.isCorss(new Rect(24*px2dip(tankBattleActivity,35),0,26*px2dip(tankBattleActivity,35),px2dip(tankBattleActivity,70)), new Rect(lp.x, lp.y, lp.x+lp.width, lp.y+lp.height)))
				    		two = true;//右上角区域被覆盖
				    }
				    if(!one){
				    	etank = new EnemyTankView(tankBattleActivity, 0, type);
				    	etank.setLayoutParams(new AbsoluteLayout.LayoutParams(px2dip(tankBattleActivity,70), px2dip(tankBattleActivity,70), 0, 0));
				    } else if(!two) {
				    	etank = new EnemyTankView(tankBattleActivity, 0, type);
				    	etank.setLayoutParams(new AbsoluteLayout.LayoutParams(px2dip(tankBattleActivity,70), px2dip(tankBattleActivity,70), 24*px2dip(tankBattleActivity,35),0));
				    }
					if(etank != null  && ++cnt_ett <= 20){
						final EnemyTankView enemyTank = etank;
						enemyTank.setScaleType(ImageView.ScaleType.FIT_XY);
						enemyTank.setCurDir(dir);
						enemyTank.setNextDir(dir);
						enemyTank.setImageResource(tankImage[type][dir]);
						tankSet.add(enemyTank);//将新出现的tank加入tank集合中
						tankBattleActivity.runOnUiThread(new Runnable() {
							public void run() {
								tankBattleActivity.getCenterView().addView(enemyTank);
								Iterator<OtherImageView> it = MySet.getInstance().getOtherSet().iterator();
								while(it.hasNext()){
									OtherImageView v = it.next();
									if(v.resID == R.drawable.grass)
										v.bringToFront();
								}
							}
						});
						new Thread(enemyTank, "enemyTankThread" + cnt_ett).start();
					}
				}
				Thread.sleep(1000);
			  } catch (Exception e) {
					Log.e("EnemyTankThread---run", e.getMessage());
			  }
		}
	}
	private int px2dip(Context context, int pxValue){
    	final float density = context.getResources().getDisplayMetrics().density;
    	return (int)(pxValue*density + 0.5f);
    }
	
}
