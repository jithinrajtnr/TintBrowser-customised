package org.cybrosys.customisation;

import org.tint.R;
import org.tint.utils.Constants;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AddTab extends ImageView implements OnGestureListener,
		OnDoubleTapListener, OnClickListener {

	MoveController controller;

	public AddTab(Context context) {
		super(context);
		this.setImageResource(R.drawable.add_tab_list);
		this.setOnClickListener(this);
		controller = (MoveController) getContext().getApplicationContext();

	}

	@Override
	public boolean onDown(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2,
			float velocityX, float velocityY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent event) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent event) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		return true;
	}

	@Override
	public void onClick(View arg0) {
		controller.uiManager.addTab(
				true,
				PreferenceManager
						.getDefaultSharedPreferences(this.getContext())
						.getBoolean(Constants.PREFERENCE_INCOGNITO_BY_DEFAULT,
								false));

	}
}
