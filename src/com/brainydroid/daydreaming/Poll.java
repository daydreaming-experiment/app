package com.brainydroid.daydreaming;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;

public class Poll {

	private int _id;
	private String _status;
	private double _locationLatitude;
	private double _locationLongitude;
	private double _locationAltitude;
	private double _locationAccuracy;
	private long _timestamp;
	private int _questionsVersion;
	private ArrayList<Question> _questions;

	public static final String COL_ID = "pollId";
	public static final String COL_STATUS = "pollStatus";
	public static final String COL_LOCATION_LATITUDE = "pollLocationLatitude";
	public static final String COL_LOCATION_LONGITUDE = "pollLocationLongitude";
	public static final String COL_LOCATION_ALTITUDE = "pollLocationAltitude";
	public static final String COL_LOCATION_ACCURACY = "pollLocationAccuracy";
	public static final String COL_TIMESTAMP = "pollTimestamp";
	public static final String COL_QUESTIONS_VERSION = "pollQestionsVersion";

	public static final String STATUS_SCHEDULED = "pollScheduled";
	public static final String STATUS_RUNNING = "pollRunning";
	public static final String STATUS_DISMISSED = "pollDismissed";
	public static final String STATUS_PARTIAL = "pollPartiallyCompleted";
	public static final String STATUS_COMPLETED = "pollCompleted";

	private final Context _context;
	private final PollsStorage pollsStorage;
	private final QuestionsStorage questionsStorage;

	public Poll(Context context) {
		_context = context.getApplicationContext();
		_id = -1;
		_status = null;
		_locationLatitude = -1;
		_locationLongitude = -1;
		_locationAltitude = -1;
		_locationAccuracy = -1;
		_timestamp = -1;
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
	}

	public double getLocationLatitude() {
		return _locationLatitude;
	}

	public double getLocationLongitude() {
		return _locationLongitude;
	}

	public double getLocationAltitude() {
		return _locationAltitude;
	}

	public double getLocationAccuracy() {
		return _locationAccuracy;
	}

	public void setLocationLatitude(double locationLatitude) {
		_locationLatitude = locationLatitude;
	}

	public void setLocationLongitude(double locationLongitude) {
		_locationLongitude = locationLongitude;
	}

	public void setLocationAltitude(double locationAltitude) {
		_locationAltitude = locationAltitude;
	}

	public void setLocationAccuracy(double locationAccuracy) {
		_locationAccuracy = locationAccuracy;
	}

	public void setLocation(Location location) {
		_locationLatitude = location.getLatitude();
		_locationLongitude = location.getLongitude();
		_locationAltitude = location.getAltitude();
		_locationAccuracy = location.getAccuracy();
	}

	public long getTimestamp() {
		return _timestamp;
	}

	public void setTimestamp(long timestamp) {
		_timestamp = timestamp;
	}

	public int getQuestionsVersion() {
		return _questionsVersion;
	}

	public void setQuestionsVersion(int questionsVersion) {
		_questionsVersion = questionsVersion;
	}

	public void save() {
		if (_id != -1) {
			pollsStorage.updatePoll(this);
		} else {
			pollsStorage.storePollGetId(this);
		}
	}
}