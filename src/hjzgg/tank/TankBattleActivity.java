package hjzgg.tank;

import hjzgg.set.MySet;
import hjzgg.thread.EnemyTankThread;
import hjzgg.thread.ShellThread;
import hjzgg.use.Direction;
import hjzgg.use.ID;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

class MyTankMove implements Runnable{
	private TankView tv = null;
	private Activity UI = null;
	private boolean flag = true;
	public void setFlag(){
		flag = false;
	}
	public MyTankMove(Activity UI, TankView tv){
		this.tv = tv;
		this.UI = UI;
	}
	public void run() {
		while(flag){
			try{
				UI.runOnUiThread(new Runnable() {
					public void run() {
						((AnimationDrawable)tv.getDrawable()).stop();
						tv.tankMove();//坦克移动
						((AnimationDrawable)tv.getDrawable()).start();
					}
				});
				Thread.sleep(50);
			} catch (Exception e) {
				Log.e("hjzgg", e.getMessage());
			}
		}
	}
}

public class TankBattleActivity extends Activity {
	private int[][] sta = new int[ID.staRow][ID.staCol];
	private byte[] mylock = new byte[0];
	private int _xDelta, _yDelta;
	private AbsoluteLayout centerView = null;
	private LinearLayout rightView = null;
	public int killTank;
	public boolean is_success;
	public AbsoluteLayout getCenterView(){
		return centerView;
	}
	
	public boolean checkSta(int id){
		if(id==R.drawable.grass || id==R.drawable.wall || id==R.drawable.steel_wall ||
				id==R.drawable.water) return true;
		return false;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_game);
        killTank = 0;
        is_success = true;
        rightView = (LinearLayout)findViewById(R.id.rightView);
        Intent intent = getIntent();
        for(int i=0; i<ID.staRow; ++i)
        	sta[i] = intent.getIntArrayExtra("hjzgg"+i);
        centerView =(AbsoluteLayout)findViewById(R.id.centerView);
        final int size = px2dip(this, 35);
        final TankView tankView = new TankView(this, 10, ID.mytank);
        for(int i=0; i<ID.staRow; ++i){
        	for(int j=0; j<ID.staCol; ++j){
        		final int x = i, y = j;
        		if(checkSta(sta[i][j])){
        			OtherImageView imgView = new OtherImageView(this, sta[x][y]);
        			MySet.getInstance().getOtherSet().add(imgView);
	        		imgView.setImageResource(sta[x][y]);
	        		//不要忘记定义组件的显示参数，否则采用的是默认的
	        		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(size, size, j*size, i*size);
	        		centerView.addView(imgView, params);
        		}
        	}
        }
        //通过这种方法实现重新定位,定位到中央
        ((FrameLayout)findViewById(R.id.gamedirbgID)).getBackground().setAlpha(100);
        ImageView gamedir = (ImageView)findViewById(R.id.gamedir);
        FrameLayout.LayoutParams gamedirLP = (FrameLayout.LayoutParams)gamedir.getLayoutParams();
        gamedirLP.setMargins(px2dip(this,50), px2dip(this,50), px2dip(this,200), px2dip(this,200));
        gamedir.requestLayout();
        gamedir.setClickable(true);
        gamedir.setOnTouchListener(new OnTouchListener() {
        	private MyTankMove myTankMove = null;
        	private void mytankMove(int x, int y){
    	    	final int speed = tankView.getSpeed();
    	    	int dir = 0;
    	    	if(x>0 && y>0){
    	    		if(Math.abs(x) >= Math.abs(y)) dir = Direction.right;
    	    		else dir = Direction.down;
    	    	} else if(x>0 && y<0){
    	    		if(Math.abs(x) >= Math.abs(y)) dir = Direction.right;
    	    		else dir = Direction.up;
    	    	} else if(x<0 && y<0) {
    	    		if(Math.abs(x) >= Math.abs(y)) dir = Direction.left;
    	    		else dir = Direction.up;
    	    	} else if(x<0 && y>0) {
    	    		if(Math.abs(x) >= Math.abs(y)) dir = Direction.left;
    	    		else dir = Direction.down;
    	    	}
    	    	if(dir == 0) dir = tankView.getCurDir();
    	    	tankView.setNextDir(dir);
    	    }
        	    
			public boolean onTouch(View v, MotionEvent event) {
				final int X = (int) event.getRawX(), Y = (int) event.getRawY();
				FrameLayout.LayoutParams gamedirLP = (FrameLayout.LayoutParams)v.getLayoutParams();
				if(event.getAction() == MotionEvent.ACTION_MOVE){//手指移动
					gamedirLP.leftMargin = X -_xDelta;
					gamedirLP.topMargin = Y - _yDelta;
					gamedirLP.rightMargin = -250;
					gamedirLP.bottomMargin = -250;
				} else if(event.getAction() == MotionEvent.ACTION_UP){//手指松开
					gamedirLP.leftMargin = px2dip(TankBattleActivity.this,50);
					gamedirLP.topMargin = px2dip(TankBattleActivity.this,50);
					gamedirLP.rightMargin = -250;
					gamedirLP.bottomMargin = -250;
					if(myTankMove != null) myTankMove.setFlag();
				} else if(event.getAction() == MotionEvent.ACTION_DOWN){
					_xDelta = X - gamedirLP.leftMargin;
					_yDelta = Y - gamedirLP.topMargin;
					myTankMove = new MyTankMove(TankBattleActivity.this, tankView);
					new Thread(myTankMove).start();
				}
				synchronized(mylock){//android 对触屏事件的相应比较敏感
					try {
						mylock.wait(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				int x1 = gamedirLP.leftMargin;
				int y1 = gamedirLP.topMargin;
				int x2 = gamedirLP.leftMargin+px2dip(TankBattleActivity.this,150);
				int y2 = gamedirLP.topMargin+px2dip(TankBattleActivity.this,150);
				if(!Circle.isOk(new Circle(x1, y1, x2, y2),  px2dip(TankBattleActivity.this, 125),  px2dip(TankBattleActivity.this,125),  px2dip(TankBattleActivity.this,60))) return true;
				int x0 = (x1+x2)/2 - px2dip(TankBattleActivity.this, 125);
				int y0 = (y1+y2)/2 - px2dip(TankBattleActivity.this, 125);
				mytankMove(x0, y0);
				v.setLayoutParams(gamedirLP);
				v.invalidate();//这行代码的位置很重要，一定要放在等待的后边，否则会出现重影现象
				return true;
			}
		});

        //发射炮弹的Button
        ImageView bombBtn = (ImageView)findViewById(R.id.bomb_btn);
        bombBtn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(final View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					TankBattleActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							((ImageView)v).setImageResource(R.drawable.bomb_btn_down);
						}
					});
				} else if(event.getAction() == MotionEvent.ACTION_UP) {
					TankBattleActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							((ImageView)v).setImageResource(R.drawable.bomb_btn_up);
							tankView.tankShell();
						}
					});
				}
				return true;
			}
		});
        
        //家的位置
        OtherImageView imgView = new OtherImageView(this, R.drawable.home);
        imgView.setImageResource(R.drawable.home);
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(size*4, size*3, 12*size, 15*size);
//        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(size*4, size*3, 12*size, 6*size);
        centerView.addView(imgView, params);
        MySet.getInstance().getOtherSet().add(imgView);
        //我的tank的位置
        tankView.setImageResource(R.anim.tank_up);
        params = new AbsoluteLayout.LayoutParams(size*2, size*2, 8*size, 16*size);
