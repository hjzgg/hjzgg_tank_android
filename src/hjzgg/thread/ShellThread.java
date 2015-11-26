package hjzgg.thread;

import java.util.Iterator;
import java.util.Set;

import android.util.Log;

import hjzgg.set.MySet;
import hjzgg.tank.ShellView;
import hjzgg.tank.TankBattleActivity;

public class ShellThread implements Runnable{
	private boolean flag = true;
	private TankBattleActivity tankBattleActivity = null;
	public ShellThread(TankBattleActivity tankBattleActivity){
		this.tankBattleActivity = tankBattleActivity;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public void run() {
		while(flag){
			try{
				final Set<ShellView> shellSet = MySet.getInstance().getShellSet();
				tankBattleActivity.runOnUiThread(new Runnable() {
					public void run() {
						try{
							Iterator<ShellView> it = shellSet.iterator();
							while(it.hasNext())
								it.next().shellMove();
						} catch(Exception e){
							Log.e("ShellThread---run---run", e.toString());//ConcurrentModificationException 有待解决
						}
					}
				});
				Thread.sleep(20);
			} catch(Exception e){
				Log.e("ShellThread---run", e.getMessage());//ConcurrentModificationException 有待解决
			}
		}
	}
}
