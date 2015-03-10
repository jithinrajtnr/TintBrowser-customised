package org.cybrosys.customisation;

import java.util.ArrayList;
import java.util.UUID;

import org.cybrosys.customisation.OrientationChangeAnim.onStateChange;
import org.tint.ui.components.CustomWebView;
import org.tint.ui.managers.BaseUIManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FrameView extends FrameLayout implements OnClickListener {

	private MoveController controller = null;
	private AddTab addTab;
	public chromeTabListner listner;
	public View SELECTED_TAB;
	private int HEIGHT;

	public void setchromeTabListner(chromeTabListner l) {
		this.listner = l;
	}

	@SuppressLint("NewApi")
	public FrameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		controller = (MoveController) context.getApplicationContext();
		controller.MAIN_VIEW_INSTANCE = this;
		controller.init_dimentions();
		this.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT));
		this.setBackgroundColor(Color.BLACK);
		this.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						FrameView.this.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						FrameView.this.HEIGHT = FrameView.this.getHeight();
					}
				});
		controller.VIEW_POSITION_MAP_FRAME_LAYOUT = new ArrayList<TemporaryData>();
	}

	public void addFirstTab() {
		final TabView tab = new TabView(getContext());
		this.addView(tab);
		controller.init_dimentions();
		controller.VIEW_POSITION_MAP_FRAME_LAYOUT.add(new TemporaryData(tab,
				controller.VIEW_POSITION_MAP_FRAME_LAYOUT.size()));
		tab.setX(15);
		tab.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						tab.getViewTreeObserver().removeGlobalOnLayoutListener(
								this);
						addTab = new AddTab(getContext());
						LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						layoutParams.gravity = Gravity.BOTTOM;
						addTab.setLayoutParams(layoutParams);
						addTab.setX(tab.getX() + tab.getWidth());
						addTab.getViewTreeObserver().addOnGlobalLayoutListener(
								new OnGlobalLayoutListener() {

									@Override
									public void onGlobalLayout() {
										addTab.getViewTreeObserver()
												.removeGlobalOnLayoutListener(
														this);
										addTab.setY(((HEIGHT - addTab
												.getHeight()) / 2));
									}
								});
						FrameView.this.addView(addTab);
						controller.MAX_X_TABS = tab.getX() + tab.getWidth();
					}
				});
	}

	@SuppressLint("NewApi")
	public synchronized TabView addTab(boolean restore , int tabcount , int inedx) {
		controller.init_dimentions();
		final TabView tab = new TabView(getContext());
		this.addView(tab);
		controller.VIEW_POSITION_MAP_FRAME_LAYOUT
				.add(new TemporaryData(tab,
						controller.VIEW_POSITION_MAP_FRAME_LAYOUT
								.size()));
		if (! restore ) {
			try {
				
				ViewTreeObserver vto = this.getViewTreeObserver();
				vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						FrameView.this.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						controller.arrangeItems(false, tab);
						// FrameView.this.addView(addTab);
						addTab.setX(tab.getX() + tab.getWidth());
						if (controller != null)
							controller.selectTab(tab);

						controller.init(FrameView.this.getChildCount());

					}

				});
				return tab;
			} catch (Exception e) {
			}
		}
		else{
			View prev = controller.VIEW_POSITION_MAP_FRAME_LAYOUT.get(inedx - 1 ).VIEW;
			View current = controller.VIEW_POSITION_MAP_FRAME_LAYOUT.get(inedx ).VIEW;
			if ( inedx < 4){
				
				current.setX( prev.getX() + 8);
				
			}
			else
				current.setX( prev.getX() );
			
			if ( inedx == tabcount-1)
			{
				long disp =  (long) (controller.MAX_WIDTH_LIMIT -  (current.getX() + current.getWidth()));
				SpaceCurrectionAnimator animator = new SpaceCurrectionAnimator(500, 1, disp, this, controller.VIEW_POSITION_MAP_FRAME_LAYOUT, controller.MAX_WIDTH_LIMIT);
				animator.startTranslationCallBack(false);
				animator.setCallbackListner(new RenderingCompleteCallback() {
					
					@Override
					public void onFinished(float f) {
						// TODO Auto-generated method stub
						addTab.setX(f);
						controller.selectTab(tab);
						controller.init(FrameView.this.getChildCount());
						controller.ARRANGED_INDEX = controller.getIndexOfFrame(tab);
					}
				});
			}
			
			return tab;
		}

		return null;
	}

	public void setTitleByUUID(UUID uuid, String title) {
		for (int i = 0; i < this.getChildCount(); i++) {
			if (this.getChildAt(i) instanceof ImageView)
				continue;
			TabView t = (TabView) this.getChildAt(i);
			if (t.getUUID() == uuid) {
				t.setTitle(title);
			}
		}
	}

	public void setFavIconByUUID(UUID uuid, Bitmap bitmap) {
		for (int i = 0; i < this.getChildCount(); i++) {
			if (this.getChildAt(i) instanceof ImageView)
				continue;
			TabView t = (TabView) this.getChildAt(i);
			if (t.getUUID() == uuid) {
				t.setFavIcon(bitmap);
			}
		}
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		controller.init_dimentions();

		controller.init_move_stack();
		controller.init_left_stack();
		View lastView = controller.VIEW_POSITION_MAP_FRAME_LAYOUT
				.get(controller.VIEW_POSITION_MAP_FRAME_LAYOUT.size() - 1).VIEW;
		final long lastViewEnd = (long) (lastView.getX() + lastView.getWidth());
		long displacement = controller.MAX_WIDTH_LIMIT - lastViewEnd;
		if (displacement > 0) {
			final long lastViewEnd1 = (long) (lastView.getX() + lastView
					.getWidth());
			displacement = controller.MAX_WIDTH_LIMIT - lastViewEnd1;
			OrientationChangeAnim anim = new OrientationChangeAnim(200, 1,
					displacement, controller.VIEW_POSITION_MAP_FRAME_LAYOUT, 0,
					0);
			anim.startTranslationCallBack(false);
			anim.statechangeListner = new onStateChange() {

				@Override
				public void onFinish(float f) {
					addTab.setX(f);
				}
			};
		} else if (displacement < 0) {
			OrientationChangeAnim anim = new OrientationChangeAnim(200, 1,
					Math.abs(displacement) * 2,
					controller.VIEW_POSITION_MAP_FRAME_LAYOUT, 1,
					controller.MAX_WIDTH_LIMIT);
			anim.startTranslationCallBack(false);
			anim.statechangeListner = new onStateChange() {

				@Override
				public void onFinish(float f) {
					addTab.setX(f);
				}
			};
		}

	}
	public void saveTabs(BaseUIManager baseUIManager){
		TintDatabase database = new TintDatabase(getContext());
		for ( int i = 0 ; i < controller.VIEW_POSITION_MAP_FRAME_LAYOUT.size() ; i++ ){
			TabView tabView = (TabView)controller.VIEW_POSITION_MAP_FRAME_LAYOUT.get(i).VIEW;
			 CustomWebView baseWebViewFragment = baseUIManager.getWebViewByTabId(tabView.getUUID());
			 database.insertUrl(baseWebViewFragment.getUrl(), tabView.getUUID());
		}
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}

}
