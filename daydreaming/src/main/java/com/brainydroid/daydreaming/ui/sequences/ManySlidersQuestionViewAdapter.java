package com.brainydroid.daydreaming.ui.sequences;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.util.FloatMath;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.ManySlidersQuestionDescriptionDetails;
import com.brainydroid.daydreaming.sequence.ManySlidersAnswer;
import com.brainydroid.daydreaming.ui.AlphaImageButton;
import com.brainydroid.daydreaming.ui.AlphaSeekBar;
import com.brainydroid.daydreaming.ui.filtering.AutoCompleteAdapter;
import com.brainydroid.daydreaming.ui.filtering.AutoCompleteAdapterFactory;
import com.brainydroid.daydreaming.ui.filtering.MetaString;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import roboguice.inject.InjectResource;

public class ManySlidersQuestionViewAdapter
        extends BaseQuestionViewAdapter implements IQuestionViewAdapter {

    private static String TAG = "ManySlidersQuestionViewAdapter";

    @InjectResource(R.string.questionSlider_sliders_untouched_multiple)
    String errorUntouched;
    @InjectResource(R.string.questionSlider_please_slide) String textPleaseSlide;
    @InjectResource(R.string.page_edit_mode_done) String editTextDone;
    @InjectResource(R.string.page_edit_mode_edit) String editTextEdit;

    @Inject private AutoCompleteAdapterFactory autoCompleteAdapterFactory;
    @Inject private HashMap<MetaString, LinearLayout> sliderLayouts;
    @Inject private ManySlidersAnswer answer;

    private boolean autoCompleteAdapterLoaded = false;
    private ProgressDialog progressDialog = null;
    private LinearLayout rowContainer;
    private boolean isEditMode = false;
    private Activity activity;

    @TargetApi(11)
    @Override
    protected ArrayList<View> inflateViews(final Activity activity, final RelativeLayout outerPageLayout,
                                           final LinearLayout questionLayout) {
        Logger.d(TAG, "Inflating question views");
        this.activity = activity;

        final ManySlidersQuestionDescriptionDetails details =
                (ManySlidersQuestionDescriptionDetails)question.getDetails();
        ArrayList<String> userSliders = parametersStorage.getUserPossibilities(
                question.getQuestionName());
        if (userSliders.size() == 0) {
            userSliders = details.getDefaultSliders();
            parametersStorage.addUserPossibilities(question.getQuestionName(), userSliders);
        }

        final LinearLayout questionView = (LinearLayout)layoutInflater.inflate(
                R.layout.question_many_sliders, questionLayout, false);

        rowContainer = (LinearLayout)questionView.findViewById(
                R.id.question_many_sliders_rowContainer);
        TextView qText = (TextView)questionView.findViewById(
                R.id.question_many_sliders_mainText);
        String initial_qText = details.getText();
        qText.setText(getExtendedQuestionText(initial_qText));
        qText.setMovementMethod(LinkMovementMethod.getInstance());

        for (String sliderText : userSliders) {
            MetaString sliderMetaString = MetaString.getInstance(sliderText);
            LinearLayout sliderLayout = inflateSlider(sliderMetaString);
            sliderLayouts.put(sliderMetaString, sliderLayout);
            rowContainer.addView(sliderLayout);
        }

        // Set auto-complete hint and adapter
        final AutoCompleteTextView autoTextView = (AutoCompleteTextView)questionView
                .findViewById(R.id.question_many_sliders_autoCompleteTextView);
        autoTextView.setHint(details.getAddItemHint());
        final AutoCompleteAdapter autoCompleteAdapter = autoCompleteAdapterFactory.create();
        autoTextView.setAdapter(autoCompleteAdapter);

        // Load auto-complete adapter
        final ArrayList<String> allPossibleSliders = new ArrayList<String>();
        allPossibleSliders.addAll(details.getAvailableSliders());
        allPossibleSliders.addAll(userSliders);
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                autoCompleteAdapter.initialize(allPossibleSliders);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                autoCompleteAdapterLoaded = true;
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        }).execute();

        // Set auto-complete item click listener
        autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.d(TAG, "Item " + position + " clicked (id " + id + ")");
                addSelection(autoCompleteAdapter.getItemById(id));
                autoTextView.setText("");
            }
        });

        // Do layout transitions if possible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we can, animate layout changes
            rowContainer.setLayoutTransition(new LayoutTransition());
        } else {
            // Adapt colors for API <= 10
            autoTextView.setTextColor(context.getResources().getColor(
                    R.color.ui_dark_blue_color));
        }

        // Set auto-complete button listener
        Button addButton = (Button)questionView.findViewById(R.id.question_many_sliders_addItem);
        addButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                Logger.v(TAG, "Add button clicked");
                String userString = autoTextView.getText().toString();
                if (userString.length() < 2) {
                    Toast.makeText(context, "No text entered", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addSelection(MetaString.getInstance(userString))) {
                    autoTextView.setText("");
                    // Update adapter
                    autoCompleteAdapter.addPossibility(userString);
                }
            }

        });

        // Add edit button
        Button editButton = (Button)outerPageLayout.findViewById(R.id.page_editModeButton);
        editButton.setVisibility(View.VISIBLE);
        editButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!isEditMode) {
                    // Show explanation dialog
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    alertDialogBuilder.setTitle("Edit mode");
                    alertDialogBuilder
                    .setMessage(details.getDialogText())
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            toggleEditMode(outerPageLayout, questionView);
                        }
                    });
                    alertDialogBuilder.create().show();
                } else {
                    // If we're already in edit mode, just get out
                    toggleEditMode(outerPageLayout, questionView);
                }
            }

        });

        ArrayList<View> views = new ArrayList<View>();
        views.add(questionView);

        return views;
    }

    protected boolean addSelection(MetaString ms) {
        Logger.d(TAG, "Adding selection");

        if (sliderLayouts.containsKey(ms)) {
            Toast.makeText(context, "You already selected this item", Toast.LENGTH_SHORT).show();
            return false;
        }

        LinearLayout sliderLayout = inflateSlider(ms);
        sliderLayouts.put(ms, sliderLayout);
        rowContainer.addView(sliderLayout);

        // Save the item to parametersStorage
        parametersStorage.addUserPossibility(question.getQuestionName(), ms.getDefinition());

        return true;
    }

    protected void removeSelection(MetaString ms) {
        Logger.d(TAG, "Removing selection");

        LinearLayout sliderLayout = sliderLayouts.remove(ms);
        rowContainer.removeView(sliderLayout);
        parametersStorage.removeUserPossibility(question.getQuestionName(), ms.getDefinition());
    }

    @TargetApi(11)
    protected void toggleEditMode(RelativeLayout pageLayout, LinearLayout questionView) {
        // Start ProgressDialog if necessary
        if (!autoCompleteAdapterLoaded && progressDialog == null) {
            progressDialog = ProgressDialog.show(activity, "Loading", "Loading edit mode...");
        }

        Logger.v(TAG, "Toggling edit mode");
        isEditMode = !isEditMode;

        // Toggle delete buttons visibility
        for (LinearLayout sliderLayout : sliderLayouts.values()) {
            sliderLayout.findViewById(
                    R.id.question_many_sliders_sliderDelete)
                    .setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        }

        // Toggle auto-complete dropdown list to add items
        RelativeLayout addLayout = (RelativeLayout)questionView.findViewById(
                R.id.question_many_sliders_addLayout);
        addLayout.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        // Toggle add button text
        Button editButton = (Button)pageLayout.findViewById(R.id.page_editModeButton);
        editButton.setText(isEditMode ? editTextDone : editTextEdit);

        // Toggle next and finish buttons (visibility stays the same)
        AlphaImageButton nextButton = (AlphaImageButton)pageLayout.findViewById(R.id.page_nextButton);
        nextButton.setClickable(!isEditMode);
        nextButton.setAlpha(isEditMode ? 0.3f : 1f);
        AlphaImageButton finishButton = (AlphaImageButton)pageLayout.findViewById(R.id.page_finishButton);
        finishButton.setClickable(!isEditMode);
        finishButton.setAlpha(isEditMode ? 0.3f : 1f);
    }

    @TargetApi(11)
    private LinearLayout inflateSlider(final MetaString sliderMetaString) {
        Logger.v(TAG, "Inflating slider {}", sliderMetaString.getDefinition());

        ManySlidersQuestionDescriptionDetails details =
                (ManySlidersQuestionDescriptionDetails)question.getDetails();
        final LinearLayout sliderLayout = (LinearLayout)layoutInflater.inflate(
                R.layout.question_many_sliders_slider, rowContainer, false);

        // Set text
        ((TextView)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_mainText)).setText(sliderMetaString.getOriginal());

        // Set extremity hints
        final ArrayList<String> hints = details.getHints();
        final int nHints = details.getHints().size();
        ((TextView)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_leftHint)).setText(hints.get(0));
        ((TextView)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_rightHint)).setText(hints.get(nHints - 1));

        // Set initial position
        final AlphaSeekBar sliderSeek = (AlphaSeekBar)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_seekBar);
        if (details.getInitialPosition() !=
                ManySlidersQuestionDescriptionDetails.DEFAULT_INITIAL_POSITION) {
            sliderSeek.setProgress(details.getInitialPosition());
        }
        sliderSeek.setProgressDrawable(sliderLayout.getResources().getDrawable(
                R.drawable.question_slider_progress));
        sliderSeek.setThumb(sliderLayout.getResources().getDrawable(
                R.drawable.question_slider_thumb));
        // Lint erroneously catches this as a call that requires API >= 11
        // (which is exactly why AlphaSeekBar exists),
        // hence the @TargetApi(11) above.
        sliderSeek.setAlpha(0.5f);

        // Set live indication
        final TextView selectedSeek = (TextView)sliderLayout.findViewById(
                R.id.question_many_sliders_slider_selectedSeek);
        selectedSeek.setVisibility(details.isShowLiveIndication() ? View.VISIBLE : View.GONE);

        // Set progress listener
        AlphaSeekBar.OnAlphaSeekBarChangeListener progressListener =
                new AlphaSeekBar.OnAlphaSeekBarChangeListener() {

            @TargetApi(11)
            @Override
            public void onProgressChanged(AlphaSeekBar seekBar, int progress,
                                          boolean fromUser) {
                Logger.v(TAG, "SeekBar progress changed -> changing text and transparency");
                int index = (int) FloatMath.floor((progress / 100f) * nHints);
                if (index == nHints) {
                    // Have an open interval to the right
                    index -= 1;
                }

                selectedSeek.setText(hints.get(index));
                // Lint erroneously catches this as a call that requires API >= 11
                // (which is exactly why AlphaSeekBar exists),
                // hence the @TargetApi(11) above.
                seekBar.setAlpha(1f);
            }

            @Override
            public void onStartTrackingTouch(AlphaSeekBar seekBar) {
                seekBar.setThumb(context.getResources().getDrawable(
                        R.drawable.question_slider_thumb_big));
            }

            @Override
            public void onStopTrackingTouch(AlphaSeekBar seekBar) {
                seekBar.setThumb(context.getResources().getDrawable(
                        R.drawable.question_slider_thumb));
            }
        };

        sliderSeek.setOnSeekBarChangeListener(progressListener);

        // Add deletion listener
        ImageButton deleteButton = (ImageButton)sliderLayout.findViewById(
                R.id.question_many_sliders_sliderDelete);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.v(TAG, "Remove button clicked for item {}, removing slider",
                        sliderLayout.getOrientation());
                removeSelection(sliderMetaString);
            }
        });

        return sliderLayout;
    }

    @Override
    public boolean validate() {
        Logger.i(TAG, "Validating answer");

        for (LinearLayout sliderLayout : sliderLayouts.values()) {
            TextView selectedSeek = (TextView)sliderLayout.findViewById(
                    R.id.question_many_sliders_slider_selectedSeek);
            if (selectedSeek.getText().equals(textPleaseSlide)) {
                Logger.v(TAG, "Found an untouched slider");
                Toast.makeText(context, errorUntouched, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveAnswer() {
        Logger.i(TAG, "Saving question answer");

        for (Map.Entry<MetaString, LinearLayout> sliderLayoutMetaString : sliderLayouts.entrySet()) {
            AlphaSeekBar seekBar = (AlphaSeekBar)sliderLayoutMetaString.getValue().findViewById(
                    R.id.question_many_sliders_slider_seekBar);
            String sliderText = sliderLayoutMetaString.getKey().getOriginal();
            int progress = seekBar.getProgress();
            answer.addSlider(sliderText, progress);
        }

        question.setAnswer(answer);
    }
}
