package com.brainydroid.daydreaming.db;

import android.util.Log;
import android.widget.LinearLayout;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.ArrayList;

public class Poll {

	private static String TAG = "Poll";

	@Expose private String status = null;
	@Expose private int questionsVersion;
	@Expose @Inject private ArrayList<Question> questions;
	@Expose private long notificationTimestamp;
	private transient int id = -1;

	public static final String COL_ID = "pollId";
	public static final String COL_STATUS = "pollStatus";
	public static final String COL_NOTIFICATION_TIMESTAMP = "pollNotificationTimestamp";
	public static final String COL_QUESTIONS_VERSION = "pollQuestionsVersion";

	public static final String STATUS_PENDING = "pollPending"; // Notification has appeared
	public static final String STATUS_RUNNING = "pollRunning"; // QuestionActivity is running
	public static final String STATUS_PARTIALLY_COMPLETED = "pollPartiallyCompleted"; // QuestionActivity was stopped, and Poll expired
	public static final String STATUS_COMPLETED = "pollCompleted"; // QuestionActivity completed

    @Inject transient PollsStorage pollsStorage;
    @Inject transient QuestionsStorage questionsStorage;

    @Inject
	public Poll(QuestionsStorage questionsStorage) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] Poll (from questionsStorage)");
		}

		questionsVersion = questionsStorage.getQuestionsVersion();
	}

	public void populateQuestions(int nQuestions) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] populateQuestions");
		}

		questions = questionsStorage.getRandomQuestions(nQuestions);
	}

	public void addQuestion(Question question) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addQuestion");
		}

		questions.add(question);
	}

	public ArrayList<Question> getQuestions() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getQuestion");
		}

		return questions;
	}

	public Question getQuestionByIndex(int index) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getQuestionByIndex");
		}

		return questions.get(index);
	}

	public int getLength() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getLength");
		}

		return questions.size();
	}

	public int getId() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getId");
		}

		return id;
	}

	public void setId(int id) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setId");
		}

        // This method is called either from PollsStorage.storePollSetId(...) or
        // from PollsStorage.getPoll(...), and in both cases calling saveIfSync() would
        // trigger an unnecessary save. So we don't call it, contrary to other setters below.
		this.id = id;
	}

	public String getStatus() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getStatus");
		}

		return status;
	}

	public void setStatus(String status) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setStatus");
		}

		this.status = status;
		saveIfSync();
	}

	public void setQuestionStatus(int questionIndex, String status) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setQuestionStatus");
		}

		questions.get(questionIndex).setStatus(status);
		saveIfSync();
	}

	public long getNotificationTimestamp() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getNotificationTimestamp");
		}

		return notificationTimestamp;
	}

	public void setNotificationTimestamp(long notificationTimestamp) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setNotificationTimestamp");
		}

		this.notificationTimestamp = notificationTimestamp;
        saveIfSync();
	}

	public int getQuestionsVersion() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getQuestionsVersion");
		}

		return questionsVersion;
	}

	public void setQuestionsVersion(int questionsVersion) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setQuestionsVersion");
		}

		this.questionsVersion = questionsVersion;
		saveIfSync();
	}

	private void saveIfSync() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] saveIfSync");
		}

		if (id != -1) {
			save();
		}
	}

	public void save() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] save");
		}

		if (id != -1) {
			pollsStorage.updatePoll(this);
		} else {
			pollsStorage.storePollSetId(this);
		}
	}

	public void saveAnswers(LinearLayout questionLinearLayout, int index) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] saveAnswers");
		}

		// Location and Timestamp are set by callbacks defined in QuestionActivity
		questions.get(index).saveAnswers(questionLinearLayout);
		save();
	}

}
