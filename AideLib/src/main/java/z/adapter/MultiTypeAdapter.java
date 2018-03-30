package z.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

// 实现多种类型的adapter
// 保存id的list list代表条目数
// id高32位=type(从1开始) id低32位=数据下标
public class MultiTypeAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
	protected ArrayList<Long> mIds = new ArrayList<Long>(8);
	protected ArrayList<ViewHolderFactory> mVhfs = new ArrayList<ViewHolderFactory>(8);
	public ScrollMngr scrollMngr;

	public MultiTypeAdapter withScroll() {
		scrollMngr = new ScrollMngr();
		return MultiTypeAdapter.this;
	}

	public ArrayList<Long> ids() {
		return mIds;
	}

	//清除所有数据
	public void clear(){
		for (ViewHolderFactory vf : mVhfs) {
			vf.clear(false);
		}
		mIds.clear();
	}
	public static long buildID(int type,int index) {
		return ((long)type)<<32|index;
	}
	// 追加一个/多个id
	public static void buildIDs(ArrayList<Long> ids,int type,int index,int count) {
		count += index;
		for (;index<count;++index) {
			ids.add(((long)type)<<32|index);
		}
	}
	public MultiTypeAdapter addType(ViewHolderFactory vhf) {
		vhf.mAdapter = this;
		mVhfs.add(vhf);
		vhf.mType = mVhfs.size(); // 从1开始
		return this;
	}
	public ArrayList<ViewHolderFactory> types() {
		return mVhfs;
	}
	@Override
	public int getCount() {
		return mIds.size();
	}
	@Override
	public Object getItem(int i) {
		long id = getItemId(i);
		i = (int)(id>>32)-1;
		if (i<0||i>=mVhfs.size()) return null;
		ViewHolderFactory vhf = mVhfs.get(i);
		i = (int)(id&0xffffffff); // index
		if (i<0) return null;
		return vhf.getItem(i);
	}
	@Override
	public long getItemId(int i) {
		return mIds.get(i);
	}
	@Override
	public int getViewTypeCount() {
		return mVhfs.size();
	}
	@Override
	public int getItemViewType(int position) {
		return (int)(getItemId(position)>>32)-1;//从1开始 需要-1
	}
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		long id = getItemId(i);
		i = (int)(id>>32)-1; // type
		ViewHolderFactory vhf = mVhfs.get(i);
		i = (int)(id&0xffffffff); // index
		return vhf.getView(i,view,viewGroup);
	}
	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int i, long id) {
		i = (int)(id>>32)-1; // type
		if (i<0||i>=mVhfs.size()) return; // 可能是head或foot
		ViewHolderFactory vhf = mVhfs.get(i);
		i = (int)(id&0xffffffff); // index
		vhf.onItemClick(adapterView,v,i);
	}

	public boolean replaceId(Long old,Long newId){
		int i = mIds.indexOf(old);
		if (i != -1){
			mIds.set(i,newId);
			return true;
		}
		return false;
	}
}
