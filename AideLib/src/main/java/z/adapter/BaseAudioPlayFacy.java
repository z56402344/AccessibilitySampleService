package z.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import z.media.BasicAudioPlay;

/**
 * 统一处理列表中需要播放的音频的facy
 */
public class BaseAudioPlayFacy<T extends Object> extends ViewHolderFactory<T> {
    public PlaceViewHolderFacy mPlace = new PlaceViewHolderFacy(null);
    protected int mCur = -1;
    protected BasicAudioPlay mBap;
    protected BasicAudioPlay.Cfg mCfg = new BasicAudioPlay.Cfg();

    public void replaceCur() {
        if (mCur != -1) {
            if (mCur >= mDatas.size()) {
                mCur = -1;
                return;
            }
            mAdapter.replaceId(id(mCur), mPlace.id(0));
        }
    }

    public void updatePlay(String url, int pos, View v) {
        if (TextUtils.isEmpty(url)) return;
        if (mCur != pos) {
            Long id = mPlace.id(0);
            if (mCur != -1) mAdapter.replaceId(id, id(mCur));
            mAdapter.replaceId(id(pos), id);
            mCur = pos;
            mPlace.mView = getView(pos, mPlace.mView, (ViewGroup) v.getParent());
            mAdapter.notifyDataSetChanged();
        }
        mBap.updatePlay(url, mCur | (mType << 16), mCfg);
    }
}
