/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brainydroid.daydreaming.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * A <em>really</em> dumb implementation of the {@link android.view.MenuItem} interface, that's only
 * useful for our actionbar-compat purposes. See
 * <code>com.android.internal.view.menu.MenuItemImpl</code> in AOSP for a more complete
 * implementation.
 */
public class SimpleMenuItem implements MenuItem {

	private static String TAG = "SimpleMenuItem";

	private final SimpleMenu mMenu;

	private final int mId;
	private final int mOrder;
	private CharSequence mTitle;
	private CharSequence mTitleCondensed;
	private Drawable mIconDrawable;
	private int mIconResId = 0;
	private boolean mEnabled = true;

	public SimpleMenuItem(SimpleMenu menu, int id, int order, CharSequence title) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] SimpleMenuItem");
		}

		mMenu = menu;
		mId = id;
		mOrder = order;
		mTitle = title;
	}

	@Override
	public int getItemId() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getItemId");
		}

		return mId;
	}

	@Override
	public int getOrder() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getOrder");
		}

		return mOrder;
	}

	@Override
	public MenuItem setTitle(CharSequence title) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setTitle (from CharSequence)");
		}

		mTitle = title;
		return this;
	}

	@Override
	public MenuItem setTitle(int titleRes) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setTitle (from int)");
		}

		return setTitle(mMenu.getContext().getString(titleRes));
	}

	@Override
	public CharSequence getTitle() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getTitle");
		}

		return mTitle;
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setTitleCondensed");
		}

		mTitleCondensed = title;
		return this;
	}

	@Override
	public CharSequence getTitleCondensed() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getTitleCondensed");
		}

		return mTitleCondensed != null ? mTitleCondensed : mTitle;
	}

	@Override
	public MenuItem setIcon(Drawable icon) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setIcon (from Drawable)");
		}

		mIconResId = 0;
		mIconDrawable = icon;
		return this;
	}

	@Override
	public MenuItem setIcon(int iconResId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setIcon (from int)");
		}

		mIconDrawable = null;
		mIconResId = iconResId;
		return this;
	}

	@Override
	public Drawable getIcon() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getIcon");
		}

		if (mIconDrawable != null) {
			return mIconDrawable;
		}

		if (mIconResId != 0) {
			return mMenu.getResources().getDrawable(mIconResId);
		}

		return null;
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setEnabled");
		}

		mEnabled = enabled;
		return this;
	}

	@Override
	public boolean isEnabled() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isEnabled");
		}

		return mEnabled;
	}

	// No-op operations. We use no-ops to allow inflation from menu XML.

	@Override
	public int getGroupId() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getGroupId");
		}

		// Noop
		return 0;
	}

	@Override
	public View getActionView() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getActionView");
		}

		// Noop
		return null;
	}

	@Override
	public MenuItem setActionProvider(ActionProvider actionProvider) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setActionProvider");
		}

		// Noop
		return this;
	}

	@Override
	public ActionProvider getActionProvider() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getActionProvider");
		}

		// Noop
		return null;
	}

	@Override
	public boolean expandActionView() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] expandActionView");
		}

		// Noop
		return false;
	}

	@Override
	public boolean collapseActionView() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] collapseActionView");
		}

		// Noop
		return false;
	}

	@Override
	public boolean isActionViewExpanded() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] isActionViewExpanded");
		}

		// Noop
		return false;
	}

	@Override
	public MenuItem setOnActionExpandListener(OnActionExpandListener onActionExpandListener) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setOnActionExpandListener");
		}

		// Noop
		return this;
	}

	@Override
	public MenuItem setIntent(Intent intent) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setIntent");
		}

		// Noop
		return this;
	}

	@Override
	public Intent getIntent() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getIntent");
		}

		// Noop
		return null;
	}

	@Override
	public MenuItem setShortcut(char c, char c1) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setShortcut");
		}

		// Noop
		return this;
	}

	@Override
	public MenuItem setNumericShortcut(char c) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setNumericShortcut");
		}

		// Noop
		return this;
	}

	@Override
	public char getNumericShortcut() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getNumericShortcut");
		}

		// Noop
		return 0;
	}

	@Override
	public MenuItem setAlphabeticShortcut(char c) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setAlphabeticShortcut");
		}

		// Noop
		return this;
	}

	@Override
	public char getAlphabeticShortcut() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getAlphabeticShortcut");
		}

		// Noop
		return 0;
	}

	@Override
	public MenuItem setCheckable(boolean b) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setCheckable");
		}

		// Noop
		return this;
	}

	@Override
	public boolean isCheckable() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] isCheckable");
		}

		// Noop
		return false;
	}

	@Override
	public MenuItem setChecked(boolean b) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setChecked");
		}

		// Noop
		return this;
	}

	@Override
	public boolean isChecked() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] isChecked");
		}

		// Noop
		return false;
	}

	@Override
	public MenuItem setVisible(boolean b) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setVisible");
		}

		// Noop
		return this;
	}

	@Override
	public boolean isVisible() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] isVisible");
		}

		// Noop
		return true;
	}

	@Override
	public boolean hasSubMenu() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] hasSubMenu");
		}

		// Noop
		return false;
	}

	@Override
	public SubMenu getSubMenu() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getSubMenu");
		}

		// Noop
		return null;
	}

	@Override
	public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setOnMenuItemClickListener");
		}

		// Noop
		return this;
	}

	@Override
	public ContextMenu.ContextMenuInfo getMenuInfo() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getMenuInfo");
		}

		// Noop
		return null;
	}

	@Override
	public void setShowAsAction(int i) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setShowAsAction");
		}

		// Noop
	}

	@Override
	public MenuItem setShowAsActionFlags(int i) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setShowAsActionFlags");
		}

		// Noop
		return null;
	}

	@Override
	public MenuItem setActionView(View view) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setActionView");
		}

		// Noop
		return this;
	}

	@Override
	public MenuItem setActionView(int i) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setActionView");
		}

		// Noop
		return this;
	}
}
