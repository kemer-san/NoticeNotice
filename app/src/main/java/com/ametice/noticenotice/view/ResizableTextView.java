package com.ametice.noticenotice.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * フォントサイズ自動調整TextView
 */
public class ResizableTextView extends TextView
{
	/**
	 * コンストラクタ
	 * @param context
	 */
	public ResizableTextView(Context context)
	{
		super(context);
	}

	/**
	 * コンストラクタ
	 * @param context
	 * @param attrs
	 */
	public ResizableTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	/**
	 * 子Viewの位置を決める
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		resize();
	}

	/**
	 * テキストサイズ調整
	 */
	private void resize()
	{
		/** 最小のテキストサイズ */
		final float MIN_TEXT_SIZE = 10f;

		int viewHeight = this.getHeight();	// Viewの縦幅
		int viewWidth = this.getWidth();	// Viewの横幅

		// テキストサイズ
		float textSize = getTextSize();

		// Paintにテキストサイズ設定
		Paint paint = new Paint();
		paint.setTextSize(textSize);

		// テキストの縦幅取得
		FontMetrics fm = paint.getFontMetrics();
		float textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));

		// テキストの横幅取得
		float textWidth = paint.measureText(this.getText().toString());

		// 縦幅と、横幅が収まるまでループ
		while (viewHeight < textHeight | viewWidth < textWidth)
		{
			// 調整しているテキストサイズが、定義している最小サイズ以下か。
			if (MIN_TEXT_SIZE >= textSize)
			{
				// 最小サイズ以下になる場合は最小サイズ
				textSize = MIN_TEXT_SIZE;
				break;
			}

			// テキストサイズをデクリメント
			textSize--;

			// Paintにテキストサイズ設定
			paint.setTextSize(textSize);

			// テキストの縦幅を再取得
			fm = paint.getFontMetrics();
			textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));

			// テキストの横幅を再取得
			textWidth = paint.measureText(this.getText().toString());
		}

		// テキストサイズ設定
		setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
	}
}