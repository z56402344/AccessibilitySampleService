package android.support.v4.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

// 修复support fragment的bug
public class FragmentFixs extends Fragment {
    public static void noteStateNotSaved(Fragment f) {
        if (f!=null&&f.mFragmentManager!=null) {
            f.mFragmentManager.noteStateNotSaved();
        }
    }
    public static void noteStateNotSaved(FragmentManager fm) {
        if (fm!=null&&fm instanceof FragmentManagerImpl) {
            ((FragmentManagerImpl)fm).noteStateNotSaved();
        }
    }
    // "java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState"
    // 修复v4内部的bug:出现状态保存异常 可以调用修复
    public void onStateNotSaved() {
        noteStateNotSaved(this);
    }
    // java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
    // 调用onCreateView之后出现错误
    @Override
    View performCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.performCreateView(inflater, container, savedInstanceState);
        if (v!=null) {
            ViewParent parent = v.getParent();
            if (parent!=null&&parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(v);
            }
        }
        return v;
    }
}
