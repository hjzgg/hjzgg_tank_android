package hjzgg.tank;

import java.util.Iterator;

import hjzgg.set.MySet;
import hjzgg.use.Direction;
import hjzgg.use.ID;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ImageView;

public class TankView extends ImageView implements Comparable<TankView>{
	private int[][] tankToShell = {
			{0, R.drawable.shell_down1, R.drawable.shell_left1, R.drawable.shell_right1, R.drawable.shell_up1},
			{0, R.drawable.shell_down2, R.drawable.shell_left2, R.drawable.shell_right2, R.drawable.shell_up2},
			{0, R.drawable.shell_down3, R.drawable.shell_left3, R.drawable.shell_right3, R.drawable.shell_up3},
			{0, R.drawable.shell_down4, R.drawable.shell_left4, R.drawable.shell_right4, R.drawable.shell_up4}
	};
	
	private int[][] tankImage = {
			{0, R.drawable.enemy1_down, R.drawable.enemy1_left, R.drawable.enemy1_right, R.drawable.enemy1_up},
			{0, R.drawable.enemy2_down, R.drawable.enemy2_left, R.drawable.enemy2_right, R.drawable.enemy2_up},
			{0, R.drawable.enemy3_down, R.drawable.enemy3_left, R.drawable.enemy3_right, R.drawable.enemy3_up},
			{0, R.anim.tank_down, R.anim.tank_left, R.anim.tank_right, R.anim.tank_up}
	};
	public int speed;//速度
	private int tankID;
	public boolean disappear = false;
	private int nextDir = Direction.up;//新的方向
	private int curDir = Direction.up;//当前方向
	private TankBattleActivity tankBattleActivity = null;
	private AbsoluteLayout centerView = null;
	public TankView(Context context, int speed, int tankID) {
		super(context);
		tankBattleActivity = (TankBattleActivity)context;
		this.speed=speed;
		this.tankID = tankID;
		centerView = tankBattleActivity.getCenterView();
	}
	
	public int getTankID(){
		return tankID;
	}
	
	public int getCurDir() {
		return curDir;
	}
	public void setCurDir(int curDir) {
		this.curDir = curDir;
	}
	public int getSpeed() {
		return speed;
	}
	public int getNextDir() {
		return nextDir;
	}
	public void setNextDir(int nextDir) {
		this.nextDir = nextDir;
	}
	
	public void tankTurn(){
		if(disappear) return;
		if(curDir != nextDir)
			this.setCurDir(nextDir);
		this.setImageResource(tankImage[tankID][nextDir]);
	}
	

	public boolean ifCanMove(AbsoluteLayout.LayoutParams params){
		if(params.x < 0 || params.y < 0 || params.x + params.width > centerView.getWidth() || params.y + params.height > centerView.getHeight())
			return false;
		try{
			Iterator<TankView> it = MySet.getInstance().getTankSet().iterator();
			while(it.hasNext()){
				TankView v = it.next();
				AbsoluteLayout.LayoutParams tmp = (AbsoluteLayout.LayoutParams) v.getLayoutParams();
				if(v!=this && Rect.isCorss(new Rect(tmp.x, tmp.y, tmp.x+tmp.width, tmp.y+tmp.height), new Rect(params.x, params.y, params.x+params.width, params.y+params.height)))
					return false;
			}
			
			Iterator<OtherImageView> itx = MySet.getInstance().getOtherSet().iterator();
			while(itx.hasNext()){
				OtherImageView v = itx.next();
				AbsoluteLayout.LayoutParams tmp = (AbsoluteLayout.LayoutParams) v.getLayoutParams();
				if(Rect.isCorss(new Rect(tmp.x, tmp.y, tmp.x+tmp.width, tmp.y+tmp.height), new Rect(params.x, params.y, params.x+params.width, params.y+params.height))){
					if(v.resID == R.drawable.grass) continue;
					return false;
				}
			}
		} catch(Exception e){
			Log.e("TankView--ifCanMove", e.toString());
		}
		return true;
	}
	
