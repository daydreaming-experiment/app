package com.brainydroid.daydreaming.ui;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.brainydroid.daydreaming.R;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DashboardActivityTest {

	@Test
	public void shouldHaveHappySmiles() throws Exception {
		String appName = new DashboardActivity().getResources().getString(R.string.app_name);
		assertThat(appName, equalTo("Day Dreaming"));
	}
}

