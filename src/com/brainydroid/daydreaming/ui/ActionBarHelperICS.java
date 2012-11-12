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

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * An extension of {@link com.brainydroid.daydreaming.ui.example.android.actionbarcompat.ActionBarHelper} that provides Android
 * 4.0-specific functionality for IceCreamSandwich devices. It thus requires API level 14.
 */
public class ActionBarHelperICS extends ActionBarHelperHoneycomb {

	private static String TAG = "ActionBarHelpICS";

	protected ActionBarHelperICS(Activity activity) {
		super(activity);

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] ActionBarHelperICS");
		}

	}

	@Override
	protected Context getActionBarThemedContext() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getActionBarThemedContect");
		}

		return mActivity.getActionBar().getThemedContext();
	}
}
