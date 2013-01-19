package com.brainydroid.daydreaming.db;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import android.graphics.drawable.ColorDrawable;

// this class defines the general structure of questions and their answer.
// These attributes will be saved

// TODO: add some way to save the phone's timezone and the user's preferences
// about what times he allowed notifications to appear at.

public class Question {

	private static String TAG = "Question";

	// attributes inherent to the question
	@Expose private String id;
	private String category;
	private String subcategory;
	private String type;
	private String mainText;
	private String parametersText;
	private int defaultPosition;
	private int questionsVersion;

	// attributes dependent on the answer
	@Expose private String status;
	@Expose private Answer answer;
	@Expose private double locationLatitude;
	@Expose private double locationLongitude;
	@Expose private double locationAltitude;
	@Expose private double locationAccuracy;
	@Expose private long timestamp;

	private transient Gson gson;

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

	// constructor : sets default values
	public Question(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] Question (argset 1: small)");
		}

		initVars();
		questionsVersion = QuestionsStorage.getInstance(context).getQuestionsVersion();
	}

	// constructor from fields values
	public Question(Context context, String id, String category, String subcategory, String type,
			String mainText, String parametersText, int defaultPosition) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] Question (argset 2: full)");
		}

		initVars();
		setId(id);
		setCategory(category);
		setSubcategory(subcategory);
		setType(type);
		setMainText(mainText);
		setParametersText(parametersText);
		setDefaultPosition(defaultPosition);
		setQuestionsVersion(QuestionsStorage.getInstance(context).getQuestionsVersion());
	}

	// default initialization of a question
	private void initVars() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] initVars");
		}

		id = null;
		category = null;
		subcategory = null;
		type = null;
		mainText = null;
		parametersText = null;
		defaultPosition = -1;
		status = null;
		answer = null;
		locationLatitude = -1;
		locationLongitude = -1;
		locationAltitude = -1;
		locationAccuracy = -1;
		timestamp = -1;
		questionsVersion = -1;
		gson = new Gson();
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
			throw new RuntimeException("Question type not set");
		}
	}

	private Answer newAnswerType() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] newAnswerType");
		}

		if (type == null) {
			throw new RuntimeException("Question type not set");
		} else if (type.equals(TYPE_MULTIPLE_CHOICE)) {
			return new MultipleChoiceAnswer();
		} else if (type.equals(TYPE_SLIDER)) {
			return new SliderAnswer();
		} else {
			throw new RuntimeException("Question type not set");
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

	private void setAnswer(Answer answer) {

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

	// Generating views for subsequent display of questions
	// Manage ui and fills answer fields of the class


	// Select questions by tags.
	// Tags only used to identify subquestions when they exist
	protected static ArrayList<View> getViewsByTag(ViewGroup root, String tag){

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getViewsByTag");
		}

		ArrayList<View> views = new ArrayList<View>();
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);
			if (child instanceof ViewGroup) {
				views.addAll(getViewsByTag((ViewGroup) child, tag));
			}

			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.equals(tag)) {
				views.add(child);
			}

		}
		return views;
	}

	public ArrayList<View> createViews(Context context) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] createViews");
		}

		Context _context = context;
		LayoutInflater inflater = (LayoutInflater)_context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		if (type.equals(TYPE_SLIDER)) {
			return createViewsSlider(inflater);
			//		} else if (_type.equals(TYPE_SINGLE_CHOICE)) {
			//			return createViewsSingleChoice(inflater);
		} else if (type.equals(TYPE_MULTIPLE_CHOICE)) {
			return createViewsMultipleChoice(inflater);
		}
		return null;
	}

	private ArrayList<View> createViewsSlider(LayoutInflater inflater) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] createViewsSlider");
		}

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

            seekBar.setBackgroundColor(Color.argb(255,255,205,205));

            OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					int index = (int)FloatMath.floor((progress / 101f) * maxSeek);
					selectedSeek.setText(parametersTexts.get(index));
                    seekBar.setBackgroundColor(Color.TRANSPARENT);
                }

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			};

			if (getDefaultPosition() != -1) {
				seekBar.setProgress(getDefaultPosition());
			}

			seekBar.setOnSeekBarChangeListener(listener);

			views.add(view);
		}

		return views;
	}

	private ArrayList<View> createViewsMultipleChoice(LayoutInflater inflater) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] createViewsMultipleChoice");
		}

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
						((LinearLayout)otherEdit.getParent()).requestFocus();
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

			final Context context = inflater.getContext();
			View.OnKeyListener onSoftKeyboardDonePress = new View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						InputMethodManager inputMM = (InputMethodManager)context.getSystemService(
								Context.INPUT_METHOD_SERVICE);
						inputMM.hideSoftInputFromWindow(otherEdit.getApplicationWindowToken(), 0);
					}
					return false;
				}
			};
			otherEdit.setOnKeyListener(onSoftKeyboardDonePress);

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

	// --- Validation functions

	public boolean validate(Context context, LinearLayout questionsLinearLayout) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] validate");
		}

		if (type.equals(TYPE_SLIDER)) {
			return validateSlider(context, questionsLinearLayout);
		} else if (type.equals(TYPE_MULTIPLE_CHOICE)) {
			return validateMultipleChoice(context, questionsLinearLayout);
		}
		return false;
	}

	// For slider, checks whether or not sliders were kept untouched
	private boolean validateSlider(Context context, LinearLayout questionsLinearLayout) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] validateSlider");
		}

		ArrayList<View> subquestions = getViewsByTag(questionsLinearLayout, "subquestion");
		boolean isMultiple = subquestions.size() > 1 ? true : false;
		Iterator<View> subquestionsIt = subquestions.iterator();

		while (subquestionsIt.hasNext()) {
			TextView selectedSeek = (TextView)subquestionsIt.next().
					findViewById(R.id.question_slider_selectedSeek);
			if (selectedSeek.getText().equals(
					context.getString(R.string.questionSlider_please_slide))) {
				Toast.makeText(context,
						context.getString(isMultiple ? R.string.questionSlider_sliders_untouched_multiple :
							R.string.questionSlider_sliders_untouched_single),
							Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		return true;
	}

	// This will behave badly when there are multiple sub-multiplechoice questions
	private boolean validateMultipleChoice(Context context, LinearLayout questionsLinearLayout) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] validateMultipleChoice");
		}

		ArrayList<View> subquestions = getViewsByTag(questionsLinearLayout, "subquestion");
		Iterator<View> subquestionsIt = subquestions.iterator();

		while (subquestionsIt.hasNext()) {

			LinearLayout subquestionLinearLayout = (LinearLayout)subquestionsIt.next();
			LinearLayout rootChoices = (LinearLayout)subquestionLinearLayout.findViewById(
					R.id.question_multiple_choice_rootChoices);
			int childCount = rootChoices.getChildCount();
			boolean hasCheck = false;
			CheckBox otherCheck = (CheckBox)subquestionLinearLayout.findViewById(
					R.id.question_multiple_choices_otherCheckBox);
			boolean hasOtherCheck = otherCheck.isChecked();

			if (hasOtherCheck) {
				EditText otherEditText = (EditText)subquestionLinearLayout.findViewById(
						R.id.question_multiple_choices_otherEditText);
				if (otherEditText.getText().length() == 0) {
					Toast.makeText(context,
							context.getString(R.string.questionMultipleChoices_other_please_fill),
							Toast.LENGTH_SHORT).show();
					return false;
				}
			}

			for (int i = 0; i < childCount; i++) {
				CheckBox child = (CheckBox)rootChoices.getChildAt(i);
				if (child.isChecked()) {
					hasCheck = true;
					break;
				}
			}

			if (!hasCheck && !hasOtherCheck) {
				Toast.makeText(context,
						context.getString(R.string.questionMultipleChoices_please_check_one),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		return true;
	}

	// Parsing subfunctions

	private ArrayList<String> parseString(String toParse, String sep) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] parseString");
		}

		return new ArrayList<String>(Arrays.asList(toParse.split(sep)));
	}

	private ArrayList<String> getParsedMainText() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getParsedMainText");
		}

		return parseString(mainText, PARAMETER_SPLITTER);
	}

	private ArrayList<ArrayList<String>> getParsedParametersText() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getParsedParametersText");
		}

		ArrayList<ArrayList<String>> parsedParametersText = new ArrayList<ArrayList<String>>();
		Iterator<String> preParsedIt = parseString(parametersText,
				PARAMETER_SPLITTER).iterator();
		ArrayList<String> subParameters;
		while (preParsedIt.hasNext()) {
			subParameters = parseString(preParsedIt.next(), SUBPARAMETER_SPLITTER);
			parsedParametersText.add(subParameters);
		}

		return parsedParametersText;
	}

	public void saveAnswers(LinearLayout questionLinearLayout) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] saveAnswers");
		}

		Answer answer = newAnswerType();
		answer.getAnswersFromLayout(questionLinearLayout);
		setAnswer(answer);
	}
}
