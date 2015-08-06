package com.androidhelper.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class FlavorGridView extends GridView {

	public FlavorGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlavorGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlavorGridView(Context context) {
		super(context);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			return true; // 禁止GridView滑动
		}
		 return super.dispatchTouchEvent(event);
	}
}
