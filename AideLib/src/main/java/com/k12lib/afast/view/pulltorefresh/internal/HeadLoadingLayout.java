/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.k12lib.afast.view.pulltorefresh.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.k12lib.R;
import com.k12lib.afast.log.Logger;
import com.k12lib.afast.view.pulltorefresh.PullToRefreshBase.Mode;
import com.k12lib.afast.view.pulltorefresh.PullToRefreshBase.Orientation;

import java.util.Random;

@SuppressLint("ViewConstructor")
public abstract class HeadLoadingLayout extends LoadingLayout {

	static final String LOG_TAG = "PullToRefresh-LoadingLayout";

	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    private static final String TAG = HeadLoadingLayout.class.getSimpleName();

    private FrameLayout mInnerLayout;

	protected final ImageView mHeaderImage,mIvLogo;
	protected final ProgressBar mHeaderProgress;

	private boolean mUseIntrinsicAnimation;

	private final TextView mHeaderText;
	private final TextView mSubHeaderText;
    private final TextView mTvEn,mTvCn;

	protected final Mode mMode;
	protected final Orientation mScrollDirection;

	private CharSequence mPullLabel;
	private CharSequence mRefreshingLabel;
	private CharSequence mReleaseLabel;

    private String[] mCns ;
    private String[] mEns ;

