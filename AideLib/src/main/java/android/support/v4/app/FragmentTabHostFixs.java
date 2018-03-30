package android.support.v4.app;

import android.content.Context;
import android.util.AttributeSet;

// 修复原类的bug
public class FragmentTabHostFixs extends FragmentTabHost {
    public FragmentManager mFragmentManager;

    public FragmentTabHostFixs(Context context) {
        super(context);
    }

    public FragmentTabHostFixs(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        FragmentFixs.noteStateNotSaved(mFragmentManager);
        super.onAttachedToWindow();
    }

    @Override
    public void setCurrentTab(int index) {
        FragmentFixs.noteStateNotSaved(mFragmentManager);
        super.setCurrentTab(index);
    }

    @Override
    public void onTabChanged(String tabId) {
        FragmentFixs.noteStateNotSaved(mFragmentManager);
        super.onTabChanged(tabId);
    }

    public FragmentManager fix(FragmentManager fm) {
        FragmentFixs.noteStateNotSaved(mFragmentManager);
        mFragmentManager = fm;
        return fm;
    }
}
