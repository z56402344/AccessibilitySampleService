package z.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

// 一个文本框
public class TextHolderFactory extends ViewHolderFactory<String> {
	public TextHolderFactory(ArrayList<String> msgs) {
		mDatas = msgs;
	}
	@Override
	public View getView(int i, View v, ViewGroup parent) {
		TextView tv;
		if (v==null) {
			tv = new TextView(parent.getContext());
			tv.setBackgroundColor(0xffff88ff);
			tv.setTextColor(0xff000000);
		} else {
			tv = (TextView)v;
		}
		tv.setText(mDatas.get(i));
		return tv;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int index) {
		Toast.makeText(v.getContext(),"Text点击"+mDatas.get(index),Toast.LENGTH_SHORT).show();
	}
}
