package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.annotations.Expose;

public class Poll {

	private static String TAG = "Poll";

	@Expose private String _status;
	@Expose private int _questionsVersion;
	@Expose private ArrayList<Question> _questions;
	@Expose private int _subjectAge;
	private transient int _id;
	private transient boolean _keepInSync = false;

	public static final String COL_ID = "pollId";
	public static final String COL_STATUS = "pollStatus";
	public static final String COL_QUESTIONS_VERSION = "pollQestionsVersion";
	public static final String COL_KEEP_IN_SYNC = "pollKeepInSync";

	public static final String STATUS_PENDING = "pollPending"; // Notification has appeared
	public static final String STATUS_EXPIRED = "pollExpired"; // QuestionActivity was paused and not resumed fast enough, or notification waited for too long
	public static final String STATUS_DISMISSED = "pollDismissed"; // Notification was dismissed
	public static final String STATUS_RUNNING = "pollRunning"; // QuestionActivity is running
	public static final String STATUS_PARTIALLY_COMPLETED = "pollPartiallyCompleted"; // QuestionActivity was stopped, and Poll expired
	public static final String STATUS_COMPLETED = "pollCompleted"; // QuestionActivity completed

	public static final int KEEP_IN_SYNC_OFF = 0;
	public static final int KEEP_IN_SYNC_ON = 1;

	private transient final Context _context;
	private transient final PollsStorage pollsStorage;
	private transient final QuestionsStorage questionsStorage;

	public Poll(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] Poll");
		}

		_context = context.getApplicationContext();
		_id = -1;
		_status = null;
		_questionsVersion = QuestionsStorage.getInstance(_context).getQuestionsVersion();
		_questions = new ArrayList<Question>();
		pollsStorage = PollsStorage.getInstance(_context);
		questionsStorage = QuestionsStorage.getInstance(_context);
	}

	public static Poll create(Context context, int nQuestions) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] create");
		}

		Poll poll = new Poll(context);
		poll.populateQuestions(nQuestions);
		return poll;
	}

	private void populateQuestions(int nQuestions) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] populateQuestions");
		}

		_questions = questionsStorage.getRandomQuestions(nQuestions);
	}

	public void addQuestion(Question question) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] addQuestion");
		}

		_questions.add(question);
	}

	public ArrayList<Question> getQuestions() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getQuestion");
		}

		return _questions;
	}

	public Question getQuestionByIndex(int index) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getQuestionByIndex");
		}

		return _questions.get(index);
	}

	public Question popQuestionByIndex(int index) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] popQuestionByIndex");
		}

		Question q = getQuestionByIndex(index);
		_questions.remove(index);
		return q;
	}

	public int getLength() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getLength");
		}

		return _questions.size();
	}

	public int getId() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getId");
		}

		return _id;
	}

	public void setId(int id) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setId");
		}

		_id = id;
	}

	public void clearId() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] clearId");
		}

		_id = -1;
	}

	public String getStatus() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getStatus");
		}

		return _status;
	}

	public void setStatus(String status) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setStatus");
		}

		_status = status;
		saveIfSync();
	}

	public void setQuestionStatus(int questionIndex, String status) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setQuestionStatus");
		}

		_questions.get(questionIndex).setStatus(status);
		saveIfSync();
	}

	public int getQuestionsVersion() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getQuestionsVersion");
		}

		return _questionsVersion;
	}

	public void setQuestionsVersion(int questionsVersion) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setQuestionsVersion");
		}

		_questionsVersion = questionsVersion;
		saveIfSync();
	}

	public boolean getKeepInSync() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getKeepInSync");
		}

		return _keepInSync;
	}

	public void setKeepInSync() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setKeepInSync");
		}

		_keepInSync = true;
	}

	private void saveIfSync() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] saveIfSync");
		}

		if (_keepInSync) {
			save();
		}
	}

	public void save() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] save");
		}

		setKeepInSync();
		if (_id != -1) {
			pollsStorage.updatePoll(this);
		} else {
			pollsStorage.storePollGetId(this);
		}
	}

	public void saveAnswers(LinearLayout questionLinearLayout, int index) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] saveAnswers");
		}

		// Location and Timestamp are set by callbacks defined in QuestionActivity
		_questions.get(index).saveAnswers(questionLinearLayout);
		save();
	}
}
