package org.cybrosys.customisation;

import java.util.ArrayList;

import android.view.View;
import android.widget.ImageView;

public class MockMoveGenerator extends ClockGenerator {

	public FrameView MAIN_VIEW_INSTANCE;
	public int LEFT_SIDE_LOCK;
	@SuppressWarnings("unused")
	private boolean FINGER_UP_FLAG = false;
	private View CURRENT_VIEW;
	public TemporaryData[] VIEW_POSIION_MAP;
	public int TOP_POSITION;
	private MoveController APPLICATION_INSTANCE;
	public boolean FINAL_TRANSLATION_FLAG = false;
	public TabView LONG_PRESSED_VIEW;
	public float START_POSITION;
	private boolean DIRECTION_FLAG;
	public int RIGHT_MOVE_INDEX;

	MoveFinishCallback finishCallback;

	public void setFinishCalback(MoveFinishCallback callback) {
		this.finishCallback = callback;
	}

	public MockMoveGenerator(long TotalTime, long Period, long Displacement,
			FrameView frameView, TabView tabView, TemporaryData[] t,
			int top_position, MoveController moveController, boolean Dflag) {
		super(TotalTime, Period, Displacement);
		this.MAIN_VIEW_INSTANCE = frameView;
		this.LEFT_SIDE_LOCK = getChildCountWithout();
		this.CURRENT_VIEW = tabView;
		this.VIEW_POSIION_MAP = t;
		this.TOP_POSITION = top_position;
		this.APPLICATION_INSTANCE = moveController;
		this.DIRECTION_FLAG = Dflag;
		this.RIGHT_MOVE_INDEX = 1;

	}

	public void setViewPositionMap(TemporaryData[] t) {
		this.VIEW_POSIION_MAP = t;
		for (int i = 0; i < VIEW_POSIION_MAP.length; i++)
			for (int j = 0; j < VIEW_POSIION_MAP.length; j++) {
				if (VIEW_POSIION_MAP[i].POSITION < VIEW_POSIION_MAP[j].POSITION) {
					TemporaryData temp = VIEW_POSIION_MAP[i];
					VIEW_POSIION_MAP[i] = VIEW_POSIION_MAP[j];
					VIEW_POSIION_MAP[j] = temp;
				}
			}
	}

	public boolean isRunning() {
		return super.IS_RUNNING;
	}

	/**
	 * to push from stacked to right
	 * 
	 * @param displacement
	 */
	private synchronized void PushTabs(float displacement) {
		if (displacement > 0 && !DIRECTION_FLAG) {
			if (finishCallback != null)
				finishCallback.onChangeVal(0);
			if (VIEW_POSIION_MAP[LEFT_SIDE_LOCK - 1].VIEW.getX() <= VIEW_POSIION_MAP[LEFT_SIDE_LOCK - 2].VIEW
					.getX() + 8) {
				if (MAIN_VIEW_INSTANCE.getChildCount() - 1 >= LEFT_SIDE_LOCK + 2) {
					if ((VIEW_POSIION_MAP[LEFT_SIDE_LOCK - 1].VIEW.getX() <= VIEW_POSIION_MAP[LEFT_SIDE_LOCK - 2].VIEW
							.getX())) {
						LEFT_SIDE_LOCK--;
					}
				} else

					LEFT_SIDE_LOCK--;
			}
			View v = VIEW_POSIION_MAP[TOP_POSITION].VIEW;
			v.setX(v.getX() + displacement);
			for (int i = TOP_POSITION; i < LEFT_SIDE_LOCK - 2; i++) {
				View top = VIEW_POSIION_MAP[i + 1].VIEW;
				View bottom = VIEW_POSIION_MAP[i].VIEW;
				if (top == CURRENT_VIEW)
					top = VIEW_POSIION_MAP[i + 2].VIEW;
				if (bottom == CURRENT_VIEW)
					bottom = VIEW_POSIION_MAP[i - 1].VIEW;
				if (bottom.getX() + bottom.getWidth() * .9 >= top.getX())
					top.setX(top.getX() + displacement);
			}
		} else if (displacement > 0 && DIRECTION_FLAG) {
			if (finishCallback != null)
				finishCallback.onChangeVal(0);
			if (VIEW_POSIION_MAP[RIGHT_MOVE_INDEX - 1].VIEW.getX() + 8 >= VIEW_POSIION_MAP[RIGHT_MOVE_INDEX].VIEW
					.getX()) {
				if (RIGHT_MOVE_INDEX >= 3) {
					if (VIEW_POSIION_MAP[RIGHT_MOVE_INDEX - 1].VIEW.getX() >= VIEW_POSIION_MAP[RIGHT_MOVE_INDEX].VIEW
							.getX()) {
						VIEW_POSIION_MAP[RIGHT_MOVE_INDEX - 1].VIEW
								.setX(MAIN_VIEW_INSTANCE.getChildAt(
										RIGHT_MOVE_INDEX).getX());
						RIGHT_MOVE_INDEX++;
					}
				} else
					RIGHT_MOVE_INDEX++;
			}
			View v = VIEW_POSIION_MAP[TOP_POSITION].VIEW;
			v.setX(v.getX() - displacement);
			for (int i = TOP_POSITION; i > RIGHT_MOVE_INDEX; i--) {
				View current = VIEW_POSIION_MAP[i].VIEW;
				View prev = VIEW_POSIION_MAP[i - 1].VIEW;
				if (current == CURRENT_VIEW)
					current = VIEW_POSIION_MAP[i + 1].VIEW;
				if (prev == CURRENT_VIEW)
					prev = VIEW_POSIION_MAP[i - 2].VIEW;
				if (current.getX() <= prev.getX() + prev.getWidth() * .9)
					prev.setX(prev.getX() - displacement);
			}

		}

	}

	public void fingerUpAction() {
		this.FINGER_UP_FLAG = true;
		if (super.IS_RUNNING != false)
			super.endTranslationCallBack();
	}

	@Override
	public void onTick(float DISPLACEMENT_ON_SINGLE_TICK) {
		PushTabs(DISPLACEMENT_ON_SINGLE_TICK);
	}

	@Override
	public void onFinish(float DISPLACEMENT_ON_SINGLE_TICK) {
		super.IS_RUNNING = false;
		APPLICATION_INSTANCE.TRANSFROMATION_COMPLETE = true;
		if (FINAL_TRANSLATION_FLAG)
			;
		if (finishCallback != null)
			finishCallback.onFinish();
	}

	public int getChildCountWithout() {
		ArrayList<View> dat = new ArrayList<View>();
		for (int i = 0; i < MAIN_VIEW_INSTANCE.getChildCount(); i++) {
			if (!(MAIN_VIEW_INSTANCE.getChildAt(i) instanceof ImageView))
				dat.add(MAIN_VIEW_INSTANCE.getChildAt(i));
		}
		return dat.size();
	}
}
