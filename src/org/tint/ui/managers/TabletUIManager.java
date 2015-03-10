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

package org.tint.ui.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.cybrosys.customisation.MoveController;
import org.cybrosys.customisation.TabView;
import org.cybrosys.customisation.chromeTabListner;
import org.tint.R;
import org.tint.controllers.Controller;
import org.tint.ui.activities.TintBrowserActivity;
import org.tint.ui.components.CustomWebView;
import org.tint.ui.fragments.BaseWebViewFragment;
import org.tint.ui.fragments.StartPageFragment;
import org.tint.ui.fragments.StartPageFragment.OnStartPageItemClickedListener;
import org.tint.ui.fragments.TabletStartPageFragment;
import org.tint.ui.fragments.TabletWebViewFragment;
import org.tint.ui.preferences.PreferencesActivity;
import org.tint.ui.tabs.WebViewFragmentTabListener;
import org.tint.ui.views.TabletUrlBar;
import org.tint.ui.views.TabletUrlBar.OnTabletUrlBarEventListener;
import org.tint.utils.Constants;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

public class TabletUIManager extends BaseUIManager implements chromeTabListner {

	private Map<Tab, TabletWebViewFragment> mTabs;
	Map<UUID, TabletWebViewFragment> mFragmentsMap;

	private TabletUrlBar mUrlBar;
	private ProgressBar mProgressBar;
	private ImageView mExitFullScreen;

	private ImageView overflow;

	public TabletUIManager(TintBrowserActivity activity) {
		super(activity);

		mTabs = new Hashtable<Tab, TabletWebViewFragment>();
		mFragmentsMap = new Hashtable<UUID, TabletWebViewFragment>();

		if (mStartPageFragment == null) {
			FragmentTransaction ft = mFragmentManager.beginTransaction();

			mStartPageFragment = new TabletStartPageFragment();
			mStartPageFragment
					.setOnStartPageItemClickedListener(new OnStartPageItemClickedListener() {
						@Override
						public void onStartPageItemClicked(String url) {
							loadUrl(url);
						}
					});

			ft.add(R.id.WebViewContainer, mStartPageFragment);
			ft.hide(mStartPageFragment);

			ft.commit();
			MoveController instance = (MoveController) getMainActivity()
					.getApplicationContext();
			instance.tabletUIManagerinstance = this;
			chrometabView.setchromeTabListner(this);

		}
	}

	public void onTabSelected(Tab tab) {
		updateUrlBar();

		CustomWebView webView = getCurrentWebView();
		if ((webView != null) && (!webView.isPrivateBrowsingEnabled())) {
			Controller.getInstance().getAddonManager()
					.onTabSwitched(mActivity, webView);
		}
	}

