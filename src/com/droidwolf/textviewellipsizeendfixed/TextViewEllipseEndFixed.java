package com.droidwolf.textviewellipsizeendfixed;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * fixed bug:TextView  multiline & ellipsize="end" 中文不显示"..."
 * 
 * @author droidwolf (droidwolf2010@gmail.com)
 * 
 */
public class TextViewEllipseEndFixed extends TextView {
	private static final String ELLIPSE_END = "…";
	private int mELLIPSEWidth;
	private int mMaxLines;
	private CharSequence mOriText;
	private boolean mSingleLine;
	private static final boolean HAS_BUG = Integer
			.parseInt(android.os.Build.VERSION.SDK) < 14; // <4.0
	private boolean mChecked = !HAS_BUG;

	public TextViewEllipseEndFixed(Context context) {
		super(context);
	}
	public TextViewEllipseEndFixed(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextViewEllipseEndFixed(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setSingleLine(boolean singleLine) {
		mSingleLine = singleLine;
		super.setSingleLine(singleLine);
	}

	public void setMaxLines(int maxlines) {
		mMaxLines = maxlines;
		mChecked = maxlines > 1 ? !HAS_BUG : true;
		super.setMaxLines(maxlines);
	}

	public void setLineSpacing(float add, float mult) {
		mChecked = !HAS_BUG;
		super.setLineSpacing(add, mult);
	}

	protected void onTextChanged(CharSequence text, int start, int before,
			int after) {

		mChecked = !HAS_BUG;
		super.onTextChanged(text, start, before, after);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mChecked = !HAS_BUG;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public void setPadding(int left, int top, int right, int bottom) {
		mChecked = false;
		super.setPadding(left, top, right, bottom);
	}

	public void setEllipsize(TruncateAt where) {
		if (HAS_BUG && where.equals(TruncateAt.END)) {
			mChecked = !HAS_BUG;
			if (mSingleLine) {
				setSingleLine(false);
			}
		} else {
			super.setEllipsize(where);
			mChecked = true;
		}
	}

	public CharSequence getText() {
		if (mOriText != null) {
			return mOriText;
		}
		return super.getText();
	}

	protected void onDraw(Canvas canvas) {
		if (HAS_BUG && !mChecked) {
			mChecked = true;
			Layout layout = super.getLayout();
			int maxLinex = mMaxLines > 0 ? mMaxLines : 1;
			if (layout.getLineCount() > maxLinex) {
				if (mELLIPSEWidth == 0) {
					mELLIPSEWidth = (int) getPaint().measureText(ELLIPSE_END);
				}
				mOriText = super.getText();
				int width = layout.getWidth(), llw = (int) layout.getLineWidth(maxLinex-1), lci = layout.getLineEnd(maxLinex-1);
				if (mELLIPSEWidth + llw > width) {
					int spc = mELLIPSEWidth + llw - width;
					int w = (int) (spc / super.getTextSize());
					if (spc % super.getTextSize() != 0) {
						++w;
					}
					lci -= w;
					
//					Log.d("TextViewEllipseEndFixed", "line"+maxLinex+"="+ mOriText.subSequence(layout.getLineStart(maxLinex-1), lci));
				}
				super.setText(mOriText.subSequence(0, lci) + ELLIPSE_END);
				
			}
		}
		super.onDraw(canvas);
	}
}// end class TextViewEllipseEndFixed
