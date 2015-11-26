package hjzgg.tank;

import android.content.Context;
import android.widget.ImageView;

public class OtherImageView extends ImageView implements Comparable<OtherImageView>{
	public int resID;
	public OtherImageView(Context context, int resID) {
		super(context);
		this.resID = resID;
	}

	public int compareTo(OtherImageView another) {
		return this.hashCode() - another.hashCode();
	}

}
