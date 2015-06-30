package com.mob.root.view;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public class TowRotateAnimation {
	private View mContainer;
	private View mFrontView;
	private View mBackView;

	public void clickFrontViewAnimation(View container, View frontView,
			View backView) {
		if (container == null || frontView == null || backView == null) {
			return;
		}
		this.mContainer = container;
		this.mFrontView = frontView;
		this.mBackView = backView;
		applyRotation(0, 0, 90);

	}

	public void clickBackViewAnimation(View container, View frontView,
			View backView) {
		if (container == null || frontView == null || backView == null) {
			return;
		}
		this.mContainer = container;
		this.mFrontView = frontView;
		this.mBackView = backView;
		applyRotation(1, 180, 90);

	}

	private void applyRotation(int position, float start, float end) {
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;

		final RotateAnimation rotation = new RotateAnimation(start, end, centerX, centerY, 310.0f, true);
		rotation.setDuration(100);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(position));
		mContainer.startAnimation(rotation);
	}

	private final class DisplayNextView implements Animation.AnimationListener {
		private final int mPosition;

		private DisplayNextView(int position) {
			mPosition = position;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mContainer.post(new SwapViews(mPosition));
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	private final class SwapViews implements Runnable {
		private final int mPosition;

		public SwapViews(int position) {
			mPosition = position;
		}

		public void run() {
			final float centerX = mContainer.getWidth() / 2.0f;
			final float centerY = mContainer.getHeight() / 2.0f;
			RotateAnimation rotation;

			if (mPosition == 0) {
				mFrontView.setVisibility(View.GONE);
				mBackView.setVisibility(View.VISIBLE);
				rotation = new RotateAnimation(90, 180, centerX, centerY, 310.0f, false);
			} else {
				mBackView.setVisibility(View.GONE);
				mFrontView.setVisibility(View.VISIBLE);
				rotation = new RotateAnimation(90, 0, centerX, centerY, 310.0f, false);
			}

			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());

			mContainer.startAnimation(rotation);
		}
	}

}
