package z.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * 占位用的Facy  传进来一个view占位用， 主要用于不需要复用的header，footer
 */
public class PlaceViewHolderFacy extends ViewHolderFactory {

    public View mView;
    public PlaceViewHolderFacy(View v){
        mView = v;
    }

    @Override
    public View getView(int i, View v, ViewGroup parent) {
        return mView;
    }
}
