package hjzgg.tank;

import hjzgg.use.Direction;
import hjzgg.use.ID;

import java.util.Random;

import android.content.Context;
import android.util.Log;


public class EnemyTankView extends TankView implements Runnable{
	//坦克的速度e
	private int f_sleep = 0;//坦克移动的频率
	private TankBattleActivity tankBattleActivity = null;
	private boolean flag = true;
	public void setFlag(){
		flag = false;
	}
	public EnemyTankView(Context context, int speed, int tankID){
		super(context, speed, tankID);
		tankBattleActivity = (TankBattleActivity)context;
		if(tankID == ID.enemy3) {
			super.speed = 15;
			f_sleep = 100;
		}
		else {
			super.speed = 10;
			f_sleep = 500;
		}
	}
	public void run(){
		final Random rd = new Random();
		while(flag){
			final int key = Math.abs(rd.nextInt())%6;
			tankBattleActivity.runOnUiThread(new Runnable() {
				public void run() {
					switch(key){
						case 0:
						case 1:
						case 2:
							if(getCurDir() != Direction.down){
								setNextDir(Direction.down);
								tankTurn();
							}
							tankMove();
							break;
						case 3://向当前方向移动
							tankMove();
							break;
						case 4://转向
							int dir = Math.abs(rd.nextInt())%4 + 1;
							if(dir == getCurDir()){
								tankMove();
								break;
							} else {
								setNextDir(dir);
								tankTurn();
							}
							break;
						default://保持不动
							int k = Math.abs(rd.nextInt())%100;
							if(k % 5 ==0)
								tankShell();//发射炮弹
							break;
					}
				}
			});
			try {
				Thread.sleep(f_sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
