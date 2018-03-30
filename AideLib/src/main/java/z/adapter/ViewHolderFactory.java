package z.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

// 不同类型的viewtype工厂
// 基类 不同的子类有特殊实现
public class ViewHolderFactory<T extends Object> {
    public int mType; // 自动生成的类型 从1开始
    public MultiTypeAdapter mAdapter; // 归属adapter
    // 数据
    public ArrayList<T> mDatas;

    // 子类必须实现
    public View getView(int i, View v, ViewGroup parent) {
        return null;
    }

    // 子类实现 点击
    public void onItemClick(AdapterView<?> parent, View v, int index) {

    }

    public Object getItem(int i) {
        if (mDatas != null && i < mDatas.size()) {
            return mDatas.get(i);
        }
        return null;
    }

    public long id(int idx) {
        return MultiTypeAdapter.buildID(mType, idx);
    }

    public void clear(boolean force) {
        if (mDatas != null) mDatas.clear();
    }

    public long addData(T t) {
        long id = id(mDatas.size());
        mDatas.add(t);
        return id;
    }

    public void addData(ArrayList<Long> ids, ArrayList<T> tl, Long sepId) {
        addData(ids, tl, sepId, false);
    }

    public void addData(ArrayList<Long> ids, ArrayList<T> tl, Long sepId, boolean isHideLastSep) {
        if (tl==null||tl.size()==0) return;
        int idx = mDatas.size();
        if (sepId != null) {
            for (T t : tl) {
                ids.add(id(idx++));
                ids.add(sepId);
            }
            if (isHideLastSep) {
                ids.remove(ids.size() - 1);
            }
        } else {
            for (T t : tl) {
                ids.add(id(idx++));
            }
        }
        mDatas.addAll(tl);
    }

    public void addData(ArrayList<Long> ids, ArrayList<T> tl, Long sepId, boolean isHideLastSep,int start, int end) {
        if (tl==null||tl.size()==0 || end <= start) return;
        int count = tl.size();
        if (count <= end) end = count - 1;
        count = mDatas.size();
        if (sepId != null) {
            for (;start < end; start++) {
                ids.add(id(count++));
                ids.add(sepId);
                mDatas.add(tl.get(start));
            }
            if (isHideLastSep) {
                ids.remove(ids.size() - 1);
            }
        } else {
            for (;start < end; start++) {
                ids.add(id(count++));
                mDatas.add(tl.get(start));
            }
        }
    }
}
