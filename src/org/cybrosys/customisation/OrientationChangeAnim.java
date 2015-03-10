package org.cybrosys.customisation;

import java.util.ArrayList;

import android.view.View;
import android.widget.ImageView;

public class OrientationChangeAnim extends ClockGenerator {

	public onStateChange statechangeListner;

	public interface onStateChange {
		public void onFinish(float pos);
	}

	ArrayList<TemporaryData> VIEW_POSITION_MAP_FRAME_LAYOUT;
	private TabView END_VIEW;
	private int MOVE;
	private int MAX_WIDTH_LIMIT;
	private View START_VIEW;
	private int RIGHT_MOVE_INDEX = 1;
	@SuppressWarnings("unused")
	private boolean startFlag = false;
	private float INIT_VIEW_START = 0;
	private boolean FIRST_VIEW_CHANGED = false;

	public OrientationChangeAnim(long TotalTime, long Period,
			long Displacement,
			ArrayList<TemporaryData> vIEW_POSITION_MAP_FRAME_LAYOUT, int move,
			int mAX_WIDTH_LIMIT) {
		super(TotalTime, Period, Displacement);
		VIEW_POSITION_MAP_FRAME_LAYOUT = vIEW_POSITION_MAP_FRAME_LAYOUT;
		END_VIEW = (TabView) VIEW_POSITION_MAP_FRAME_LAYOUT
				.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW;
		this.MOVE = move;
		this.MAX_WIDTH_LIMIT = mAX_WIDTH_LIMIT;
		this.START_VIEW = getStartView();
		INIT_VIEW_START = vIEW_POSITION_MAP_FRAME_LAYOUT.get(0).VIEW.getX();
		FIRST_VIEW_CHANGED = false;
	}

	private View getStartView() {

		return VIEW_POSITION_MAP_FRAME_LAYOUT.get(0).VIEW;
	}

	@Override
	public void onTick(float DISPLACEMENT_ON_SINGLE_TICK) {
		if (!super.RUNNING_FLAG)
			return;

		try {
			if (MOVE == 0) {
				if (FIRST_VIEW_CHANGED)
					return;
				if ((VIEW_POSITION_MAP_FRAME_LAYOUT.get(0).VIEW.getX() + VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(0).VIEW.getWidth() * .96) < VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(1).VIEW.getX())
					return;

				if (INIT_VIEW_START < VIEW_POSITION_MAP_FRAME_LAYOUT.get(0).VIEW
						.getX()) {
					FIRST_VIEW_CHANGED = true;
					VIEW_POSITION_MAP_FRAME_LAYOUT.get(0).VIEW
							.setX(INIT_VIEW_START);

					return;
				}

				END_VIEW.setX(END_VIEW.getX() + DISPLACEMENT_ON_SINGLE_TICK);
				for (int i = getIndexOfFrame(END_VIEW); i > 0; i--) {
					View current = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;
					View prev = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i - 1).VIEW;
					if (current.getX() >= prev.getX() + prev.getWidth() * .93) {
						if (prev == START_VIEW) {
							startFlag = true;
						}
						prev.setX(prev.getX() + DISPLACEMENT_ON_SINGLE_TICK);
					}
				}
			} else if (MOVE == 1) {
				if (END_VIEW.getX() + END_VIEW.getWidth() <= MAX_WIDTH_LIMIT)
					return;
				if (VIEW_POSITION_MAP_FRAME_LAYOUT.get(RIGHT_MOVE_INDEX - 1).VIEW
						.getX() + 8 >= VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(RIGHT_MOVE_INDEX).VIEW.getX()) {

					if (RIGHT_MOVE_INDEX >= 4) {
						if (VIEW_POSITION_MAP_FRAME_LAYOUT
								.get(RIGHT_MOVE_INDEX - 1).VIEW.getX() >= VIEW_POSITION_MAP_FRAME_LAYOUT
								.get(RIGHT_MOVE_INDEX).VIEW.getX()) {
							VIEW_POSITION_MAP_FRAME_LAYOUT
									.get(RIGHT_MOVE_INDEX).VIEW
									.setX(VIEW_POSITION_MAP_FRAME_LAYOUT
											.get(RIGHT_MOVE_INDEX - 1).VIEW
											.getX());
							RIGHT_MOVE_INDEX++;
						}
					} else {
						VIEW_POSITION_MAP_FRAME_LAYOUT.get(RIGHT_MOVE_INDEX).VIEW
								.setX(VIEW_POSITION_MAP_FRAME_LAYOUT
										.get(RIGHT_MOVE_INDEX - 1).VIEW.getX() + 8);
						RIGHT_MOVE_INDEX++;
					}
				}
				View v = VIEW_POSITION_MAP_FRAME_LAYOUT.get(RIGHT_MOVE_INDEX).VIEW;
				if (v instanceof ImageView)
					return;
				v.setX(v.getX() - DISPLACEMENT_ON_SINGLE_TICK);
				for (int i = RIGHT_MOVE_INDEX; i < VIEW_POSITION_MAP_FRAME_LAYOUT
						.size() - 1; i++) {
					View top = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i + 1).VIEW;
					View bottom = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;
					if (bottom.getX() + bottom.getWidth() * .95 <= top.getX()) {
						top.setX(top.getX() - DISPLACEMENT_ON_SINGLE_TICK);
					}
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onFinish(float DISPLACEMENT_ON_SINGLE_TICK) {
		if (statechangeListner != null)
			statechangeListner.onFinish(END_VIEW.getX() + END_VIEW.getWidth());

	}

	private int getIndexOfFrame(TabView tabView) {
		for (TemporaryData data : VIEW_POSITION_MAP_FRAME_LAYOUT)
			if (data.VIEW == tabView)
				return VIEW_POSITION_MAP_FRAME_LAYOUT.indexOf(data);
		return -1;
	}

}
