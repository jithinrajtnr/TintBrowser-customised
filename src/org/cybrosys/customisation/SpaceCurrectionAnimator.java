package org.cybrosys.customisation;

import java.util.ArrayList;

import android.view.View;

public class SpaceCurrectionAnimator extends ClockGenerator {

	private TabView END_VIEW;
	public RenderingCompleteCallback callback;
	private ArrayList<TemporaryData> VIEW_POSITION_MAP_FRAME_LAYOUT;
	private int MAX_WIDTH_LIMIT;
	@SuppressWarnings("unused")
	private TabView END_PREV_VIEW;

	/**
	 * 
	 * @param TotalTime
	 * @param Period
	 * @param Displacement
	 * @param mAX_WIDTH_LIMIT
	 * @param currentView
	 * @param mapDatas
	 * @param direction
	 *            0 <- left , 1 -> right
	 */
	public SpaceCurrectionAnimator(long TotalTime, long Period,
			long Displacement, FrameView fr,
			ArrayList<TemporaryData> vIEW_POSITION_MAP_FRAME_LAYOUT,
			int mAX_WIDTH_LIMIT) {
		super(TotalTime, Period, Displacement);
		this.VIEW_POSITION_MAP_FRAME_LAYOUT = vIEW_POSITION_MAP_FRAME_LAYOUT;
		END_VIEW = (TabView) VIEW_POSITION_MAP_FRAME_LAYOUT
				.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW;
		this.MAX_WIDTH_LIMIT = mAX_WIDTH_LIMIT;
	}

	@Override
	public void onTick(float DISPLACEMENT_ON_SINGLE_TICK) {
		try {
			if (!super.RUNNING_FLAG)
				return;
			if (END_VIEW.getX() + END_VIEW.getWidth() > MAX_WIDTH_LIMIT)
				return;
			if ((VIEW_POSITION_MAP_FRAME_LAYOUT.get(0).VIEW.getX() + VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(0).VIEW.getWidth() * .92) < VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(1).VIEW.getX())
				return;

			END_VIEW.setX(END_VIEW.getX() + DISPLACEMENT_ON_SINGLE_TICK);
			for (int i = getIndexOfFrame(END_VIEW); i > 0; i--) {
				View current = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;
				View prev = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i - 1).VIEW;
				if (current.getX() >= prev.getX() + prev.getWidth() * .93)
					prev.setX(prev.getX() + DISPLACEMENT_ON_SINGLE_TICK);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onFinish(float DISPLACEMENT_ON_SINGLE_TICK) {
		if (callback != null)
			callback.onFinished(END_VIEW.getX() + END_VIEW.getWidth());
	}

	public void setCallbackListner(RenderingCompleteCallback callback) {
		this.callback = callback;
	}

	private int getIndexOfFrame(TabView tabView) {
		for (TemporaryData data : VIEW_POSITION_MAP_FRAME_LAYOUT)
			if (data.VIEW == tabView)
				return VIEW_POSITION_MAP_FRAME_LAYOUT.indexOf(data);
		return -1;
	}

}