	public HeadLoadingLayout(Context context, final Mode mode, final Orientation scrollDirection, TypedArray attrs) {
		super(context);
		mMode = mode;
		mScrollDirection = scrollDirection;
        Log.i("LoadingLayout","LoadingLayout 一次>>> ");
		switch (scrollDirection) {
			case HORIZONTAL:
                    LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_horizontal, this);
				break;
			case VERTICAL:
                LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_vertical, this);
                break;
			default:
				LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_horizontal, this);
				break;
		}

		mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
        mTvEn = (TextView) mInnerLayout.findViewById(R.id.mTvEn);
        mTvCn = (TextView) mInnerLayout.findViewById(R.id.mTvCn);
        mHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);
		mHeaderProgress = (ProgressBar) mInnerLayout.findViewById(R.id.pull_to_refresh_progress);
		mSubHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);
		mHeaderImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_image);
        mIvLogo = (ImageView) mInnerLayout.findViewById(R.id.mIvLogo);

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();
        mCns = context.getResources().getStringArray(R.array.item_cn);
        mEns = context.getResources().getStringArray(R.array.item_en);

        //随机的名言警句
        Random random = new Random();
        int i = random.nextInt(10);
        mTvCn.setText(mCns[i]);
        mTvEn.setText(mEns[i]);

		switch (mode) {
			case PULL_FROM_END:
				lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT;

				// Load in labels
				mPullLabel = context.getString(R.string.pull_to_refresh_from_bottom_pull_label);
				mRefreshingLabel = context.getString(R.string.pull_to_refresh_from_bottom_refreshing_label);
				mReleaseLabel = context.getString(R.string.pull_to_refresh_from_bottom_release_label);
				break;

			case PULL_FROM_START:
			default:
				lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT;

				// Load in labels
				mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
				mRefreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
				mReleaseLabel = context.getString(R.string.pull_to_refresh_release_label);
				break;
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
			Drawable background = attrs.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
			if (null != background) {
				ViewCompat.setBackground(this, background);
			}
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance, styleID);
			setTextAppearance(styleID.data);
		}
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance, styleID);
			setSubTextAppearance(styleID.data);
		}

		// Text Color attrs need to be set after TextAppearance attrs
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextColor)) {
			ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderTextColor);
			if (null != colors) {
				setTextColor(colors);
			}
		}
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderSubTextColor)) {
			ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderSubTextColor);
			if (null != colors) {
				setSubTextColor(colors);
			}
		}

		// Try and get defined drawable from Attrs
		Drawable imageDrawable = null;
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawable)) {
			imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawable);
		}

		// Check Specific Drawable from Attrs, these overrite the generic
		// drawable attr above
		switch (mode) {
			case PULL_FROM_START:
			default:
				if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableStart)) {
					imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableStart);
				} else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableTop)) {
					Utils.warnDeprecation("ptrDrawableTop", "ptrDrawableStart");
					imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableTop);
				}
				break;

			case PULL_FROM_END:
				if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableEnd)) {
					imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableEnd);
				} else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableBottom)) {
					Utils.warnDeprecation("ptrDrawableBottom", "ptrDrawableEnd");
					imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableBottom);
				}
				break;
		}

		// If we don't have a user defined drawable, load the default
		if (null == imageDrawable) {
			imageDrawable = context.getResources().getDrawable(getDefaultDrawableResId());
		}

		// Set Drawable, and save width/height
		setLoadingDrawable(imageDrawable);

		reset();
	}

	public final void setHeight(int height) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.height = height;
		requestLayout();
	}

	public final void setWidth(int width) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.width = width;
		requestLayout();
	}

	public final int getContentSize() {
		switch (mScrollDirection) {
			case HORIZONTAL:
				return mInnerLayout.getWidth();
			case VERTICAL:
			default:
				return mInnerLayout.getHeight();
		}
	}

	public final void hideAllViews() {
//		if (View.VISIBLE == mHeaderText.getVisibility()) {
//			mHeaderText.setVisibility(View.INVISIBLE);
//		}
//		if (View.VISIBLE == mHeaderProgress.getVisibility()) {
//			mHeaderProgress.setVisibility(View.INVISIBLE);
//		}
		if (View.VISIBLE == mHeaderImage.getVisibility()) {
			mHeaderImage.setVisibility(View.INVISIBLE);
		}
//		if (View.VISIBLE == mSubHeaderText.getVisibility()) {
//			mSubHeaderText.setVisibility(View.INVISIBLE);
//		}

        if (View.VISIBLE == mTvCn.getVisibility()) {
            mTvCn.setVisibility(View.INVISIBLE);
        }

        if (View.VISIBLE == mTvEn.getVisibility()) {
            mTvEn.setVisibility(View.INVISIBLE);
        }

        if (View.VISIBLE == mIvLogo.getVisibility()) {
            mIvLogo.setVisibility(View.INVISIBLE);
        }

	}

    //下拉时距离的回调
	public final void onPull(float scaleOfLayout) {
		if (!mUseIntrinsicAnimation) {
			onPullImpl(scaleOfLayout);
		}
        if(scaleOfLayout <= 1){
            mTvEn.setTextSize(TypedValue.COMPLEX_UNIT_SP,17 * scaleOfLayout);
            mTvCn.setTextSize(TypedValue.COMPLEX_UNIT_SP,17 * scaleOfLayout);
        }else{
            mTvEn.setTextSize(TypedValue.COMPLEX_UNIT_SP,17 * 1);
            mTvCn.setTextSize(TypedValue.COMPLEX_UNIT_SP,17 * 1);
        }
//        Log.i("LoadingLayout","scaleOfLayout >>> "+scaleOfLayout);
    }

	public final void pullToRefresh() {
		if (null != mHeaderText) {
			mHeaderText.setText(mPullLabel);
		}
		// Now call the callback
		pullToRefreshImpl();
        Log.i("LoadingLayout","pullToRefresh 一次>>> ");
	}

	public final void refreshing() {
		if (null != mHeaderText) {
			mHeaderText.setText(mRefreshingLabel);
		}
        mTvCn.setVisibility(View.GONE);
        mTvEn.setVisibility(View.GONE);
        mIvLogo.setVisibility(View.VISIBLE);
        mHeaderImage.setVisibility(View.VISIBLE);
		if (mUseIntrinsicAnimation) {
			((AnimationDrawable) mHeaderImage.getDrawable()).start();
		} else {
			// Now call the callback
			refreshingImpl();
		}

		if (null != mSubHeaderText) {
			mSubHeaderText.setVisibility(View.GONE);
		}
        Log.i("LoadingLayout","refreshing 一次>>> ");

	}

	public final void releaseToRefresh() {
		if (null != mHeaderText) {
			mHeaderText.setText(mReleaseLabel);
		}

		// Now call the callback
		releaseToRefreshImpl();
        Log.i("LoadingLayout","releaseToRefresh 一次>>> ");
	}

	public final void reset() {
		if (null != mHeaderText) {
			mHeaderText.setText(mPullLabel);
		}
		mHeaderImage.setVisibility(View.VISIBLE);
        mIvLogo.setVisibility(View.VISIBLE);

        mTvCn.setVisibility(View.VISIBLE);
        mTvEn.setVisibility(View.VISIBLE);
        mHeaderImage.setVisibility(View.INVISIBLE);

        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable) mHeaderImage.getDrawable()).stop();
		} else {
			// Now call the callback
			resetImpl();
		}