	public void tankMove(){//tank 移动
		if(disappear) return;
		if(curDir != nextDir){
			tankTurn();
		} else {
			AbsoluteLayout.LayoutParams tmp = (AbsoluteLayout.LayoutParams) this.getLayoutParams();
			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(tmp.width, tmp.height, tmp.x, tmp.y);
	    	switch(curDir){
	    		case Direction.up:
	    			params.y -= speed;
	    			break;
	    		case Direction.down:
	    			params.y += speed;
	    			break;
	    		case Direction.left:
	    			params.x -= speed;
	    			break;
	    		case Direction.right:
	    			params.x += speed;
	    			break;
	    		default:
	    			break;
	    	}
	    	if(!ifCanMove(params))
	    		return;
	    	this.setLayoutParams(params);
		}
		this.invalidate();
	}
	
	class BombStopListener implements Runnable{
		private ImageView bombView = null;
		public BombStopListener(ImageView bombView){
			this.bombView = bombView;
		}
		public void run() {
			try {
				Thread.sleep(900);
				((AnimationDrawable)bombView.getDrawable()).stop();
				tankBattleActivity.runOnUiThread(new Runnable() {
					public void run() {
						bombView.setVisibility(GONE);
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void tankShell(){//tank 发射炮弹
		ImageView bombView = new ImageView(tankBattleActivity);
		AbsoluteLayout.LayoutParams params = null;
		int size = px2dip(tankBattleActivity, 50);
		AbsoluteLayout.LayoutParams tankParams = (AbsoluteLayout.LayoutParams) this.getLayoutParams();
		int x=0, y=0;
		switch(curDir){
			case Direction.up:
				bombView.setImageResource(R.anim.bomb_up);
				x = tankParams.x + px2dip(tankBattleActivity, 10);
				y = tankParams.y - size + 10;
			    params = new AbsoluteLayout.LayoutParams(size, size, x, y);
				break;
			case Direction.down:
				bombView.setImageResource(R.anim.bomb_down);
				x = tankParams.x + px2dip(tankBattleActivity, 10);
				y = tankParams.y + tankParams.height - 20;
				params = new AbsoluteLayout.LayoutParams(size, size, x, y);
				break;
			case Direction.left:
				bombView.setImageResource(R.anim.bomb_left);
				x = tankParams.x - size + 10;
				y = tankParams.y + px2dip(tankBattleActivity, 10) - 10;
				params = new AbsoluteLayout.LayoutParams(size, size, x, y);
				break;
			case Direction.right:
				bombView.setImageResource(R.anim.bomb_right);
				x = tankParams.x + tankParams.width - 10;
				y = tankParams.y + px2dip(tankBattleActivity, 10) - 10;
				params = new AbsoluteLayout.LayoutParams(size, size, x, y);
				break;
			default:
				break;
		}
		
		//爆炸时加入炮弹view
		boolean flag = false;
		if(tankID == ID.mytank) flag = true;
		final ShellView sv = new ShellView(tankBattleActivity, curDir, flag);
		sv.setImageResource(tankToShell[tankID][curDir]);
		sv.setScaleType(ImageView.ScaleType.FIT_XY);
		AbsoluteLayout.LayoutParams shellParams = null;
		switch(curDir){
			case Direction.up:
			case Direction.down:
				shellParams = new AbsoluteLayout.LayoutParams(px2dip(tankBattleActivity, 15), px2dip(tankBattleActivity, 50), params.x + px2dip(tankBattleActivity, 15), params.y);
				break;
			case Direction.left:
			case Direction.right:
				shellParams = new AbsoluteLayout.LayoutParams(px2dip(tankBattleActivity, 50), px2dip(tankBattleActivity, 15), params.x, params.y + px2dip(tankBattleActivity, 15));
				break;
			default:
				break;
		}
		final AbsoluteLayout.LayoutParams tmpShellParams = shellParams;
		sv.setLayoutParams(tmpShellParams);
		centerView.addView(sv);
		MySet.getInstance().getShellSet().add(sv);//将炮弹添加到集合中去	
		//添加爆炸图片
		centerView.addView(bombView, params);
		AnimationDrawable anim = (AnimationDrawable)bombView.getDrawable();
		anim.setOneShot(true);
	    anim.start();
	    new Thread(new BombStopListener(bombView)).start();
	}
	
	private int px2dip(Context context, int pxValue){
    	final float density = context.getResources().getDisplayMetrics().density;
    	return (int)(pxValue*density + 0.5f);
    }
	public int compareTo(TankView another) {
		return this.hashCode() - another.hashCode();
	}
}
