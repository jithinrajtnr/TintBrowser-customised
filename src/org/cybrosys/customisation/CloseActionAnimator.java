/*
 * Tint Browser for Android
 * 
 * Copyright (C) 2012 - to infinity and beyond J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.cybrosys.customisation;

import java.util.ArrayList;

import android.view.View;
import android.widget.ImageView;

public class CloseActionAnimator extends ClockGenerator {

	private FrameView MAINVIEW_INSTANCE;
	private TabView NEXT_TAB;
	public RenderingCompleteCallback callback;
	private TabView PREV_TAB;
	public View ORG_TAB;
	private ArrayList<TemporaryData> VIEW_POSITION_MAP_FRAME_LAYOUT;

	/**
	 * 
	 * @param TotalTime
	 * @param Period
	 * @param Displacement
	 * @param tabView
	 * @param frameView
	 * @param dir
	 */
	public CloseActionAnimator(long TotalTime, long Period, long Displacement,
			TabView tabView, FrameView frameView, int dir,
			ArrayList<TemporaryData> vIEW_POSITION_MAP_FRAME_LAYOUT) {
		super(TotalTime, Period, Displacement);
		this.MAINVIEW_INSTANCE = frameView;
		this.VIEW_POSITION_MAP_FRAME_LAYOUT = vIEW_POSITION_MAP_FRAME_LAYOUT;
		this.NEXT_TAB = (TabView) VIEW_POSITION_MAP_FRAME_LAYOUT
				.get(getIndexOfFrame(tabView) + 1).VIEW;// MAINVIEW_INSTANCE.getChildAt(MAINVIEW_INSTANCE.indexOfChild(tabView)
														// + 1);
		if (VIEW_POSITION_MAP_FRAME_LAYOUT.size() > 0
				&& VIEW_POSITION_MAP_FRAME_LAYOUT.get(0).VIEW != tabView)
			this.PREV_TAB = (TabView) VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(getIndexOfFrame(tabView) - 1).VIEW;
	}

	float disp = 0;

	protected void moveTab(float displacement) {
		if (!super.IS_RUNNING)
			return;
		disp = disp + displacement;
		try {
			if (!super.RUNNING_FLAG)
				return;
			if (PREV_TAB != null)
				if (PREV_TAB.getX() > NEXT_TAB.getX() - displacement)
					return;
			NEXT_TAB.setX(NEXT_TAB.getX() - displacement);
			for (int i = getIndexOfFrame(NEXT_TAB); i < getChildCountWithout() - 1; i++) {
				View currentView = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;// MAINVIEW_INSTANCE.getChildAt(i);
				View nextView = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i + 1).VIEW;// MAINVIEW_INSTANCE.getChildAt(i+1);
				if ((currentView.getX() + currentView.getWidth() * .9) <= nextView
						.getX()) {

					nextView.setX(nextView.getX() - displacement);
				}
			}
			ORG_TAB = NEXT_TAB;

		} catch (Exception e) {
		}
	}

	@Override
	public void onTick(float DISPLACEMENT_ON_SINGLE_TICK) {
		if (super.IS_RUNNING == true)
			moveTab(DISPLACEMENT_ON_SINGLE_TICK);
	}

	@Override
	public void onFinish(float DISPLACEMENT_ON_SINGLE_TICK) {
		if (callback != null)
			callback.onFinished(4545);
	}

	public void setCallbackListner(RenderingCompleteCallback callback) {
		this.callback = callback;
	}

	private int getChildCountWithout() {
		ArrayList<View> dat = new ArrayList<View>();
		for (int i = 0; i < MAINVIEW_INSTANCE.getChildCount(); i++) {
			if (!(MAINVIEW_INSTANCE.getChildAt(i) instanceof ImageView))
				dat.add(MAINVIEW_INSTANCE.getChildAt(i));
		}
		return dat.size();
	}

	public long getRemainingWidth(TabView tabView, int direction) {
		switch (direction) {
		case org.cybrosys.customisation.Constants.LEFT_LOCK_MOVE:
			int left_childs = MAINVIEW_INSTANCE.indexOfChild(tabView);
			View leftView = MAINVIEW_INSTANCE.getChildAt(MAINVIEW_INSTANCE
					.indexOfChild(tabView) - 1);
			long totalwidth = (long) ((leftView.getWidth() * .93) * left_childs);
			long current = (long) Math.abs(MAINVIEW_INSTANCE.getChildAt(0)
					.getX() - (leftView.getX() + leftView.getWidth() * .93));
			return Math.abs(totalwidth - current);

		}
		return 0;
	}

	private int getIndexOfFrame(TabView tabView) {
		for (TemporaryData data : VIEW_POSITION_MAP_FRAME_LAYOUT)
			if (data.VIEW == tabView)
				return VIEW_POSITION_MAP_FRAME_LAYOUT.indexOf(data);
		return -1;
	}
}