//		if (null != mSubHeaderText) {
//			if (TextUtils.isEmpty(mSubHeaderText.getText())) {
//				mSubHeaderText.setVisibility(View.GONE);
//			} else {
//				mSubHeaderText.setVisibility(View.VISIBLE);
//			}
//		}
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		setSubHeaderText(label);
	}

	public final void setLoadingDrawable(Drawable imageDrawable) {
		// Set Drawable
		mHeaderImage.setImageDrawable(imageDrawable);
		mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable);
        Log.i(TAG,"setLoadingDrawable  一次 >>> "+mUseIntrinsicAnimation);
		// Now call the callback
		onLoadingDrawableSet(imageDrawable);
	}

	public void setPullLabel(CharSequence pullLabel) {
		mPullLabel = pullLabel;
	}

	public void setRefreshingLabel(CharSequence refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(CharSequence releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	@Override
	public void setTextTypeface(Typeface tf) {
		mHeaderText.setTypeface(tf);
	}

	public final void showInvisibleViews() {
//		if (View.INVISIBLE == mHeaderText.getVisibility()) {
//			mHeaderText.setVisibility(View.VISIBLE);
//		}
//		if (View.INVISIBLE == mHeaderProgress.getVisibility()) {
//			mHeaderProgress.setVisibility(View.VISIBLE);
//		}
		if (View.INVISIBLE == mHeaderImage.getVisibility()) {
			mHeaderImage.setVisibility(View.VISIBLE);
		}
//		if (View.INVISIBLE == mSubHeaderText.getVisibility()) {
//			mSubHeaderText.setVisibility(View.VISIBLE);
//		}

        if (View.VISIBLE == mTvCn.getVisibility()) {
            mTvCn.setVisibility(View.INVISIBLE);
        }

        if (View.VISIBLE == mTvEn.getVisibility()) {
            mTvEn.setVisibility(View.INVISIBLE);
        }

        if (View.VISIBLE == mIvLogo.getVisibility()) {
            mIvLogo.setVisibility(View.VISIBLE);
        }
        Log.i(TAG,"showInvisibleViews  一次 >>> ");
	}

	/**
	 * Callbacks for derivative Layouts
	 */

	protected abstract int getDefaultDrawableResId();

	protected abstract void onLoadingDrawableSet(Drawable imageDrawable);

	protected abstract void onPullImpl(float scaleOfLayout);

	protected abstract void pullToRefreshImpl();

	protected abstract void refreshingImpl();

	protected abstract void releaseToRefreshImpl();

	protected abstract void resetImpl();

	private void setSubHeaderText(CharSequence label) {
		if (null != mSubHeaderText) {
			if (TextUtils.isEmpty(label)) {
				mSubHeaderText.setVisibility(View.GONE);
			} else {
				mSubHeaderText.setText(label);

				// Only set it to Visible if we're GONE, otherwise VISIBLE will
				// be set soon
				if (View.GONE == mSubHeaderText.getVisibility()) {
					mSubHeaderText.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void setSubTextAppearance(int value) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	private void setSubTextColor(ColorStateList color) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

	private void setTextAppearance(int value) {
		if (null != mHeaderText) {
			mHeaderText.setTextAppearance(getContext(), value);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	private void setTextColor(ColorStateList color) {
		if (null != mHeaderText) {
			mHeaderText.setTextColor(color);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

    public void setHeadSize(int headSize) {
        Logger.d(TAG,"headSize >>> "+headSize);
    }
}
