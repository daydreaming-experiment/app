package com.brainydroid.daydreaming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.location.Location;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

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

	public static final String STATUS_ASKED = "questionAsked";
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
		initVariables();
	}

	public Question(Context context) {
		initVariables();
		_questionsVersion = QuestionsStorage.getInstance(context).getQuestionsVersion();
	}

	public Question(Context context, String id, String category, String subcategory, String type,
			String mainText, String parametersText) {
		initVariables();
		_id = id;
		_category = category;
		_subcategory = subcategory;
		_type = type;
		_mainText = mainText;
		_parametersText = parametersText;
		_questionsVersion = QuestionsStorage.getInstance(context).getQuestionsVersion();
	}

	private void initVariables() {
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

	public ArrayList<View> getViews(Context context) {
		Context _context = context;
		LayoutInflater inflater = (LayoutInflater)_context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		if (_type.equals(TYPE_SLIDER)) {
			return createViewsSlider(inflater);
			//		} else if (_type.equals(TYPE_SINGLE_CHOICE)) {
			//			return createViewsSingleChoice(inflater);
		} else if (_type.equals(TYPE_MULTIPLE_CHOICE)) {
			return createViewsMultipleChoice(inflater);
		}
		return null;
	}

	private ArrayList<View> createViewsSlider(LayoutInflater inflater) {
		ArrayList<View> views = new ArrayList<View>();
		ArrayList<String> mainTexts = getParsedMainText();
		ArrayList<ArrayList<String>> allParametersTexts = getParsedParametersText();

		Iterator<String> mtIt = mainTexts.iterator();
		Iterator<ArrayList<String>> ptsIt = allParametersTexts.iterator();

		while (mtIt.hasNext()) {
			String mainText = mtIt.next();
			final ArrayList<String> parametersTexts = ptsIt.next();

			View view = inflater.inflate(R.layout.question_slider, null);
			TextView qText = (TextView)view.findViewById(R.id.question_slider_mainText);
			qText.setText(mainText);
			TextView leftHintText = (TextView)view.findViewById(R.id.question_slider_leftHint);
			leftHintText.setText(parametersTexts.get(0));
			TextView rightHintText = (TextView)view.findViewById(R.id.question_slider_rightHint);
			rightHintText.setText(parametersTexts.get(parametersTexts.size() - 1));
			SeekBar seekBar = (SeekBar)view.findViewById(R.id.question_slider_seekBar);
			final TextView selectedSeek = (TextView)view.findViewById(R.id.question_slider_selectedSeek);
			final int maxSeek = parametersTexts.size();
			OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					int index = (int)FloatMath.floor((progress / 101f) * maxSeek);
					selectedSeek.setText(parametersTexts.get(index));
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			};
			seekBar.setOnSeekBarChangeListener(listener);

			views.add(view);
		}

		return views;
	}

	private ArrayList<View> createViewsMultipleChoice(LayoutInflater inflater) {
		ArrayList<View> views = new ArrayList<View>();
		ArrayList<String> mainTexts = getParsedMainText();
		ArrayList<ArrayList<String>> allParametersTexts = getParsedParametersText();

		Iterator<String> mtIt = mainTexts.iterator();
		Iterator<ArrayList<String>> ptsIt = allParametersTexts.iterator();

		while (mtIt.hasNext()) {
			String mainText = mtIt.next();
			final ArrayList<String> parametersTexts = ptsIt.next();

			View view = inflater.inflate(R.layout.question_multiple_choice, null);
			TextView qText = (TextView)view.findViewById(R.id.question_multiple_choice_mainText);
			qText.setText(mainText);
			final CheckBox otherCheck = (CheckBox)view.findViewById(R.id.question_multiple_choices_otherCheckBox);
			final EditText otherEdit = (EditText)view.findViewById(R.id.question_multiple_choices_otherEditText);
			OnCheckedChangeListener otherCheckListener = new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						otherEdit.requestFocus();
					} else {
						otherEdit.setText("");
					}
				}

			};
			otherCheck.setOnCheckedChangeListener(otherCheckListener);

			View.OnClickListener otherEditClickListener = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					otherCheck.setChecked(true);
				}

			};
			otherEdit.setOnClickListener(otherEditClickListener);

			View.OnFocusChangeListener otherEditFocusListener = new View.OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						otherCheck.setChecked(true);
					}
				}
			};
			otherEdit.setOnFocusChangeListener(otherEditFocusListener);

			LinearLayout checksLayout = (LinearLayout)view.findViewById(R.id.question_multiple_choice_rootChoices);
			Iterator<String> ptIt = parametersTexts.iterator();

			while (ptIt.hasNext()) {
				String parameter = ptIt.next();
				CheckBox checkBox = (CheckBox)inflater.inflate(R.layout.question_multiple_choices_item, null);
				checkBox.setText(parameter);
				checksLayout.addView(checkBox);
			}

			views.add(view);
		}

		return views;
	}

	public boolean validate() {
		if (_type.equals(TYPE_SLIDER)) {
			return validateSlider();
		} else if (_type.equals(TYPE_MULTIPLE_CHOICE)) {
			return validateMultipleChoice();
		}
		return false;
	}

	private boolean validateSlider() {
		// TODO: Find all sliders
		// Check they've all been touched
		return true;
	}

	private boolean validateMultipleChoice() {
		// TODO: Find all the checkboxes
		// Check at least one is checked ; check that if "Other" is checked, it is also filled
		return true;
	}

	private ArrayList<String> parseString(String toParse, String sep) {
		return new ArrayList<String>(Arrays.asList(toParse.split(sep)));
	}

	private ArrayList<String> getParsedMainText() {
		return parseString(_mainText, ";");
	}

	private ArrayList<ArrayList<String>> getParsedParametersText() {
		ArrayList<ArrayList<String>> parsedParametersText = new ArrayList<ArrayList<String>>();
		Iterator<String> preParsedIt = parseString(_parametersText, ";").iterator();
		ArrayList<String> subParameters;
		while (preParsedIt.hasNext()) {
			subParameters = parseString(preParsedIt.next(), "§");
			parsedParametersText.add(subParameters);
		}

		return parsedParametersText;
	}
}
