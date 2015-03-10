package org.cybrosys.customisation;

import java.util.ArrayList;

import org.tint.R;
import org.tint.ui.managers.TabletUIManager;
import org.tint.ui.managers.UIManager;
import org.tint.utils.Constants;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

@SuppressWarnings("unused")
public class MoveController extends Application implements
		RenderingCompleteCallback {

	ArrayList<TabChara> TABS = new ArrayList<TabChara>();
	public FrameView MAIN_VIEW_INSTANCE;
	private int PREV_X = 0;

	private int INDEX_TOP_MOVABLE_CHILD;
	private int LEFT_CHILD_COUNT = 0;
	private int DELTA_X = -1;
	private int RIGHT_CHILD_COUNT;
	private int RIGHT_MOVE_INDEX = 1;
	private int SCREEN_DIM_X;
	private int SCREEN_DIM_Y;
	public int MAX_WIDTH_LIMIT;
	private final int LOCKING_LIMIT = 8;
	public int ARRANGED_INDEX = 0;
	private View PREVIOUS_VIEW;
	private final boolean SYNC_VARIABLE = true;
	public TemporaryData[] VIEW_POSITION_MAP;
	public ArrayList<TemporaryData> VIEW_POSITION_MAP_FRAME_LAYOUT;
	public int MOVABLE_CHILD_POSITION;
	public Bitmap VIEW_COPY;
	private ImageView __COPY_;
	private boolean __MOVE_LOCK_ = false;
	private boolean _SCROLL_HANDLER;
	private final boolean END_INDECATOR = false;
	private View MOVE_VIEW;
	private View INSERTION_SLOT;
	private MockMoveGenerator mockMoveGenerator, RIGHT_MOCK_MOVE_GEN;
	private final boolean VIEW_CREATION_FLAG = false;
	private float FINAL_MOVE_POSITION = -1;

	private boolean ANIMATION_LOCK = false;
	private boolean CALLBACK_WAIT = false;
	private float LAST_MOVED_POSITION;
	public boolean TRANSFROMATION_COMPLETE = true;
	private float SAVED_X = -1;
	private TabView TABVIEW_INSTANCE;
	public float MAX_X_TABS;
	public float UP_EVENT_X;
	private boolean STACKED_MOVE = false;
	private int prev_x;
	private int delta_x;
	private float DELTA_TRANSLATION;
	private float PREV_TANSLATION;
	boolean MOTION_EVENT_UP_FLAG = false;
	ImageView copy;
	boolean TrannslationGeneratorFlag = false;
	public UIManager uiManager;

	public TabletUIManager tabletUIManagerinstance;

	private boolean MOVE_TYPE;

	TextView TT;

	private final ArrayList<int[]> viewData = new ArrayList<int[]>();
	private int[] SLOT_CHART;

	public void addTabData(TabView tab) {

	}

	public void init(int child_count) {
		child_count--;
		this.LEFT_CHILD_COUNT = child_count;
		this.RIGHT_CHILD_COUNT = child_count;
		this.LEFT_SIDE_LOCK = child_count;

	}

	@SuppressLint("NewApi")
	public float getTabWidth() {
		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		SCREEN_DIM_X = size.x;
		return (MAIN_VIEW_INSTANCE.getChildCount() > 2) ? (float) ((SCREEN_DIM_X * 0.9) / 3.0)
				: (float) ((SCREEN_DIM_X * 0.9) / 2.0);

	}

	private boolean shiftFlag = false;

	public boolean arrangeFiished = false;
	public synchronized void arrangeItems(boolean OrientationChange, TabView tabView) {

		arrangeFiished = false;
		shiftFlag = false;
		
		for (int i = 1; i < VIEW_POSITION_MAP_FRAME_LAYOUT.size() ; i++) {
			if (ARRANGED_INDEX >= i) {
				continue;
			}
			View next = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;
			View prev = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i - 1).VIEW;
			float next_end_position = (float) (prev.getX() + prev.getWidth() * .93)
					+ next.getWidth();

			if (MAX_WIDTH_LIMIT < next_end_position) {
				next.setX(MAX_WIDTH_LIMIT - next.getWidth());
				shiftFlag = true;

			} else {
				next.setX((float) (prev.getX() + prev.getWidth() * .93));
				ARRANGED_INDEX = getIndexOfFrame((TabView) next);
			}

		}
		if (shiftFlag) {

			View prev = VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(indexOfChildFrame(tabView) - 1).VIEW;
			long displacement = (long) (prev.getWidth() * .93);
			AddTabAnimator tabAnimator = new AddTabAnimator((long) 600,
					(long) 1, (long) displacement, prev, MAIN_VIEW_INSTANCE,
					VIEW_POSITION_MAP_FRAME_LAYOUT);
			tabAnimator.startTranslationCallBack(false);

		}

		arrangeFiished = true;
	}

	public void screenOrientationShift() {
		try {

		} catch (Exception e) {
		}
	}

	public int indexOfChildFrame(View v) {
		for (int i = 0; i < VIEW_POSITION_MAP_FRAME_LAYOUT.size(); i++) {
			if (VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW == v)
				return VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).POSITION;
		}
		return -1;
	}

	private void leftShift() {
		for (int i = MAIN_VIEW_INSTANCE.getChildCount() - 2; i > 0; i--) {
			View tempTopView = MAIN_VIEW_INSTANCE.getChildAt(i);
			View tempBottomView = MAIN_VIEW_INSTANCE.getChildAt(i - 1);
			if (tempBottomView.getX() + 8 >= tempTopView.getX() - 80) {
				tempTopView.setX(tempBottomView.getX() + 8);
			} else
				tempTopView.setX(tempTopView.getX() - 80);

		}
	}

	@SuppressLint("NewApi")
	public void init_dimentions() {
		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		SCREEN_DIM_X = size.x;
		SCREEN_DIM_Y = size.y;
		MAX_WIDTH_LIMIT = (int) (SCREEN_DIM_X * .93);
		AddTab addTab = getAddTabView();
		if (addTab != null) {
			float addtabWidth = addTab.getWidth();
			MAX_WIDTH_LIMIT = (int) (SCREEN_DIM_X - addtabWidth - 10);
		}
	}

	/**
	 * To slide the tabs
	 * 
	 * @param tb
	 * @param event
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onTouchEvent(TabView tb, MotionEvent event) {

		try {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				PREV_X = (int) event.getRawX();

				return;
			}
			if (!(event.getAction() == MotionEvent.ACTION_MOVE))
				return;
			int current_x = (int) event.getRawX();
			DELTA_X = current_x - PREV_X;
			PREV_X = current_x;
			if (DELTA_X >= 30)
				DELTA_X = 30;
			else if (DELTA_X <= -30)
				DELTA_X = -30;
			if (DELTA_X > 0) {

				RIGHT_CHILD_COUNT = MAIN_VIEW_INSTANCE.getChildCount() - 1;
				RIGHT_MOVE_INDEX = 1;
				if ((VIEW_POSITION_MAP_FRAME_LAYOUT.get(0).VIEW.getX() + VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(0).VIEW.getWidth() * .9) <= VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(1).VIEW.getX())
					return;

				if (VIEW_POSITION_MAP_FRAME_LAYOUT.get(LEFT_CHILD_COUNT - 1).VIEW
						.getX() <= VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(LEFT_CHILD_COUNT - 2).VIEW.getX() + 8) {
					if (VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1 >= LEFT_CHILD_COUNT + 2) {
						if ((VIEW_POSITION_MAP_FRAME_LAYOUT
								.get(LEFT_CHILD_COUNT - 1).VIEW.getX() <= VIEW_POSITION_MAP_FRAME_LAYOUT
								.get(LEFT_CHILD_COUNT - 2).VIEW.getX())) {
							VIEW_POSITION_MAP_FRAME_LAYOUT
									.get(LEFT_CHILD_COUNT - 2).VIEW
									.setX(VIEW_POSITION_MAP_FRAME_LAYOUT
											.get(LEFT_CHILD_COUNT - 1).VIEW
											.getX());
							LEFT_CHILD_COUNT--;
						}
					} else {
						VIEW_POSITION_MAP_FRAME_LAYOUT
								.get(LEFT_CHILD_COUNT - 2).VIEW
								.setX(VIEW_POSITION_MAP_FRAME_LAYOUT
										.get(LEFT_CHILD_COUNT - 1).VIEW.getX() - 8);

						LEFT_CHILD_COUNT--;

					}
				}
				View firstView = VIEW_POSITION_MAP_FRAME_LAYOUT.get(0).VIEW;
				View v = VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(LEFT_CHILD_COUNT - 2).VIEW;
				if (v instanceof ImageView)
					return;
				if (v == firstView)
					return;
				v.setX(v.getX() + DELTA_X);
				for (int i = LEFT_CHILD_COUNT - 1; i > 1; i--) {
					View top = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;
					View bottom = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i - 1).VIEW;
					if (top.getX() >= (bottom.getX() + bottom.getWidth() * .95)) {
						if (bottom == firstView)
							return;
						bottom.setX(bottom.getX() + DELTA_X);
					}
				}

			} else if (DELTA_X < 0) {

				LEFT_CHILD_COUNT = MAIN_VIEW_INSTANCE.getChildCount() - 1;
				if (VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW
						.getX() >= VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 2).VIEW
						.getX()
						+ VIEW_POSITION_MAP_FRAME_LAYOUT
								.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 2).VIEW
								.getWidth() * .90) {
					return;
				}

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
				TabView lastView = (TabView) VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW;
				View v = VIEW_POSITION_MAP_FRAME_LAYOUT.get(RIGHT_MOVE_INDEX).VIEW;
				if (v instanceof ImageView)
					return;
				/*
				 * if ( VIEW_POSITION_MAP_FRAME_LAYOUT.get(RIGHT_MOVE_INDEX -
				 * 1).VIEW.getX() > v.getX() + DELTA_X) return;
				 */
				if (v == lastView) {
					return;
					/*
					 * if ( (lastView.getX() + lastView.getWidth() -
					 * Math.abs(DELTA_X)) < MAX_WIDTH_LIMIT ){ Log.e("return",
					 * "return"); return; }
					 */
				}
				v.setX(v.getX() + DELTA_X);
				for (int i = RIGHT_MOVE_INDEX; i < MAIN_VIEW_INSTANCE
						.getChildCount() - 2; i++) {
					View top = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i + 1).VIEW;
					View bottom = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;
					if (bottom.getX() + bottom.getWidth() * .95 <= top.getX()) {
						if (top == lastView)
							return;
						top.setX(top.getX() + DELTA_X);

					}
				}
			}
		} catch (Exception e) {
			// Log.e("Exception  moveee", e.toString());
		}

	}

	public void init_move_stack() {
		LEFT_CHILD_COUNT = MAIN_VIEW_INSTANCE.getChildCount() - 1;
		RIGHT_CHILD_COUNT = MAIN_VIEW_INSTANCE.getChildCount() - 1;
		RIGHT_MOVE_INDEX = 1;
	}

	/**
	 * 
	 * @param event
	 *            Motionevent to handle
	 * @param tabView
	 *            Tab that need to move
	 */

	TabView tcurrentView;
	private boolean blank = false;

	public synchronized void onMoveEvent(MotionEvent event,
			final TabView tabView) {
		tcurrentView = tabView;
		try {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// Log.i("onMoveEvent Controller", "movw");
				createViewHeirarchy();
				blank = false;
				SLOT_CHART = new int[getChildCountWithout()];
				for (int i = 0; i < getChildCountWithout(); i++) {
					if (i == MAIN_VIEW_INSTANCE.indexOfChild(tabView)
							&& !(MAIN_VIEW_INSTANCE.getChildAt(i) instanceof ImageView))
						SLOT_CHART[i] = 1;
					else
						SLOT_CHART[i] = 0;
				}
				init_View_Positions();
				prev_x = (int) event.getRawX();
				delta_x = 0;
				__COPY_ = new ImageView(getApplicationContext());
				__COPY_.setImageBitmap(getBitmapFromView(tabView));
				__COPY_.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						Gravity.NO_GRAVITY));
				__COPY_.setX(event.getRawX());
				__MOVE_LOCK_ = false;
				_SCROLL_HANDLER = false;
				MOVE_VIEW = tabView;
				DELTA_TRANSLATION = 0;
				PREV_TANSLATION = event.getRawX();
				this.TABVIEW_INSTANCE = tabView;
				this.MAIN_VIEW_INSTANCE.addView(__COPY_);
				break;
			case MotionEvent.ACTION_MOVE:
				blank = true;
				DELTA_TRANSLATION = event.getRawX() - PREV_TANSLATION;
				PREV_TANSLATION = event.getRawX();
				__COPY_.setX(__COPY_.getX() + DELTA_TRANSLATION);
				if (__MOVE_LOCK_ == false && TrannslationGeneratorFlag == false) {

					if (TRANSFROMATION_COMPLETE == false
							|| ANIMATION_LOCK == true) {
						SAVED_X = PREV_TANSLATION;
						return;
					}
					if (event.getRawX() >= 0
							&& event.getRawX() <= (SCREEN_DIM_X / 2)) {
						final int stack_pos = checkStaked(event);

						if (stack_pos != -1) {

							MOVE_TYPE = true;
							if (mockMoveGenerator == null) {
								long translation = (long) (VIEW_POSITION_MAP[stack_pos].VIEW
										.getWidth() * 2);
								mockMoveGenerator = new MockMoveGenerator(2000,
										5, translation, MAIN_VIEW_INSTANCE,
										tabView, VIEW_POSITION_MAP, stack_pos,
										this, false);
								mockMoveGenerator
										.setViewPositionMap(VIEW_POSITION_MAP);
								mockMoveGenerator
										.setFinishCalback(new MoveFinishCallback() {

											@Override
											public void onFinish() {
												recreateSlotChart(0);
												changeViewPositionOfStacked(
														tabView, 0);
												recreateViewHeirarchy();
											}

											@Override
											public void onChangeVal(int val) {
												if (RIGHT_MOCK_MOVE_GEN != null)
													RIGHT_MOCK_MOVE_GEN.RIGHT_MOVE_INDEX = 1;
											}
										});

								if (mockMoveGenerator.IS_RUNNING)
									return;
								if (!mockMoveGenerator.IS_RUNNING)
									mockMoveGenerator
											.startTranslationCallBack(false);
								TRANSFROMATION_COMPLETE = false;

							} else {
								if (mockMoveGenerator.IS_RUNNING)
									return;
								mockMoveGenerator.TOP_POSITION = stack_pos;
								mockMoveGenerator
										.setViewPositionMap(VIEW_POSITION_MAP);
								mockMoveGenerator
										.startTranslationCallBack(false);
								TRANSFROMATION_COMPLETE = false;

							}
							return;
						}
					} else if (event.getRawX() >= (SCREEN_DIM_X / 2)
							&& event.getRawX() <= SCREEN_DIM_X) {
						try {
							View rightBottomView = getStackedRightEnd(event);

							if (rightBottomView != null) {
								MOVE_TYPE = true;
								if (RIGHT_MOCK_MOVE_GEN == null) {
									long translation = (long) (rightBottomView
											.getWidth() * 2);
									RIGHT_MOCK_MOVE_GEN = new MockMoveGenerator(
											2000,
											5,
											translation,
											MAIN_VIEW_INSTANCE,
											tabView,
											VIEW_POSITION_MAP,
											getIndexOfViewPositionMap(rightBottomView),
											this, true);
									RIGHT_MOCK_MOVE_GEN
											.setViewPositionMap(VIEW_POSITION_MAP);
									RIGHT_MOCK_MOVE_GEN
											.setFinishCalback(new MoveFinishCallback() {

												@Override
												public void onFinish() {
													recreateSlotChart(1);
													changeViewPositionOfStacked(
															tabView, 1);
													recreateViewHeirarchy();
												}

												@Override
												public void onChangeVal(int val) {
													if (mockMoveGenerator != null)
														mockMoveGenerator.LEFT_SIDE_LOCK = mockMoveGenerator
																.getChildCountWithout();
												}
											});

									if (RIGHT_MOCK_MOVE_GEN.IS_RUNNING)
										return;
									else
										RIGHT_MOCK_MOVE_GEN
												.startTranslationCallBack(false);
									TRANSFROMATION_COMPLETE = false;

								} else {
									if (RIGHT_MOCK_MOVE_GEN.IS_RUNNING)
										return;
									RIGHT_MOCK_MOVE_GEN.TOP_POSITION = getIndexOfViewPositionMap(rightBottomView);
									RIGHT_MOCK_MOVE_GEN
											.setViewPositionMap(VIEW_POSITION_MAP);
									RIGHT_MOCK_MOVE_GEN
											.startTranslationCallBack(false);
									TRANSFROMATION_COMPLETE = false;
								}

								return;
							}
						} catch (Exception e) {
						}
					}

					int start = getUnstackedPositions();
					if (ANIMATION_LOCK == true)
						return;
					for (int i = 0; i < viewData.size(); i++) {

						if (event.getRawX() >= viewData.get(i)[2]
								&& event.getRawX() <= viewData.get(i)[3]) {
							final View moVeView = VIEW_POSITION_MAP[i].VIEW;
							if (moVeView == tabView)
								continue;
							if (moVeView == PREVIOUS_VIEW) {
								continue;
							} else {
								PREVIOUS_VIEW = moVeView;
							}
							int tranlation = moVeView.getWidth();
							final int action = getAction(moVeView);
							try {
								if (action == 0) {
									tranlation = (int) Math
											.abs((VIEW_POSITION_MAP[getIndexOfViewPositionMap(moVeView) + 1].VIEW
													.getX() - moVeView.getX()));
								} else if (action == 1) {
									tranlation = (int) Math
											.abs(VIEW_POSITION_MAP[getIndexOfViewPositionMap(moVeView) - 1].VIEW
													.getX() - moVeView.getX());
								}
								tranlation = (int) Math
										.abs(moVeView.getWidth() * .95);
							} catch (Exception e) {
							}
							final int translation_amount = action == 0 ? tranlation
									: action == 1 ? -tranlation : 0;
							LAST_MOVED_POSITION = moVeView.getX();
							TranslateAnimation tr = new TranslateAnimation(0,
									translation_amount, 0, 0);
							tr.setDuration(500);
							tr.setFillEnabled(true);
							tr.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
									__MOVE_LOCK_ = true;
									FINAL_MOVE_POSITION = moVeView.getX();
									ANIMATION_LOCK = true;
									MOVE_TYPE = false;
								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {
								}

								@Override
								public void onAnimationEnd(Animation animation) {
									moVeView.setX(moVeView.getX()
											+ translation_amount);

									PREVIOUS_VIEW = null;
									ANIMATION_LOCK = false;
									recreateSlotChart(action);
									changeViewPositions(moVeView, action);
									recreateViewHeirarchy();

									if (mockMoveGenerator != null
											&& CALLBACK_WAIT == true) {

										mockMoveGenerator
												.setViewPositionMap(MoveController.this.VIEW_POSITION_MAP);
										mockMoveGenerator
												.startTranslationCallBack(false);
										CALLBACK_WAIT = false;
									}

									__MOVE_LOCK_ = false;
									tabView.setX(LAST_MOVED_POSITION);
								}
							});
							moVeView.startAnimation(tr);
						}
					}

				}

				break;
			case MotionEvent.ACTION_UP:
				MOTION_EVENT_UP_FLAG = true;
				TrannslationGeneratorFlag = false;
				if (!blank) {
					tabView.setVisibility(View.VISIBLE);
					((ViewGroup) __COPY_.getParent()).removeView(__COPY_);
					return;
				}
				LEFT_SIDE_LOCK = getChildCountWithout();
				if (mockMoveGenerator != null)
					mockMoveGenerator.fingerUpAction();
				if (INSERTION_SLOT != null) {
				}

				tabView.setVisibility(View.VISIBLE);
				if (!STACKED_MOVE) {
					if (MOVE_TYPE) {
						View prev_view = VIEW_POSITION_MAP[getIndexOfViewPositionMap(tabView) - 1].VIEW;
						tabView.setX((float) (prev_view.getX() + prev_view
								.getWidth() * .95));
					}

					refreshZAxis();
					View v = findUnOrderedView();
					return;
				}

				break;
			}
		} catch (Exception e) {
		}
	}

	private View findUnOrderedView() {
		ArrayList<View> temp = new ArrayList<View>();

		for (int i = 0; i < VIEW_POSITION_MAP.length - 1; i++) {
			View current = VIEW_POSITION_MAP[i].VIEW;
			View next = VIEW_POSITION_MAP[i + 1].VIEW;
			if (current.getX() + current.getWidth() < next.getX())
				temp.add(next);
		}
		if (temp.isEmpty())
			return null;
		return temp.get(temp.size() - 1);
	}

	private int getUnstackedPositions() {
		ArrayList<View> d = new ArrayList<View>();
		for (int i = 0; i < VIEW_POSITION_MAP.length - 1; i++) {
			if (VIEW_POSITION_MAP[i].VIEW.getX()
					+ VIEW_POSITION_MAP[i].VIEW.getWidth() * .8 >= VIEW_POSITION_MAP[i + 1].VIEW
					.getX())
				d.add(VIEW_POSITION_MAP[i + 1].VIEW);
		}
		if (d.isEmpty())
			return 0;
		else {
			if (d.get(d.size() - 1) == TABVIEW_INSTANCE)
				d.remove(d.size() - 1);
			if (!d.isEmpty())
				return getIndexOfViewPositionMap(d.get(d.size() - 1));
		}
		return 0;
	}

	private int getIndexOfViewPositionMap(View v) {
		for (int i = 0; i < VIEW_POSITION_MAP.length; i++) {
			if (VIEW_POSITION_MAP[i].VIEW == v)
				return VIEW_POSITION_MAP[i].POSITION;
		}
		return -1;
	}

	private View getBottomTAb(MotionEvent motionEvent) {

		ArrayList<View> d = new ArrayList<View>();

		for (int i = 0; i < VIEW_POSITION_MAP.length; i++) {
			View v = VIEW_POSITION_MAP[i].VIEW;
			if (motionEvent.getRawX() >= v.getX()
					&& motionEvent.getRawX() <= v.getX() + v.getWidth()) {
				d.add(v);
			}
		}
		if (!d.isEmpty()) {
			if (d.size() == 1) {
				if (d.get(0) != TABVIEW_INSTANCE)
					return d.get(0);
			}
			if (d.size() > 1) {
				if (d.get(d.size() - 1) == TABVIEW_INSTANCE) {
					return d.get(d.size() - 2);
				}
			}
			return d.get(d.size() - 1);
		}
		return null;
	}

	private int checkStaked(MotionEvent event) {
		int j = -1;
		ArrayList<View> d = new ArrayList<View>();
		for (int i = 0; i < VIEW_POSITION_MAP.length; i++) {
			View v = VIEW_POSITION_MAP[i].VIEW;
			if (v == tcurrentView)
				continue;
			if (event.getRawX() >= v.getX() + v.getWidth() * .15
					&& event.getRawX() <= v.getX() + v.getWidth() * .85) {
				d.add(v);
			}
		}
		if (d.isEmpty())
			return -1;
		else if (d.size() == 1)
			return -1;
		else {
			if (d.get(d.size() - 1) == tcurrentView)
				return indexOfMainViewInstance(d.get(d.size() - 2));
			return indexOfMainViewInstance(d.get(d.size() - 1));
		}
	}

	private View getStackedRightEnd(MotionEvent event) {
		int j = -1;
		ArrayList<View> d = new ArrayList<View>();
		for (int i = VIEW_POSITION_MAP.length - 1; i > 0; i--) {
			View v = VIEW_POSITION_MAP[i].VIEW;
			if (v == tcurrentView)
				continue;
			if (event.getRawX() >= v.getX() + v.getWidth() * .1
					&& event.getRawX() <= v.getX() + v.getWidth() * .9) {
				d.add(v);
			}
		}
		if (d.isEmpty())
			return null;
		else if (d.size() == 1)
			return null;
		else {
			if (d.get(d.size() - 1) == tcurrentView) {
				return d.get(d.size() - 2);
			}

			return d.get(d.size() - 1);
		}
	}

	private int indexOfMainViewInstance(View view) {
		for (int i = 0; i < VIEW_POSITION_MAP.length; i++) {
			if (view == VIEW_POSITION_MAP[i].VIEW)
				return i;
		}
		return -1;
	}

	/**
	 * working function
	 */
	private void refreshZAxis() {
		AddTab t = getAddTabView();
		MAIN_VIEW_INSTANCE.removeAllViews();
		for (int i = 0; i < VIEW_POSITION_MAP.length; i++)
			for (int j = 0; j < VIEW_POSITION_MAP.length; j++) {
				if (VIEW_POSITION_MAP[i].POSITION < VIEW_POSITION_MAP[j].POSITION) {
					TemporaryData temp = VIEW_POSITION_MAP[i];
					VIEW_POSITION_MAP[i] = VIEW_POSITION_MAP[j];
					VIEW_POSITION_MAP[j] = temp;
				}
			}

		for (int i = 0; i < VIEW_POSITION_MAP.length; i++) {
			View v = VIEW_POSITION_MAP[i].VIEW;
			MAIN_VIEW_INSTANCE.addView(v);
		}
		MAIN_VIEW_INSTANCE.addView(t);
	}

	private AddTab getAddTabView() {
		for (int i = 0; i < MAIN_VIEW_INSTANCE.getChildCount(); i++) {
			if (MAIN_VIEW_INSTANCE.getChildAt(i) instanceof AddTab)
				return (AddTab) MAIN_VIEW_INSTANCE.getChildAt(i);
		}
		return null;
	}

	private void changeViewPositions(View tabView, int action) {

		for (TemporaryData t : VIEW_POSITION_MAP) {
			if (t.VIEW == tcurrentView)
				Log.i("before", t.POSITION + "  " + t.VIEW + "  " + "villan");
			else if (t.VIEW == tabView)
				Log.i("before", t.POSITION + "  " + t.VIEW + "  " + "Applaying");
			else
				Log.i("before", t.POSITION + "  " + t.VIEW);
		}

		int old_pos = 0;
		for (int i = 0; i < VIEW_POSITION_MAP.length; i++) {
			TemporaryData t = VIEW_POSITION_MAP[i];
			if (t.VIEW == tabView) {
				if (action == 0) {
					old_pos = VIEW_POSITION_MAP[i].POSITION;
					VIEW_POSITION_MAP[i].POSITION++;
					for (int j = 0; j < VIEW_POSITION_MAP.length; j++) {
						TemporaryData t2 = VIEW_POSITION_MAP[j];
						if (t2.POSITION == old_pos + 1 && t2.VIEW != t.VIEW)
							VIEW_POSITION_MAP[j].POSITION = old_pos;
					}
				} else if (action == 1) {
					old_pos = VIEW_POSITION_MAP[i].POSITION;
					VIEW_POSITION_MAP[i].POSITION--;
					for (int j = 0; j < VIEW_POSITION_MAP.length; j++) {
						TemporaryData t2 = VIEW_POSITION_MAP[j];
						if (t2.POSITION == old_pos - 1 && t2.VIEW != t.VIEW)
							VIEW_POSITION_MAP[j].POSITION = old_pos;
					}
				}
			}
		}
		for (int i = 0; i < VIEW_POSITION_MAP.length; i++)
			for (int j = 0; j < VIEW_POSITION_MAP.length; j++) {
				if (VIEW_POSITION_MAP[i].POSITION < VIEW_POSITION_MAP[j].POSITION) {
					TemporaryData temp = VIEW_POSITION_MAP[i];
					VIEW_POSITION_MAP[i] = VIEW_POSITION_MAP[j];
					VIEW_POSITION_MAP[j] = temp;
				}
			}

		for (TemporaryData t : VIEW_POSITION_MAP) {
			if (t.VIEW == tcurrentView)
				Log.d("after sort", t.POSITION + "  " + t.VIEW + "  "
						+ "villan");
			else if (t.VIEW == tabView)
				Log.i("after sort", t.POSITION + "  " + t.VIEW + "  "
						+ "Applaying");
			else
				Log.d("after sort", t.POSITION + "  " + t.VIEW);
		}
	}

	/**
	 * 
	 * @param v
	 * @param action
	 *            0- > left
	 */
	private void changeViewPositionOfStacked(View v, int action) {

		for (int i = 0; i < VIEW_POSITION_MAP.length; i++) {
			View current = VIEW_POSITION_MAP[i].VIEW;
			if (v == current) {
				if (action == 0) {
					VIEW_POSITION_MAP[i].POSITION--;
					VIEW_POSITION_MAP[i - 1].POSITION++;
				} else if (action == 1) {
					VIEW_POSITION_MAP[i].POSITION++;
					VIEW_POSITION_MAP[i + 1].POSITION--;
				}
			}

		}
		for (int i = 0; i < VIEW_POSITION_MAP.length; i++)
			for (int j = 0; j < VIEW_POSITION_MAP.length; j++) {
				if (VIEW_POSITION_MAP[i].POSITION < VIEW_POSITION_MAP[j].POSITION) {
					TemporaryData temp = VIEW_POSITION_MAP[i];
					VIEW_POSITION_MAP[i] = VIEW_POSITION_MAP[j];
					VIEW_POSITION_MAP[j] = temp;
				}
			}
	}

	private void init_View_Positions() {
		VIEW_POSITION_MAP = new TemporaryData[getChildCountWithout()];
		for (int i = 0; i < getChildCountWithout(); i++) {
			View v = MAIN_VIEW_INSTANCE.getChildAt(i);
			VIEW_POSITION_MAP[i] = new TemporaryData(v, i);
		}
	}

	protected void recreateSlotChart(int action) {
		int cuttent_index = 0;
		if (action == -1)
			return;
		for (int i = 0; i < SLOT_CHART.length; i++) {
			if (SLOT_CHART[i] == 1) {
				cuttent_index = i;
				break;
			}

		}
		if (action == 0)
			cuttent_index--;
		else if (action == 1)
			cuttent_index++;
		else
			return;
		for (int i = 0; i < SLOT_CHART.length; i++) {
			if (i == cuttent_index)
				SLOT_CHART[i] = 1;
			else
				SLOT_CHART[i] = 0;
		}

	}

	protected void recreateViewHeirarchy() {
		ArrayList<int[]> temp = new ArrayList<int[]>();
		temp.clear();
		for (int i = 0; i < VIEW_POSITION_MAP.length; i++) {
			View tampView = VIEW_POSITION_MAP[i].VIEW;
			if (tampView instanceof ImageView)
				continue;
			temp.add(new int[] { (int) tampView.getX(),
					(int) (tampView.getX() + tampView.getWidth()),
					(int) (tampView.getX() + tampView.getWidth() * .3),
					(int) (tampView.getX() + tampView.getWidth() * .7) });
		}
		viewData.clear();
		for (int[] x : temp)
			viewData.add(x);
	}

	/**
	 * 
	 * @param moVeView
	 * @return 0 if move right, 1 for left and -1 for no action
	 */
	private int getAction(View moVeView) {

		int index = -1;
		for (int i = 0; i < SLOT_CHART.length; i++) {
			if (SLOT_CHART[i] == 1) {
				index = i;
				break;
			}
		}
		int index_target = -1;
		for (TemporaryData t : VIEW_POSITION_MAP) {
			if (t.VIEW == moVeView) {
				index_target = t.POSITION;
			}
		}
		return index > index_target ? 0 : (index < index_target ? 1 : -1);
	}

	private void createViewHeirarchy() {
		viewData.clear();
		for (int i = 0; i < MAIN_VIEW_INSTANCE.getChildCount(); i++) {
			View temp = MAIN_VIEW_INSTANCE.getChildAt(i);
			if (temp instanceof ImageView)
				continue;
			viewData.add(new int[] { (int) temp.getX(),
					(int) (temp.getX() + temp.getWidth()),
					(int) (temp.getX() + temp.getWidth() * .2),
					(int) (temp.getX() + temp.getWidth() * .8) });
		}
	}

	private TabView PREV_SELECTED;

	public void selectTab(final TabView v) {

		MAIN_VIEW_INSTANCE.SELECTED_TAB = v;

		if (MAIN_VIEW_INSTANCE.listner != null)
			MAIN_VIEW_INSTANCE.listner.select_tab(v.getUUID());

		for (int i = 0; i < MAIN_VIEW_INSTANCE.getChildCount(); i++) {
			if (MAIN_VIEW_INSTANCE.getChildAt(i) instanceof ImageView)
				continue;
			TabView tabView = (TabView) MAIN_VIEW_INSTANCE.getChildAt(i);
			if (tabView == v) {
				tabView.INFLATED_VIEW.setBackgroundResource(R.drawable.wb_tab);
			} else {
				tabView.INFLATED_VIEW
						.setBackgroundResource(R.drawable.wb_tab_unselected);
			}
		}
		changeFrameHeirarchy(v);
	}

	public void activateTab(TabView tabView) {
		changeFrameHeirarchy(tabView);
		tabView.requestLayout();
	}

	private void changeFrameHeirarchy(TabView v) {
		AddTab add = getAddTabView();
		MAIN_VIEW_INSTANCE.removeAllViews();
		for (TemporaryData t : VIEW_POSITION_MAP_FRAME_LAYOUT)
			MAIN_VIEW_INSTANCE.addView(t.VIEW);
		MAIN_VIEW_INSTANCE.addView(add);
		MAIN_VIEW_INSTANCE.bringChildToFront(v);

	}

	/**
	 * 
	 * @param view
	 * @return Bitmap of the view
	 */
	public Bitmap getBitmapFromView(View view) {
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
				view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			bgDrawable.draw(canvas);
		else
			canvas.drawColor(Color.TRANSPARENT);
		view.draw(canvas);
		return returnedBitmap;
	}

	private int LEFT_SIDE_LOCK;

	public void closeTabAction(final TabView tabView) {

		final int current_move;
		if (getIndexOfFrame(tabView) == 0 && getChildCountWithout() == 1) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					MAIN_VIEW_INSTANCE.getContext());
			alertDialog.setTitle("Exit Tint Browser ?");
			alertDialog.setMessage("ok to exit");
			alertDialog.setPositiveButton("ok", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {

					Intent i = new Intent(Constants.CLOSE_TAB_ACTION);
					getBaseContext().sendBroadcast(i);

				}
			});
			alertDialog.setNegativeButton("cancel", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
			alertDialog.show();
			return;
		} else if (getIndexOfFrame(tabView) == getChildCountWithout() - 1
				&& getIndexOfFrame(tabView) != 0) {
			View prev = VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(getIndexOfFrame(tabView) - 1).VIEW;
			if (MAIN_VIEW_INSTANCE.SELECTED_TAB == tabView)
				this.selectTab((TabView) prev);

			MAIN_VIEW_INSTANCE.removeView(tabView);
			init(MAIN_VIEW_INSTANCE.getChildCount());
			removeFromFrameList(tabView);
			final View addTab = getAddTabView();
			long disp = (long) (MAX_WIDTH_LIMIT - (VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW.getX() + VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW
					.getWidth() * .93));
			SpaceCurrectionAnimator animator = new SpaceCurrectionAnimator(200,
					1, disp, MAIN_VIEW_INSTANCE,
					VIEW_POSITION_MAP_FRAME_LAYOUT, MAX_WIDTH_LIMIT);
			animator.startTranslationCallBack(false);
			animator.setCallbackListner(new RenderingCompleteCallback() {

				@Override
				public void onFinished(float f) {
					if (addTab != null) {
						addTab.setX(f);

					}
				}
			});
			MoveController.this.ARRANGED_INDEX = 0;
			return;
		}
		float displacement;
		int action = 0;
		if (getIndexOfFrame(tabView) == 0 && getChildCountWithout() != 1) {
			current_move = org.cybrosys.customisation.Constants.FIRST_TAB;
			displacement = Math.abs(VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(getIndexOfFrame(tabView) + 1).VIEW.getX()
					- tabView.getX());

		} else if (getIndexOfFrame(tabView) == getChildCountWithout() - 1) {
			displacement = Math.abs(VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(getIndexOfFrame(tabView) - 1).VIEW.getX()
					- VIEW_POSITION_MAP_FRAME_LAYOUT
							.get(getIndexOfFrame(tabView)).VIEW.getX());
			action = 2;
		} else {
			displacement = (float) (VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(getIndexOfFrame(tabView) + 1).VIEW.getX() - (VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(getIndexOfFrame(tabView) - 1).VIEW.getX() + VIEW_POSITION_MAP_FRAME_LAYOUT
					.get(getIndexOfFrame(tabView) - 1).VIEW.getWidth() * .93));
		}
		tabView.setVisibility(View.GONE);
		final CloseActionAnimator animator = new CloseActionAnimator(400, 2,
				Math.abs((long) displacement), tabView, MAIN_VIEW_INSTANCE,
				action, VIEW_POSITION_MAP_FRAME_LAYOUT);
		animator.startTranslationCallBack(false);
		animator.setCallbackListner(new RenderingCompleteCallback() {

			@Override
			public void onFinished(float f) {
				final View addTab = getAddTab();
				int pos = getIndexOfFrame(tabView);
				final View prev = VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(getIndexOfFrame(tabView) + 1).VIEW;

				if (MAIN_VIEW_INSTANCE.listner != null)
					MAIN_VIEW_INSTANCE.listner.close_tab(tabView.getUUID());
				ArrayList<View> temp = new ArrayList<View>();
				for (int i = 0; i < VIEW_POSITION_MAP_FRAME_LAYOUT.size(); i++) {
					if (VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW != tabView) {
						temp.add(VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW);
					}
				}
				MAIN_VIEW_INSTANCE.removeAllViews();
				for (View v : temp)
					MAIN_VIEW_INSTANCE.addView(v);
				init(MAIN_VIEW_INSTANCE.getChildCount());
				MoveController.this.ARRANGED_INDEX = 0;
				removeFromFrameList(tabView);

				if (pos == 0)
					animator.ORG_TAB.setX(tabView.getX());
				long disp = (long) (MAX_WIDTH_LIMIT - (VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW
						.getX() + VIEW_POSITION_MAP_FRAME_LAYOUT
						.get(VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW
						.getWidth() * .93));
				SpaceCurrectionAnimator animator = new SpaceCurrectionAnimator(
						200, 1, disp, MAIN_VIEW_INSTANCE,
						VIEW_POSITION_MAP_FRAME_LAYOUT, MAX_WIDTH_LIMIT);
				animator.startTranslationCallBack(false);
				animator.setCallbackListner(new RenderingCompleteCallback() {

					@Override
					public void onFinished(float f) {
						if (addTab != null) {
							MAIN_VIEW_INSTANCE.addView(addTab);
							addTab.setX(VIEW_POSITION_MAP_FRAME_LAYOUT
									.get(getChildCountWithout() - 1).VIEW
									.getX()
									+ VIEW_POSITION_MAP_FRAME_LAYOUT
											.get(getChildCountWithout() - 1).VIEW
											.getWidth());
							if (MAIN_VIEW_INSTANCE.SELECTED_TAB == tabView)
								selectTab((TabView) prev);
							else

								activateTab((TabView) MAIN_VIEW_INSTANCE.SELECTED_TAB);
						}
					}
				});
			}

			private View getAddTab() {
				for (int i = 0; i < MAIN_VIEW_INSTANCE.getChildCount(); i++) {
					if (MAIN_VIEW_INSTANCE.getChildAt(i) instanceof ImageView)
						return MAIN_VIEW_INSTANCE.getChildAt(i);

				}
				return null;
			}
		});
	}

	private void removeFromFrameList(View v) {
		try {
			for (int i = 0; i < VIEW_POSITION_MAP_FRAME_LAYOUT.size(); i++)
				if (VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW == v)
					VIEW_POSITION_MAP_FRAME_LAYOUT.remove(i);
		} catch (Exception e) {
		}

	}

	public int getIndexOfFrame(TabView tabView) {
		for (TemporaryData data : VIEW_POSITION_MAP_FRAME_LAYOUT)
			if (data.VIEW == tabView)
				return VIEW_POSITION_MAP_FRAME_LAYOUT.indexOf(data);
		return -1;
	}

	@Override
	public void onFinished(float f) {

	}

	public int getChildCountWithout() {
		ArrayList<View> dat = new ArrayList<View>();
		for (int i = 0; i < MAIN_VIEW_INSTANCE.getChildCount(); i++) {
			if (!(MAIN_VIEW_INSTANCE.getChildAt(i) instanceof ImageView))
				dat.add(MAIN_VIEW_INSTANCE.getChildAt(i));

		}
		return dat.size();
	}

	private String createSlotString() {
		String s = "";
		for (int i : SLOT_CHART) {
			s = s + "   " + i;
		}
		return s;
	}

	public void init_left_stack() {

		int stacked = 0;
		for (int i = 0; i < VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1; i++) {
			View first = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;
			View second = VIEW_POSITION_MAP_FRAME_LAYOUT.get(i + 1).VIEW;
			if (stacked <= 2) {
				second.setX(first.getX() + 8);
				stacked++;

			} else {
				second.setX(first.getX());
			}
		}

	}

}
