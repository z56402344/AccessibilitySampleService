package z.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

// 横线 左右缩进横线 上下线
public class Lines extends BaseDrawable {
	public int mColor = 0xff888888;
	public int mH = 0;
	public Lines withColorH(int color,int h) {
		mColor = color;
		mPaint.setColor(mColor);
		mPaint.setStyle(Paint.Style.FILL);
		mH = h;
		return this;
	}
	@Override
	public int getIntrinsicHeight() {
		return mH>0?mH:super.getIntrinsicHeight();
	}
	@Override
	public int getMinimumHeight() {
		return mH>0?mH:super.getMinimumHeight();
	}
	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(mRectF,mPaint);
	}
	// 缩进线
	public static class PaddingLine extends Lines {
		public int mPaddingColor = 0;
		public int mPaddingLeft = 0;
		public int mPaddingRight = 0;
		// 设置左右缩进及缩进区域的颜色
		public PaddingLine withPadding(int color,int l,int r) {
			mPaddingColor = color;
			mPaddingLeft = l;
			mPaddingRight = r;
			return this;
		}
		@Override
		public void draw(Canvas canvas) {
			Rect bounds = getBounds();
			if ((mPaddingColor&0xff000000)!=0) {
				mPaint.setColor(mPaddingColor);
				if (mPaddingLeft>0) {
					mRectF.set(bounds.left,bounds.top,bounds.left+mPaddingLeft,bounds.bottom);
					canvas.drawRect(mRectF,mPaint);
				}
				if (mPaddingRight>0) {
					mRectF.set(bounds.right-mPaddingRight,bounds.top,bounds.right,bounds.bottom);
					canvas.drawRect(mRectF,mPaint);
				}
			}
			mPaint.setColor(mColor);
			mRectF.set(bounds.left + mPaddingLeft, bounds.top, bounds.right - mPaddingRight, bounds.bottom);
			canvas.drawRect(mRectF,mPaint);
		}
	}
	// 上下线 支持左右缩进
	public static class UpDnLine extends PaddingLine {
		public int mSpaceColor = 0;
		public int mTop = 0;
		public int mBottom = 0;
		// 设置上下线的高度及中间部分颜色
		public UpDnLine withUpDn(int color,int t,int b) {
			mSpaceColor = color;
			mTop = t;
			mBottom = b;
			return this;
		}
		@Override
		public void draw(Canvas canvas) {
			Rect bounds = getBounds();
			// draw padding
			if ((mPaddingColor&0xff000000)!=0) {
				mPaint.setColor(mPaddingColor);
				if (mPaddingLeft>0) {
					mRectF.set(bounds.left,bounds.top,bounds.left+mPaddingLeft,bounds.bottom);
					canvas.drawRect(mRectF,mPaint);
				}
				if (mPaddingRight>0) {
					mRectF.set(bounds.right-mPaddingRight,bounds.top,bounds.right,bounds.bottom);
					canvas.drawRect(mRectF,mPaint);
				}
			}
			mRectF.left = bounds.left+mPaddingLeft;
			mRectF.right = bounds.right-mPaddingRight;
			if ((mColor&0xff000000)!=0) {
				mPaint.setColor(mColor);
				if (mTop>0) {
					mRectF.top = bounds.top;
					mRectF.bottom = bounds.top+mTop;
					canvas.drawRect(mRectF,mPaint);
				}
				if (mBottom>0) {
					mRectF.bottom = bounds.bottom-mBottom;
					mRectF.bottom = bounds.bottom;
					canvas.drawRect(mRectF,mPaint);
				}
			}
			mRectF.top = bounds.top+mTop;
			mRectF.bottom = bounds.bottom-mBottom;
			mPaint.setColor(mSpaceColor);
			canvas.drawRect(mRectF,mPaint);
		}
	}

	//支持第线有第二种颜色
	public static class SecondColorLine extends PaddingLine{
		private int mSecondColor,mSecondWidth,mSecondHeight;
		//sh是短线的高度。sw是长线的长度
		public SecondColorLine withSecond(int sc, int sw, int sh){
			mSecondColor = sc;
			mSecondWidth = sw;
			mSecondHeight = sh;
			return this;
		}

		@Override
		public void draw(Canvas canvas) {
			//画第二条线
			Rect bounds = getBounds();
			mPaint.setColor(mColor);
			mRectF.set(bounds.left + mPaddingLeft + mSecondWidth, bounds.bottom - mSecondHeight, bounds.right, bounds.bottom);
			canvas.drawRect(mRectF,mPaint);

			mPaint.setColor(mSecondColor);
			mRectF.set(bounds.left + mPaddingLeft, bounds.top, bounds.left + mPaddingLeft + mSecondWidth, bounds.bottom);
			canvas.drawRect(mRectF,mPaint);
		}
	}
}
