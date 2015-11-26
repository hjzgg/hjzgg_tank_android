package hjzgg.tank;

import hjzgg.set.MySet;
import hjzgg.use.Direction;
import hjzgg.use.ID;

import java.util.Iterator;
import java.util.Set;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

public class ShellView extends ImageView implements Comparable<ShellView>{
	private int speed = 10;
	private int dir;
	public boolean disappear = false;
	private boolean good_or_bad; 
    private AbsoluteLayout centerView = null;
    private TankBattleActivity tankBattleActivity = null;
	public ShellView(Context context, int dir, boolean flag) {
		super(context);
		tankBattleActivity = (TankBattleActivity)context;
		this.centerView = tankBattleActivity.getCenterView();
		this.dir = dir;
		this.good_or_bad = flag;
	}
	
	class BombStopListener implements Runnable{
		private ImageView v = null;
		private boolean flag;//标记图片爆炸后是否消失
		public BombStopListener(ImageView v, boolean flag){
			this.v = v;
			this.flag = flag;
		}
		public void run() {
			try {
				if(v instanceof TankView){
					Iterator<TankView> it = MySet.getInstance().getTankSet().iterator();
					while(it.hasNext()){
						if(it.next() == v){
							it.remove();
							break;
						}
					}
				}
				else if(v instanceof ShellView){
					Iterator<ShellView> it = MySet.getInstance().getShellSet().iterator();
					while(it.hasNext()){
						if(it.next() == v){
							it.remove();
							break;
						}
					}
				}
				else{
					Iterator<OtherImageView> it = MySet.getInstance().getOtherSet().iterator();
					while(it.hasNext()){
						if(it.next() == v){
							it.remove();
							break;
						}
					}
				}
				
				Thread.sleep(900);
				tankBattleActivity.runOnUiThread(new Runnable(){
					public void run() {
						if(flag){//爆照之后消失
//							v.setVisibility(View.GONE);
							AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams)v.getLayoutParams();
							lp.x = -200;
							lp.y = -200;
							v.setLayoutParams(lp);
							v.invalidate();
						} else {
							v.setImageResource(R.drawable.home_destroy);
							v.invalidate();
							if(tankBattleActivity.is_success){
								tankBattleActivity.is_success = false;
								tankBattleActivity.showMyDialog();
							}
						}
					}
				});
				((AnimationDrawable)v.getDrawable()).stop();
			} catch (Exception e) {
				Log.e("ShellView----BombStopListener", e.getMessage());
			} finally {
				if(v instanceof TankView){
					if(((TankView)v).getTankID() == ID.mytank && tankBattleActivity.is_success){
						tankBattleActivity.is_success = false;
						Looper.prepare();
						tankBattleActivity.showMyDialog();
						Looper.myLooper().loop();
					}
					else{
						final ImageView v = new ImageView(tankBattleActivity);
						v.setImageResource(R.drawable.modal_tank);
						v.setMaxHeight(20);
						v.setMaxWidth(20);
						tankBattleActivity.runOnUiThread(new Runnable() {
							public void run() {
								tankBattleActivity.getRightView().addView(v);
							}
						});
						if(++tankBattleActivity.killTank == 20 && tankBattleActivity.is_success){
							tankBattleActivity.is_success = false;
							Looper.prepare();
							tankBattleActivity.showMyDialog();
							Looper.myLooper().loop();
						}
					}
				}
			}
		}
	}
	
	public void checkMove(AbsoluteLayout.LayoutParams LPshell){
		try{
			if(LPshell.x > centerView.getWidth() || LPshell.y > centerView.getHeight() || LPshell.x + LPshell.width < 0 || LPshell.y + LPshell.height < 0){
				//在画面中消失，在炮弹集合中消失
				Set<ShellView> ss = MySet.getInstance().getShellSet();
				Iterator<ShellView> it = ss.iterator();
				while(it.hasNext()){
					if(it.next() == ShellView.this){
						it.remove();
						break;
					}
				}
				ShellView.this.setVisibility(View.GONE);
				centerView.removeView(ShellView.this);
				return;
			}
			boolean is_destroy = false;
			//检验子弹是否与tank相撞
			Iterator<TankView> it = MySet.getInstance().getTankSet().iterator();
			while(it.hasNext()){
				TankView v = it.next();
				AbsoluteLayout.LayoutParams LPtank = (AbsoluteLayout.LayoutParams)v.getLayoutParams();
				if(!v.disappear && (v.getTankID() == ID.mytank && this.good_or_bad == false || v.getTankID() != ID.mytank && this.good_or_bad == true) &&
						Rect.isCorss(new Rect(LPshell.x, LPshell.y, LPshell.x+LPshell.width, LPshell.y+LPshell.height), new Rect(LPtank.x, LPtank.y, LPtank.x+LPtank.width, LPtank.y+LPtank.height))){
					if(v instanceof EnemyTankView) ((EnemyTankView)v).setFlag();//终止该线程
					v.setImageResource(R.anim.destroy_bomb);
					v.setScaleType(ImageView.ScaleType.FIT_XY);
					v.disappear = true;
					((AnimationDrawable)v.getDrawable()).setOneShot(true);
					((AnimationDrawable)v.getDrawable()).start();
					new Thread(new BombStopListener(v, true)).start();
					is_destroy = true;
					break;
				}
			}
			//检验子弹是否于子弹相撞
			if(!is_destroy) {
			    Iterator<ShellView> itx = MySet.getInstance().getShellSet().iterator();
				while(itx.hasNext()){
					ShellView v = itx.next();
					AbsoluteLayout.LayoutParams LPshellTmp = (AbsoluteLayout.LayoutParams)v.getLayoutParams();
					if(!v.disappear && (this.good_or_bad==false && v.good_or_bad==true || this.good_or_bad==true && v.good_or_bad==false)&& Rect.isCorss(new Rect(LPshell.x, LPshell.y, LPshell.x+LPshell.width, LPshell.y+LPshell.height), new Rect(LPshellTmp.x, LPshellTmp.y, LPshellTmp.x+LPshellTmp.width, LPshellTmp.y+LPshellTmp.height))){
						v.setImageResource(R.anim.destroy_bomb);
						v.setScaleType(ImageView.ScaleType.FIT_XY);
						((AnimationDrawable)v.getDrawable()).setOneShot(true);
						((AnimationDrawable)v.getDrawable()).start();
						new Thread(new BombStopListener(v, true)).start();
						is_destroy = true;
						v.disappear = true;
						break;
					}
				}
			}
			//检验子弹是否与障碍物相撞
			if(!is_destroy) {
			    Iterator<OtherImageView> itx = MySet.getInstance().getOtherSet().iterator();
				while(itx.hasNext()){
					OtherImageView v = itx.next();
					AbsoluteLayout.LayoutParams LPshellTmp = (AbsoluteLayout.LayoutParams)v.getLayoutParams();
					if( Rect.isCorss(new Rect(LPshell.x, LPshell.y, LPshell.x+LPshell.width, LPshell.y+LPshell.height), new Rect(LPshellTmp.x, LPshellTmp.y, LPshellTmp.x+LPshellTmp.width, LPshellTmp.y+LPshellTmp.height))){
						if(v.resID == R.drawable.wall || v.resID == R.drawable.home){
							v.setImageResource(R.anim.destroy_bomb);
							v.setScaleType(ImageView.ScaleType.FIT_XY);
							((AnimationDrawable)v.getDrawable()).setOneShot(true);
							((AnimationDrawable)v.getDrawable()).start();
							if(v.resID == R.drawable.wall)
									new Thread(new BombStopListener(v, true)).start();
							else 	new Thread(new BombStopListener(v, false)).start();
						}
						if(v.resID != R.drawable.grass && v.resID != R.drawable.water){
							is_destroy = true;
							break;
						}
					}
				}
			}
			
			if(is_destroy){
				disappear = true;
				this.setImageResource(R.anim.destroy_bomb);
				this.setScaleType(ImageView.ScaleType.FIT_XY);
				((AnimationDrawable)this.getDrawable()).setOneShot(true);
				((AnimationDrawable)this.getDrawable()).start();
				new Thread(new BombStopListener(this, true)).start();
			}
			
		} catch(Exception e){
			Log.e("ShellView----ifCanMove", e.getMessage());
		}
	}
	
	public void shellMove(){
		if(disappear) return;
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) this.getLayoutParams();
    	switch(dir){
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
    	this.setLayoutParams(params);
    	this.invalidate();
    	checkMove(params);
	}
	public int compareTo(ShellView another) {
		return 1;
	}
}