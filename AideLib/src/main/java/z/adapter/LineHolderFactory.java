package z.adapter;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

// 分隔线类型 可以添加多种分隔线 使用id中的index标志使用第几个
// 分隔线的高度在添加之前已经设置
public class LineHolderFactory extends ViewHolderFactory<Drawable> {
	public LineHolderFactory add(Drawable d) {
		if (mDatas==null) {
			mDatas = new ArrayList<Drawable>(8);
		}
		mDatas.add(d);
		return this;
	}
	@Override
	public View getView(int i, View v, ViewGroup parent) {
		if (v==null) {
			v = new View(parent.getContext());
//			v.setMinimumHeight(2);
//			v.setBackgroundColor(0xff888888);
		}
		Drawable d = null;
		if (i>=0&&i<mDatas.size()) {
			d = mDatas.get(i);
		} else {
			d = mDatas.get(0);
		}
		v.setBackgroundDrawable(d);
		return v;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int index) {
//		Toast.makeText(v.getContext(), "Line点击:" + index, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void clear(boolean force) {
		if (!force) return;
		super.clear(force);
	}
}
