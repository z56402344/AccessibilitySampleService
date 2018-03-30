package z.draw;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

// 基础类 空实现
public class BaseDrawable extends Drawable implements Drawable.Callback {
	protected Paint mPaint = new Paint();
	protected RectF mRectF = new RectF();
	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mRectF.set(bounds);
	}
	@Override
	public void draw(Canvas canvas) {
	}
	@Override
	public void setAlpha(int i) {
		mPaint.setAlpha(i);
	}
	@Override
	public void setColorFilter(ColorFilter colorFilter) {
		mPaint.setColorFilter(colorFilter);
	}
	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void invalidateDrawable(Drawable who) {
		super.invalidateSelf();
	}

	@Override
	public void scheduleDrawable(Drawable who, Runnable what, long when) {
		super.scheduleSelf(what, when);
	}

	@Override
	public void unscheduleDrawable(Drawable who, Runnable what) {
		super.unscheduleSelf(what);
	}
}
