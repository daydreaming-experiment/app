package com.brainydroid.daydreaming;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.location.Location;

public class Question {

	private String _id;
	private String _category;
	private String _subcategory;
	private String _type;
	private String _mainText;
	private String _parametersText;
	private String _status;
	private String _answer;
	private double _locationLatitude;
	private double _locationLongitude;
	private double _locationAltitude;
	private double _locationAccuracy;
	private long _timestamp;
	private int _questionsVersion;

	public static final String COL_ID = "questionId";
	public static final String COL_CATEGORY = "questionCategory";
	public static final String COL_SUBCATEGORY = "questionSubategory";
	public static final String COL_TYPE = "questionType";
	public static final String COL_MAIN_TEXT = "mainText";
	public static final String COL_PARAMETERS_TEXT = "parametersText";
	public static final String COL_STATUS = "questionStatus";
	public static final String COL_ANSWER = "questionAnswer";
	public static final String COL_LOCATION_LATITUDE = "questionLocationLatitude";
	public static final String COL_LOCATION_LONGITUDE = "questionLocationLongitude";
	public static final String COL_LOCATION_ALTITUDE = "questionLocationAltitude";
	public static final String COL_LOCATION_ACCURACY = "questionLocationAccuracy";
	public static final String COL_TIMESTAMP = "questionTimestamp";
	public static final String COL_QUESTIONS_VERSION = "questionQestionsVersion";

	public static final String STATUS_DISMISSED = "questionAsked";
	public static final String STATUS_ASKED_DISMISSED = "questionAskedDismissed";
	public static final String STATUS_ANSWERED = "questionAnswered";

	public static final String CATEGORY_MIND_WANDERING = "mindWandering";
	public static final String CATEGORY_CURRENT_ACTIVITY = "currentActivity";
	public static final String CATEGORY_AFFECTIVE_STATE = "affectiveState";

	public static final Set<String> CATEGORIES_WITH_SUBCATEGORIES =
			new HashSet<String>(Arrays.asList(new String[] {"mindWandering"}));
	public static final String SUBCATEGORY_CONTENT = "content";
	public static final String SUBCATEGORY_QUALITIES = "qualities";

	public static final String TYPE_SLIDER = "slider";
	public static final String TYPE_MULTIPLE_CHOICE = "multipleChoice";
	public static final String TYPE_SINGLE_CHOICE = "singleChoice";

	public Question() {
		_id = null;
		_category = null;
		_subcategory = null;
		_type = null;
		_mainText = null;
		_parametersText = null;
		_status = null;
		_answer = null;
		_locationLatitude = -1;
		_locationLongitude = -1;
		_locationAltitude = -1;
		_locationAccuracy = -1;
		_timestamp = -1;
		_questionsVersion = -1;
	}

	public Question(Context context) {
		_id = null;
		_category = null;
		_subcategory = null;
		_type = null;
		_mainText = null;
		_parametersText = null;
		_status = null;
		_answer = null;
		_locationLatitude = -1;
		_locationLongitude = -1;
		_locationAltitude = -1;
		_locationAccuracy = -1;
		_timestamp = -1;
		_questionsVersion = QuestionsStorage.getInstance(context).getQuestionsVersion();
	}

	public Question(Context context, String id, String category, String subcategory, String type,
			String mainText, String parametersText) {
		_id = id;
		_category = category;
		_subcategory = subcategory;
		_type = type;
		_status = null;
		_mainText = mainText;
		_parametersText = parametersText;
		_answer = null;
		_locationLatitude = -1;
		_locationLongitude = -1;
		_locationAltitude = -1;
		_locationAccuracy = -1;
		_timestamp = -1;
		_questionsVersion = QuestionsStorage.getInstance(context).getQuestionsVersion();
	}


	public String getId() {
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}

	public String getCategory() {
		return _category;
	}

	public void setCategory(String category) {
		_category = category;
	}

	public String getSubcategory() {
		return _subcategory;
	}

	public void setSubcategory(String subcategory) {
		_subcategory = subcategory;
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		_type = type;
	}

	public String getStatus() {
		return _status;
	}

	public void setStatus(String status) {
		_status = status;
	}

	public String getMainText() {
		return _mainText;
	}

	public void setMainText(String mainText) {
		_mainText = mainText;
	}

	public String getParametersText() {
		return _parametersText;
	}

	public void setParametersText(String parametersText) {
		_parametersText = parametersText;
	}

	public String getAnswer() {
		return _answer;
	}

	public void setAnswer(String answer) {
		_answer = answer;
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
}
