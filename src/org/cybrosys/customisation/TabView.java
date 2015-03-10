package org.cybrosys.customisation;

import java.util.ArrayList;
import java.util.UUID;

import org.tint.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
@SuppressWarnings("unused")
public class TabView extends LinearLayout implements OnClickListener {

	public MoveController controller;
	ImageView close_button;
	LinearLayout layout, close;
	private boolean LONG_PRESSED = false;
	private final boolean HANDLER_LOCK = false;
	public static int LONG_PRESS_TIME = 400;
	final Handler _handler = new Handler();
	private boolean TOUCH_MOVED = false;
	private MotionEvent UP_EVENT;
	private MotionEvent down;
	private MotionEvent MOVE_EVENT;
	private boolean INIT_SCROLL_FLAG = false;
	private boolean INIT_MOVE_FLAG = false;
	ArrayList<MotionEvent> d = new ArrayList<MotionEvent>();
	private float X;
	private WebView WEBVIEW;
	private TextView TITLE_TAB;
	private ImageView FAVICON_TAB;
	private ImageView CLOSE_BUTTON;
	public View INFLATED_VIEW;

	private int MOVE_EVENT_COUNT = 0;

	private boolean MOVE_FLAG = false;
	private int MOVE_COUNT = 0;

	private boolean ONE_MOVE = false;

	protected UUID TABUUID;

	GestureDetector gestureDetector;

	Runnable _longPressed = new Runnable() {
		@Override
		public void run() {
			Log.i("info", "LongPress");
			LONG_PRESSED = true;
			controller.MOVABLE_CHILD_POSITION = (int) TabView.this.getX();
			TabView.this.setVisibility(View.INVISIBLE);
			controller.onMoveEvent(down, TabView.this);
		}
	};

	public TabView(Context context) {
		super(context);
		int tabWidth = (int) ((getScreenWIdth() * 0.93) - 15) / 4;
		controller = (MoveController) getContext().getApplicationContext();
		LinearLayout.LayoutParams x = new LinearLayout.LayoutParams(tabWidth,
				LayoutParams.MATCH_PARENT);
		this.setLayoutParams(x);
		this.setOrientation(LinearLayout.HORIZONTAL);
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		INFLATED_VIEW = inflater.inflate(R.layout.single_tab, null);
		this.addView(INFLATED_VIEW);
		close = (LinearLayout) INFLATED_VIEW.findViewById(R.id.close);
		TITLE_TAB = (TextView) INFLATED_VIEW.findViewById(R.id.textViewp);
		TITLE_TAB.setText("NEW TAB");
		layout = (LinearLayout) INFLATED_VIEW.findViewById(R.id.touchable_area);
		FAVICON_TAB = (ImageView) INFLATED_VIEW.findViewById(R.id.imageView1);
		CLOSE_BUTTON = (ImageView) INFLATED_VIEW.findViewById(R.id.imageView2);
		final View parent = (View) CLOSE_BUTTON.getParent();
		parent.post(new Runnable() {
			public void run() {
				final Rect r = new Rect();
				CLOSE_BUTTON.getHitRect(r);
				r.left -= 40;
				r.right += 50;
				r.top = -20;
				r.bottom = 20;
				parent.setTouchDelegate(new TouchDelegate(r, CLOSE_BUTTON));
			}
		});
		CLOSE_BUTTON.setOnClickListener(this);
		this.setOnClickListener(this);
		gestureDetector = new GestureDetector(context, new TabGesture());

	}

	@SuppressLint({ "Recycle", "ClickableViewAccessibility" })
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);
		controller.onTouchEvent(this, ev);
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
			MOVE_COUNT = 0;
		if (ev.getAction() == MotionEvent.ACTION_MOVE)
			MOVE_COUNT++;
		gestureDetector.onTouchEvent(ev);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void onClick(View v) {
		if (v == CLOSE_BUTTON) {
			if (controller.MAIN_VIEW_INSTANCE.listner != null) {

				if (controller.getChildCountWithout() == 1)
					;
				else
					controller.MAIN_VIEW_INSTANCE.listner.close_tab(this
							.getUUID());
			}
			if (controller != null)
				controller.closeTabAction(this);
			
			TintDatabase database = new TintDatabase(getContext());
			database.removeClosedTabData(this.getUUID());
		} else if (MOVE_COUNT < 5) {

			if (controller != null)
				controller.selectTab(this);
			if (controller.MAIN_VIEW_INSTANCE.listner != null)
				controller.MAIN_VIEW_INSTANCE.listner
						.select_tab(this.getUUID());

		}

	}

	public void setWebView(WebView webView) {
		this.WEBVIEW = webView;
	}

	public WebView getWebView() {
		return this.WEBVIEW;
	}

	public void setTitle(String titleString) {
		if (titleString.length() >= 13)
			titleString = titleString.substring(0, 13) + "...";
		this.TITLE_TAB.setText(titleString);
	}

	public void setFavIcon(Bitmap bitmap) {
		this.FAVICON_TAB.setImageBitmap(bitmap);
	}

	public void setUUID(UUID uuid) {
		this.TABUUID = uuid;
	}

	public UUID getUUID() {
		return this.TABUUID;
	}

	public int getScreenWIdth() {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x > size.y ? size.x : size.y;
	}

	private class TabGesture extends GestureDetector.SimpleOnGestureListener {

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}

	}
}