package com.brainydroid.daydreaming.db;

import android.location.Location;
import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.lang.reflect.Type;

// this class defines the general structure of questions and their answer.
// These attributes will be saved

// TODO: add some way to save the phone's timezone and the user's preferences
// about what times he allowed notifications to appear at.

public class Question {

	private static String TAG = "Question";

	// attributes inherent to the question
	@Expose private String id = null;
	private String category = null;
	private String subcategory = null;
	private String type = null;
	private String mainText = null;
	private String parametersText = null;
	private int defaultPosition = -1;
	private int questionsVersion = -1;

	// attributes dependent on the answer
	@Expose private String status = null;
	@Expose private Answer answer = null;
	@Expose private double locationLatitude = -1;
	@Expose private double locationLongitude = -1;
	@Expose private double locationAltitude = -1;
	@Expose private double locationAccuracy = -1;
	@Expose private long timestamp = -1;

	public static final String PARAMETER_SPLITTER = "\\{s\\}";
	public static final String SUBPARAMETER_SPLITTER = "\\{ss\\}";

	public static final String COL_ID = "questionId";
	public static final String COL_CATEGORY = "questionCategory";
	public static final String COL_SUBCATEGORY = "questionSubcategory";
	public static final String COL_TYPE = "questionType";
	public static final String COL_MAIN_TEXT = "mainText";
	public static final String COL_PARAMETERS_TEXT = "parametersText";
	public static final String COL_DEFAULT_POSITION = "defaultPosition";
	public static final String COL_STATUS = "questionStatus";
	public static final String COL_ANSWER = "questionAnswer";
	public static final String COL_LOCATION_LATITUDE = "questionLocationLatitude";
	public static final String COL_LOCATION_LONGITUDE = "questionLocationLongitude";
	public static final String COL_LOCATION_ALTITUDE = "questionLocationAltitude";
	public static final String COL_LOCATION_ACCURACY = "questionLocationAccuracy";
	public static final String COL_TIMESTAMP = "questionTimestamp";
	public static final String COL_QUESTIONS_VERSION = "questionQuestionsVersion";

	public static final String STATUS_ASKED = "questionAsked";
	public static final String STATUS_ASKED_DISMISSED = "questionAskedDismissed";
	public static final String STATUS_ANSWERED = "questionAnswered";

	public static final String TYPE_SLIDER = "slider";
	public static final String TYPE_MULTIPLE_CHOICE = "multipleChoice";

    @Inject transient Gson gson;

    // constructor : sets default values
    @Inject
	public Question(QuestionsStorage questionsStorage) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] Question");
		}

		questionsVersion = questionsStorage.getQuestionsVersion();
	}

	//-------------------------- set / get functions

	public String getId() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getId");
		}

		return id;
	}

	public void setId(String id) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setId");
		}

		this.id = id;
	}

	public String getCategory() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getCategory");
		}

		return category;
	}

	public void setCategory(String category) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setCategory");
		}

		this.category = category;
	}

	public String getSubcategory() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getSubcategory");
		}

		return subcategory;
	}

	public void setSubcategory(String subcategory) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setSubcategory");
		}

		this.subcategory = subcategory;
	}

	public String getType() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getType");
		}

		return type;
	}

	private Type getTypeType() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getTypeType");
		}

		if (type == null) {
			throw new RuntimeException("Question type not set");
		} else if (type.equals(TYPE_MULTIPLE_CHOICE)) {
			return MultipleChoiceAnswer.class;
		} else if (type.equals(TYPE_SLIDER)) {
			return SliderAnswer.class;
		} else {
			throw new RuntimeException("Question type not recognized");
		}
	}

	public void setType(String type) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setType");
		}

		this.type = type;
	}

	public String getStatus() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getStatus");
		}

		return status;
	}

	public void setStatus(String status) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setStatus");
		}

		this.status = status;
	}

	public String getMainText() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getMainText");
		}

		return mainText;
	}

	public void setMainText(String mainText) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setMainText");
		}

		this.mainText = mainText;
	}

	public String getParametersText() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getParametersText");
		}

		return parametersText;
	}

	public void setParametersText(String parametersText) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setParametersText");
		}

		this.parametersText = parametersText;
	}

	public int getDefaultPosition() {

		// Verbose
		if (Config.LOGV){
			Log.v(TAG, "[fn] getDefaultPosition");
		}

		return defaultPosition;
	}

	public void setDefaultPosition(int defaultPosition) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setDefaultPosition");
		}

		this.defaultPosition = defaultPosition;
	}

	public String getAnswer() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getAnswer");
		}

		if (answer != null) {
			return answer.toJson();
		} else {
			return null;
		}
	}

	public void setAnswer(Answer answer) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setAnswer (from Answer)");
		}

		this.answer = answer;
	}

	public void setAnswer(String jsonAnswer) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setAnswer (from String)");
		}

		if (jsonAnswer != null && jsonAnswer.length() != 0) {
			Type typeType = getTypeType();
			Answer answer = gson.fromJson(jsonAnswer, typeType);
			setAnswer(answer);
		}
	}

	public double getLocationLatitude() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getLocationLatitude");
		}

		return locationLatitude;
	}

	public double getLocationLongitude() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getLocationLongitude");
		}

		return locationLongitude;
	}

	public double getLocationAltitude() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getLocationAltitude");
		}

		return locationAltitude;
	}

	public double getLocationAccuracy() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getLocationAccuracy");
		}

		return locationAccuracy;
	}

	public void setLocationLatitude(double locationLatitude) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setLocationLatitude");
		}

		this.locationLatitude = locationLatitude;
	}

	public void setLocationLongitude(double locationLongitude) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setLocationLongitude");
		}

		this.locationLongitude = locationLongitude;
	}

	public void setLocationAltitude(double locationAltitude) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setLocationAltitude");
		}

		this.locationAltitude = locationAltitude;
	}

	public void setLocationAccuracy(double locationAccuracy) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setLocationAccuracy");
		}

		this.locationAccuracy = locationAccuracy;
	}

	public void setLocation(Location location) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setLocation");
		}

		if (location != null) {
			locationLatitude = location.getLatitude();
			locationLongitude = location.getLongitude();
			locationAltitude = location.getAltitude();
			locationAccuracy = location.getAccuracy();
		}
	}

	public long getTimestamp() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getTimestamp");
		}

		return timestamp;
	}

	public void setTimestamp(long timestamp) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setTimestamp");
		}

		this.timestamp = timestamp;
	}

	public int getQuestionsVersion() {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] getQuestionsVersion");
		}

		return questionsVersion;
	}

	public void setQuestionsVersion(int questionsVersion) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setQuestionsVersion");
		}

		this.questionsVersion = questionsVersion;
	}

}