	@Override
	protected void setupUI() {

		Editor editor = PreferenceManager
				.getDefaultSharedPreferences(mActivity).edit();
		editor.putBoolean(Constants.PREFERENCE_FULL_SCREEN, false);
		editor.commit();

		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setHomeButtonEnabled(false);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mProgressBar = (ProgressBar) mActivity
				.findViewById(R.id.WebViewProgress);
		mExitFullScreen = (ImageView) mActivity
				.findViewById(R.id.ExitFullScreen);
		mExitFullScreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleFullScreen();
			}
		});

		mUrlBar = (TabletUrlBar) mActivity.findViewById(R.id.UrlBar);
		mUrlBar.setEventListener(new OnTabletUrlBarEventListener() {

			@Override
			public void onUrlValidated() {
				loadCurrentUrl();
			}

			@Override
			public void onHomeClicked() {
				loadHomePage();
			}

			@Override
			public void onGoStopReloadClicked() {
				if (mUrlBar.isUrlChangedByUser()) {
					// Use the UIManager to load urls, as it perform check on
					// them.
					loadCurrentUrl();
				} else if (getCurrentWebView().isLoading()) {
					getCurrentWebView().stopLoading();
				} else {
					getCurrentWebView().reload();
				}
			}

			@Override
			public void onForwardClicked() {
				if ((!getCurrentWebViewFragment().isStartPageShown())
						&& (getCurrentWebView().canGoForward())) {
					getCurrentWebView().goForward();
				}
			}

			@Override
			public void onBookmarksClicked() {
				openBookmarksActivityForResult();
			}

			@Override
			public void onBackClicked() {
				if ((!getCurrentWebViewFragment().isStartPageShown())
						&& (getCurrentWebView().canGoBack())) {
					getCurrentWebView().goBack();
				}
			}
		});

		overflow = (ImageView) mActivity.findViewById(R.id.UrlOverflaw);
		overflow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PopupMenu popup = new PopupMenu(getMainActivity(), overflow);
				popup.getMenuInflater().inflate(R.menu.poupup_menu,
						popup.getMenu());
				//init menu
				Menu menu = popup.getMenu();
				CustomWebView currentWebView = getCurrentWebView();
				boolean privateBrowsing = currentWebView != null
						&& currentWebView.isPrivateBrowsingEnabled();
				menu.findItem(R.id.MainActivity_MenuIncognitoTab).setChecked(
						privateBrowsing);
				boolean fullScreen = (mActivity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
				menu.findItem(R.id.MainActivity_MenuFullScreen).setChecked(
						fullScreen);

				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Intent i;

						switch (item.getItemId()) {

						case R.id.MainActivity_MenuAddBookmark:
							addBookmarkFromCurrentPage();
							return true;
						case R.id.MainActivity_MenuBookmarks:
							openBookmarksActivityForResult();
							return true;

						case R.id.MainActivity_MenuIncognitoTab:
							togglePrivateBrowsing();
							return true;

						case R.id.MainActivity_MenuSharePage:
							shareCurrentPage();
							return true;

						case R.id.MainActivity_MenuSearch:
							startSearch();
							return true;

						case R.id.MainActivity_MenuPreferences:
							i = new Intent(getMainActivity(),
									PreferencesActivity.class);
							getMainActivity().startActivity(i);
							return true;
						case R.id.MainActivity_MenuFullScreen:
							boolean fullScreen = (mActivity.getWindow()
									.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
							toggleFullscreen(!fullScreen);
						}
						return true;
					}
				});

				popup.show();
			}

		});

		super.setupUI();
	}

	private void toggleFullscreen(boolean fullscreen) {
		WindowManager.LayoutParams attrs = mActivity.getWindow()
				.getAttributes();
		if (fullscreen) {
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		} else {
			attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		}
		mActivity.getWindow().setAttributes(attrs);
	}

	@Override
	public CustomWebView getCurrentWebView() {
		if (mActionBar.getSelectedTab() != null) {
			return mTabs.get(mActionBar.getSelectedTab()).getWebView();
		} else {
			return null;
		}
	}

	@Override
	public String getCurrentUrl() {
		return mUrlBar.getUrl();
	}

	@Override
	public BaseWebViewFragment getCurrentWebViewFragment() {
		if (mActionBar.getSelectedTab() != null) {
			return mTabs.get(mActionBar.getSelectedTab());
		} else {
			return null;
		}
	}

	@Override
	public void addTab(String url, boolean openInBackground,
			boolean privateBrowsing, boolean restore, int c, int inedx) {
		Tab tab = mActionBar.newTab();
		tab.setText(R.string.NewTab);

		TabletWebViewFragment fragment = new TabletWebViewFragment();

		fragment.init(this, tab, privateBrowsing, url);

		tab.setTabListener(new WebViewFragmentTabListener(this, fragment));

		mTabs.put(tab, fragment);
		UUID fragmentId = fragment.getUUID();
		if (chrometabView.getChildCount() == 0) {
			chrometabView.addFirstTab();
			((TabView) chrometabView.getChildAt(0)).setUUID(fragmentId);
		} else {
			try {
				TabView newTabView = chrometabView.addTab(restore, c, inedx);
				newTabView.setUUID(fragmentId);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		mFragmentsMap.put(fragmentId, fragment);

		mActionBar.addTab(tab, mActionBar.getSelectedNavigationIndex() + 1);

		if (!openInBackground) {
			mActionBar.selectTab(tab);
		}
	}

	@Override
	public void closeCurrentTab() {
		if (mActionBar.getTabCount() > 1) {
			closeTabByTab(mActionBar.getSelectedTab());
		} else {
			loadHomePage();
		}
	}

	@Override
	public void closeTab(UUID tabId) {
		if (mActionBar.getTabCount() > 1) {
			TabletWebViewFragment fragment = (TabletWebViewFragment) getWebViewFragmentByUUID(tabId);
			if (fragment != null) {
				Tab tab = fragment.getTab();
				if (tab != null) {

					/**
					 * Next task
					 */
					closeTabByTab(tab);

				}
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		for (TabletWebViewFragment fragment : mTabs.values()) {
			fragment.getWebView().loadSettings();
		}
	}

	@Override
	public void onMenuVisibilityChanged(boolean isVisible) {
	}

	@Override
	public boolean onKeyBack() {
		if (!super.onKeyBack()) {
			CustomWebView currentWebView = getCurrentWebView();

			if ((currentWebView != null) && (currentWebView.canGoBack())) {
				currentWebView.goBack();
				return true;
			} else if (isHomePageStartPage() && !isStartPageShownOnCurrentTab()) {
				loadHomePage();
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onKeySearch() {
		mUrlBar.setFocusOnUrl();

		return true;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		if (view == getCurrentWebView()) {
			mProgressBar.setProgress(0);
			mProgressBar.setVisibility(View.VISIBLE);

			mUrlBar.setUrl(url);

			mUrlBar.setGoStopReloadImage(R.drawable.ic_stop);

			updateBackForwardEnabled();
			Iterator<Map.Entry<UUID, TabletWebViewFragment>> it = mFragmentsMap
					.entrySet().iterator();
			while (it.hasNext()) {
				Entry<UUID, TabletWebViewFragment> entry = it.next();

				// Remove entry if key is null or equals 0.
				if (entry.getKey() != null) {
					// Log.e(entry.getKey() + " key" , "  " +
					// getWebViewByTabId(entry.getKey()));
					if (getWebViewByTabId(entry.getKey()) == view)
						chrometabView.setFavIconByUUID(entry.getKey(), favicon);
				}
			}
		}
	}

	public class MyReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, "MyEvent", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);

		if (view == getCurrentWebView()) {
			mProgressBar.setProgress(100);
			mProgressBar.setVisibility(View.INVISIBLE);

			mUrlBar.setUrl(url);

			mUrlBar.setGoStopReloadImage(R.drawable.ic_refresh);

			updateBackForwardEnabled();

		}
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		if (view == getCurrentWebView()) {
			mProgressBar.setProgress(newProgress);
		}
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {

		for (TabletWebViewFragment fragment : mTabs.values()) {
			fragment.onReceivedTitle(view, title);

		}

		Iterator<Map.Entry<UUID, TabletWebViewFragment>> it = mFragmentsMap
				.entrySet().iterator();

		while (it.hasNext()) {
			Entry<UUID, TabletWebViewFragment> entry = it.next();
			if (entry.getKey() != null) {
				if (getWebViewByTabId(entry.getKey()) == view)
					chrometabView.setTitleByUUID(entry.getKey(), title);
			}
		}

	}

	@Override
	public void onShowStartPage() {
		mUrlBar.setUrl(null);
		mUrlBar.setBackEnabled(false);
		mUrlBar.setForwardEnabled(false);
		mUrlBar.setGoStopReloadImage(R.drawable.ic_go);

		mActionBar.setIcon(R.drawable.ic_launcher);

		Tab tab = mActionBar.getSelectedTab();
		tab.setText(R.string.NewTab);
	}

	@Override
	public void onHideStartPage() {
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	protected int getTabCount() {
		return mTabs.size();
	}

	@Override
	protected BaseWebViewFragment getWebViewFragmentByUUID(UUID fragmentId) {
		return mFragmentsMap.get(fragmentId);
	}

	@Override
	public void onActionModeStarted(ActionMode mode) {
	}

	@Override
	public void onActionModeFinished(ActionMode mode) {
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	}

	public StartPageFragment getStartPageFragment() {
		return mStartPageFragment;
	}

	@Override
	protected void setFullScreenFromPreferences() {
		boolean fullScreen = isFullScreen();

		if (fullScreen) {
			mActionBar.hide();
		} else {
			mActionBar.show();
		}

		if (mExitFullScreen != null) {
		}
	}

	@Override
	protected void showStartPage(BaseWebViewFragment webViewFragment) {

		if ((webViewFragment != null) && (!webViewFragment.isStartPageShown())) {

			webViewFragment.getWebView().onPause();
			webViewFragment.setStartPageShown(true);

			if (webViewFragment == getCurrentWebViewFragment()) {

				FragmentTransaction ft = mFragmentManager.beginTransaction();

				ft.hide(webViewFragment);
				ft.show(mStartPageFragment);

				ft.commit();

				onShowStartPage();
			}
		}
	}

	@Override
	protected void hideStartPage(BaseWebViewFragment webViewFragment) {

		if ((webViewFragment != null) && (webViewFragment.isStartPageShown())) {

			webViewFragment.setStartPageShown(false);

			if (webViewFragment == getCurrentWebViewFragment()) {

				FragmentTransaction ft = mFragmentManager.beginTransaction();

				ft.hide(mStartPageFragment);
				ft.show(webViewFragment);

				ft.commit();

				onHideStartPage();
			}
		}
	}

	@Override
	protected void resetUI() {
		updateUrlBar();
	}

	@Override
	protected Collection<BaseWebViewFragment> getTabsFragments() {
		return new ArrayList<BaseWebViewFragment>(mTabs.values());
	}

	private void closeTabByTab(Tab tab) {
		TabletWebViewFragment oldFragment = mTabs.get(tab);

		if (oldFragment != null) {
			CustomWebView webView = oldFragment.getWebView();

			if (!webView.isPrivateBrowsingEnabled()) {
				Controller.getInstance().getAddonManager()
						.onTabClosed(mActivity, webView);
			}

			webView.onPause();

			mTabs.remove(tab);
			mFragmentsMap.remove(oldFragment.getUUID());

			mActionBar.removeTab(tab);

			// Toast.makeText(mActivity, "close tab", 3).show();

		}
	}

	private void updateUrlBar() {
		CustomWebView currentWebView;
		BaseWebViewFragment currentFragment = getCurrentWebViewFragment();

		if ((currentFragment != null) && (currentFragment.isStartPageShown())) {
			currentWebView = null;
		} else {
			currentWebView = getCurrentWebView();
		}

		if (currentWebView != null) {
			String url = currentWebView.getUrl();

			if ((url != null) && (!url.isEmpty())) {
				mUrlBar.setUrl(url);
			} else {
				mUrlBar.setUrl(null);
			}

			setApplicationButtonImage(currentWebView.getFavicon());

			if (currentWebView.isLoading()) {
				mUrlBar.setGoStopReloadImage(R.drawable.ic_stop);
				mProgressBar.setVisibility(View.VISIBLE);
			} else {
				mUrlBar.setGoStopReloadImage(R.drawable.ic_refresh);
				mProgressBar.setVisibility(View.GONE);
			}

			updateBackForwardEnabled();
		} else {
			mUrlBar.setUrl(null);
			mUrlBar.setBackEnabled(false);
			mUrlBar.setForwardEnabled(false);

			mActionBar.setIcon(R.drawable.ic_launcher);

		}

		mUrlBar.setPrivateBrowsingIndicator(currentFragment != null ? currentFragment
				.isPrivateBrowsingEnabled() : false);
	}

	private void updateBackForwardEnabled() {
		CustomWebView currentWebView = getCurrentWebView();

		mUrlBar.setBackEnabled(currentWebView.canGoBack());
		mUrlBar.setForwardEnabled(currentWebView.canGoForward());
	}

	@Override
	public void select_tab(UUID uuid) {
		// TODO Auto-generated method stub
		if (mActionBar.getTabCount() > 1) {
			// Log.e("mActionBar.getTabCount" , "mActionBar.getTabCount");
			TabletWebViewFragment fragment = (TabletWebViewFragment) getWebViewFragmentByUUID(uuid);
			if (fragment != null) {
				Tab tab = fragment.getTab();
				mActionBar.selectTab(tab);
				onTabSelected(tab);

			}

		}

	}

	@Override
	public void close_tab(UUID uuid) {
		// TODO Auto-generated method stub
		closeTab(uuid);
	}

	/**
	 * Next Implementation
	 */
	@Override
	public void add_tab() {
		// TODO Auto-generated method stub

	}
}
