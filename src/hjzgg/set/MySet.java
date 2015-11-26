package hjzgg.set;

import hjzgg.tank.EnemyTankView;
import hjzgg.tank.OtherImageView;
import hjzgg.tank.ShellView;
import hjzgg.tank.TankView;
import hjzgg.thread.EnemyTankThread;
import hjzgg.thread.ShellThread;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
public class MySet {
	private Set<TankView> tankSet = new TreeSet<TankView>();//坦克的集合
	private EnemyTankThread ett = null;
	private ShellThread st = null;
	public Set<TankView> getTankSet() {
		return tankSet;
	}

	public Set<OtherImageView> getOtherSet() {
		return otherSet;
	}


	public Set<ShellView> getShellSet() {
		return shellSet;
	}

	private Set<OtherImageView> otherSet = new TreeSet<OtherImageView>();//静止障碍的集合
	private Set<ShellView> shellSet = new TreeSet<ShellView>();//炮弹的集合
	
	private static MySet myset = null;
	
	private MySet(){}
	
	public static MySet getInstance(){
		if(myset == null) myset = new MySet();
		return myset;
	}
	
	public static void resetMySet(){
		if(myset != null){
			if(myset.ett != null) myset.ett.setFlag(false);
			if(myset.st != null) myset.st.setFlag(false);
			Iterator<TankView> it = myset.tankSet.iterator();
			while(it.hasNext()){
				TankView v = it.next();
				if(v instanceof EnemyTankView)
					((EnemyTankView)v).setFlag();
			}
		}
		myset = null;
	}

	public EnemyTankThread getEtt() {
		return ett;
	}

	public void setEtt(EnemyTankThread ett) {
		this.ett = ett;
	}

	public ShellThread getSt() {
		return st;
	}

	public void setSt(ShellThread st) {
		this.st = st;
	}
	
}
