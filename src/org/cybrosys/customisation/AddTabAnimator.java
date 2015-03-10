package org.cybrosys.customisation;

import java.util.ArrayList;

import android.view.View;
import android.widget.ImageView;

public class AddTabAnimator extends ClockGenerator {

	View CURRENT_TAB;
	FrameView MAIN_VIEW_INSTANCE;
	private int RIGHT_MOVE_INDEX;
	private ArrayList<TemporaryData> VIEW_POSITION_MAP_FRAME_LAYOUT;

	public interface AddTabAnimComplete {
		public void onFinish();
	}

	AddTabAnimComplete tabAnimComplete;

	public AddTabAnimator(long TotalTime, long Period, long Displacement,
			View v, FrameView fr,
			ArrayList<TemporaryData> vIEW_POSITION_MAP_FRAME_LAYOUT) {
		super(TotalTime, Period, Displacement);
		this.CURRENT_TAB = v;
		this.MAIN_VIEW_INSTANCE = fr;
		this.RIGHT_MOVE_INDEX = 1;
		this.VIEW_POSITION_MAP_FRAME_LAYOUT = vIEW_POSITION_MAP_FRAME_LAYOUT;
	}

	@Override
	public void onTick(float DISPLACEMENT_ON_SINGLE_TICK) {

		if (!super.RUNNING_FLAG)
			return;

		try {
			if (VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 2).VIEW.getX()
					+ VIEW_POSITION_MAP_FRAME_LAYOUT
							.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 2).VIEW
							.getWidth() * .93 <= VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW
					.getX())
				return;

			if (VIEW_POSITION_MAP_FRAME_LAYOUT.get(RIGHT_MOVE_INDEX - 1).VIEW
					.getX() + 8 >= (VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(RIGHT_MOVE_INDEX).VIEW.getX())) {
				if (RIGHT_MOVE_INDEX >= 4) {
					if (VIEW_POSITION_MAP_FRAME_LAYOUT
							.get(RIGHT_MOVE_INDEX - 1).VIEW.getX() >= VIEW_POSITION_MAP_FRAME_LAYOUT
							.get(RIGHT_MOVE_INDEX).VIEW.getX()) {

						VIEW_POSITION_MAP_FRAME_LAYOUT.get(RIGHT_MOVE_INDEX).VIEW
								.setX(VIEW_POSITION_MAP_FRAME_LAYOUT
										.get(RIGHT_MOVE_INDEX - 1).VIEW.getX());
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
					.size() - 2; i++) {
				View top = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i + 1).VIEW;
				View bottom = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;
				if (bottom.getX() + bottom.getWidth() * .95 <= top.getX()) {

					top.setX(top.getX() - DISPLACEMENT_ON_SINGLE_TICK);
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onFinish(float DISPLACEMENT_ON_SINGLE_TICK) {

		if (tabAnimComplete != null)
			tabAnimComplete.onFinish();
	}
}