//        params = new AbsoluteLayout.LayoutParams(size*2, size*2, 8*size, 0);
        centerView.addView(tankView, params);
        MySet.getInstance().getTankSet().add(tankView);
        AnimationDrawable anim = (AnimationDrawable)tankView.getDrawable();
        anim.start();
        //开启炮弹的线程
        ShellThread st = new ShellThread(this);
        new Thread(st, "ShellThread").start();
        MySet.getInstance().setSt(st);
        
        //开始敌方tank线程
        EnemyTankThread ett = new EnemyTankThread(this);
        new Thread(ett, "EnemyTankThread").start();
        MySet.getInstance().setEtt(ett);
    }
    
    private int px2dip(Context context, int pxValue){
    	final float density = context.getResources().getDisplayMetrics().density;
    	return (int)(pxValue*density + 0.5f);
    }

	public void showMyDialog() {
		AlertDialog alert = (new AlertDialog.Builder(this)).create();
		alert.setIcon(R.drawable.modal_tank); // 设置对话框的图标
		alert.setTitle("是否继续游戏？"); // 设置对话框的标题
		alert.setMessage("继续HJZGG--TankBattle？"); // 设置要显示的内容
		// 添加取消按钮
		alert.setButton(DialogInterface.BUTTON_NEGATIVE, "不", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				 MySet.resetMySet();
				 finish();
			}
		});
		// 添加确定按钮
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "是的", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MySet.resetMySet();
				Intent intent = new Intent(TankBattleActivity.this, BeginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		alert.setCancelable(false);
		alert.setCanceledOnTouchOutside(false);
		alert.show(); // 创建对话框并显示
	}

	public LinearLayout getRightView() {
		return rightView;
	}
}