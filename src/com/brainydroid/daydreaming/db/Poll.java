package com.brainydroid.daydreaming.db;

import java.util.ArrayList;

import android.content.Context;

public class Poll {

	private int _id;
	private String _status;
	private int _questionsVersion;
	private ArrayList<Question> _questions;
	private boolean _keepInSync = false;

	public static final String COL_ID = "pollId";
	public static final String COL_STATUS = "pollStatus";
	public static final String COL_QUESTIONS_VERSION = "pollQestionsVersion";
	public static final String COL_KEEP_IN_SYNC = "pollKeepInSync";

	public static final String STATUS_PENDING = "pollPending"; // Notification has appeared
	public static final String STATUS_STOPPED = "pollStopped"; // Notification has appeared, was opened, but QuestionActivity was paused
	public static final String STATUS_RUNNING = "pollRunning"; // QuestionActivity is running
	public static final String STATUS_EXPIRED = "pollExpired"; // QuestionActivity was paused and not resumed fast enough, or notification waited for too long
	public static final String STATUS_DISMISSED = "pollDismissed"; // Notification was dismissed
	public static final String STATUS_COMPLETED = "pollCompleted"; // QuestionActivity completed

	public static final int KEEP_IN_SYNC_OFF = 0;
	public static final int KEEP_IN_SYNC_ON = 1;

	private final Context _context;
	private final PollsStorage pollsStorage;
	private final QuestionsStorage questionsStorage;

	public Poll(Context context) {
		_context = context.getApplicationContext();
		_id = -1;
		_status = null;
		_questionsVersion = QuestionsStorage.getInstance(_context).getQuestionsVersion();
		_questions = new ArrayList<Question>();
		pollsStorage = PollsStorage.getInstance(_context);
		questionsStorage = QuestionsStorage.getInstance(_context);
	}

	public static Poll create(Context context, int nQuestions) {
		Poll poll = new Poll(context);
		poll.populateQuestions(nQuestions);
		return poll;
	}

	private void populateQuestions(int nQuestions) {
		_questions = questionsStorage.getRandomQuestions(nQuestions);
	}

	public void addQuestion(Question question) {
		_questions.add(question);
	}

	public ArrayList<Question> getQuestions() {
		return _questions;
	}

	public Question getQuestionByIndex(int index) {
		return _questions.get(index);
	}

	public Question popQuestionByIndex(int index) {
		Question q = getQuestionByIndex(index);
		_questions.remove(index);
		return q;
	}

	public int getLength() {
		return _questions.size();
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

	public void clearId() {
		_id = -1;
	}

	public String getStatus() {
		return _status;
	}

	public void setStatus(String status) {
		_status = status;
		saveIfSync();
	}

	public boolean isOver() {
		if (_status == null) {
			return false;
		}
		return _status.equals(STATUS_COMPLETED) || _status.equals(STATUS_DISMISSED) ||
				_status.equals(STATUS_EXPIRED);
	}

	public int getQuestionsVersion() {
		return _questionsVersion;
	}

	public void setQuestionsVersion(int questionsVersion) {
		_questionsVersion = questionsVersion;
		saveIfSync();
	}

	public boolean getKeepInSync() {
		return _keepInSync;
	}

	public void setKeepInSync() {
		_keepInSync = true;
	}

	private void saveIfSync() {
		if (_keepInSync) {
			save();
		}
	}

	public void save() {
		setKeepInSync();
		if (_id != -1) {
			pollsStorage.updatePoll(this);
		} else {
			pollsStorage.storePollGetId(this);
		}
	}

	public void saveAnswer(int index) {
		// TODO: get latest location
		// get timestamp
		// set them in the question
		// save poll
	}
}