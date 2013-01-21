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

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

/**
 * A <em>really</em> dumb implementation of the {@link android.view.Menu} interface, that's only
 * useful for our actionbar-compat purposes. See
 * <code>com.android.internal.view.menu.MenuBuilder</code> in AOSP for a more complete
 * implementation.
 */
public class SimpleMenu implements Menu {

	private static String TAG = "SimpleMenu";

	private final Context mContext;
	private final Resources mResources;

	private final ArrayList<SimpleMenuItem> mItems;

	public SimpleMenu(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] SimpleMenu");
		}

		mContext = context;
		mResources = context.getResources();
		mItems = new ArrayList<SimpleMenuItem>();
	}

	public Context getContext() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getContext");
		}

		return mContext;
	}

	public Resources getResources() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getResources");
		}

		return mResources;
	}

	@Override
	public MenuItem add(CharSequence title) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] add (from CharSequence)");
		}

		return addInternal(0, 0, title);
	}

	@Override
	public MenuItem add(int titleRes) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] add (from int)");
		}

		return addInternal(0, 0, mResources.getString(titleRes));
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] add (from int, int, int, CharSequence)");
		}

		return addInternal(itemId, order, title);
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleRes) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] add (from int, int, int, int)");
		}

		return addInternal(itemId, order, mResources.getString(titleRes));
	}

	/**
	 * Adds an item to the menu.  The other add methods funnel to this.
	 */
	private MenuItem addInternal(int itemId, int order, CharSequence title) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addInternal");
		}

		final SimpleMenuItem item = new SimpleMenuItem(this, itemId, order, title);
		mItems.add(findInsertIndex(mItems, order), item);
		return item;
	}

	private static int findInsertIndex(ArrayList<? extends MenuItem> items, int order) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] findInsertIndex");
		}

		for (int i = items.size() - 1; i >= 0; i--) {
			MenuItem item = items.get(i);
			if (item.getOrder() <= order) {
				return i + 1;
			}
		}

		return 0;
	}

	public int findItemIndex(int id) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] findItemIndex");
		}

		final int size = size();

		for (int i = 0; i < size; i++) {
			SimpleMenuItem item = mItems.get(i);
			if (item.getItemId() == id) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public void removeItem(int itemId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] removeItem");
		}

		removeItemAtInt(findItemIndex(itemId));
	}

	private void removeItemAtInt(int index) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] removeItemAtInt");
		}

		if ((index < 0) || (index >= mItems.size())) {
			return;
		}
		mItems.remove(index);
	}

	@Override
	public void clear() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] clear");
		}

		mItems.clear();
	}

	@Override
	public MenuItem findItem(int id) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] findItem");
		}

		final int size = size();
		for (int i = 0; i < size; i++) {
			SimpleMenuItem item = mItems.get(i);
			if (item.getItemId() == id) {
				return item;
			}
		}

		return null;
	}

	@Override
	public int size() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] size");
		}

		return mItems.size();
	}

	@Override
	public MenuItem getItem(int index) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getItem");
		}

		return mItems.get(index);
	}

	// Unsupported operations.

	@Override
	public SubMenu addSubMenu(CharSequence charSequence) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addSubMenu (from CharSequence)");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public SubMenu addSubMenu(int titleRes) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addSubMenu (from int)");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addSubMenu (from int, int, int, CharSequence)");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addSubMenu (from int, int, int, int)");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public int addIntentOptions(int i, int i1, int i2, ComponentName componentName,
			Intent[] intents, Intent intent, int i3, MenuItem[] menuItems) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addIntentOptions");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public void removeGroup(int i) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] removeGroup");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public void setGroupCheckable(int i, boolean b, boolean b1) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setGroupCheckable");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public void setGroupVisible(int i, boolean b) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setGroupVisible");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public void setGroupEnabled(int i, boolean b) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setGroupEnabled");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public boolean hasVisibleItems() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] hasVisibleItems");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public void close() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] close");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public boolean performShortcut(int i, KeyEvent keyEvent, int i1) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] performShortcut");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public boolean isShortcutKey(int i, KeyEvent keyEvent) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] isShortcutKey");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public boolean performIdentifierAction(int i, int i1) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] performIdentifierAction");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	@Override
	public void setQwertyMode(boolean b) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setQwertyMode");
		}

		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}
}
