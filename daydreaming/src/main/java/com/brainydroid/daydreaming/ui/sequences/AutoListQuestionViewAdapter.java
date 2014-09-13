package com.brainydroid.daydreaming.ui.sequences;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.background.StatusManager;
import com.brainydroid.daydreaming.db.AutoListQuestionDescriptionDetails;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.brainydroid.daydreaming.sequence.AutoListAnswer;
import com.brainydroid.daydreaming.ui.filtering.AutoCompleteAdapter;
import com.brainydroid.daydreaming.ui.filtering.AutoCompleteAdapterFactory;
import com.brainydroid.daydreaming.ui.filtering.MetaString;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;

import roboguice.inject.InjectResource;

@SuppressWarnings("UnusedDeclaration")
public class AutoListQuestionViewAdapter
        extends BaseQuestionViewAdapter implements IQuestionViewAdapter {

    private static String TAG = "AutoListQuestionViewAdapter";

    @Inject Context context;
    @Inject AutoListAnswer answer;
    @Inject AutoCompleteAdapterFactory autoCompleteAdapterFactory;
    @Inject ParametersStorage parametersStorage;

    @InjectResource(R.string.questionAutoList_please_select) String errorPleaseSelect;

    private LinearLayout selectionLayout;
    private ArrayList<String> initialPossibilities;
    @Inject private HashMap<MetaString, LinearLayout> selectionViews;

    @TargetApi(11)
    @Override
    protected ArrayList<View> inflateViews(Activity activity, LinearLayout questionLayout) {
        Logger.d(TAG, "Inflating question views");

        AutoListQuestionDescriptionDetails details =
                (AutoListQuestionDescriptionDetails)question.getDetails();
        initialPossibilities = details.getPossibilities();
        initialPossibilities.addAll(parametersStorage.getUserPossibilities(question.getQuestionName()));

        LinearLayout questionView = (LinearLayout)layoutInflater.inflate(
                R.layout.question_auto_list, questionLayout, false);

        TextView qText = (TextView)questionView.findViewById(
                R.id.question_auto_list_mainText);
        String initialQText = details.getText();
        qText.setText(getExtendedQuestionText(initialQText));
        qText.setMovementMethod(LinkMovementMethod.getInstance());

        selectionLayout = (LinearLayout)questionView.findViewById(
                R.id.question_auto_list_selectionList);
        final AutoCompleteTextView autoTextView = (AutoCompleteTextView)questionView.findViewById(
                R.id.question_auto_list_autoCompleteTextView);
        Button addButton = (Button)questionView.findViewById(R.id.question_auto_list_addButton);

        autoTextView.setHint(details.getHint());
        // TODO: move this initialization to background, because it takes around 5 seconds
        final AutoCompleteAdapter adapter = autoCompleteAdapterFactory.create();
        autoTextView.setAdapter(adapter);

        final ProgressDialog progressDialog = ProgressDialog.show(activity,
                "Loading", "Loading question...");

        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                adapter.initialize(initialPossibilities);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                progressDialog.dismiss();
            }
        }).execute();

        autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.d(TAG, "Item " + position + " clicked (id " + id + ")");
                addSelection(adapter.getItemById(id));
                autoTextView.setText("");
            }
        });

        // TODO: request keyboard focus

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we can, animate layout changes
            selectionLayout.setLayoutTransition(new LayoutTransition());
        } else {
            // Adapt colors for API <= 10
            autoTextView.setTextColor(context.getResources().getColor(
                    R.color.ui_dark_blue_color));
            // TODO: fix hint color if build < 11 ?
        }

        Button.OnClickListener addItemClickListener =
                new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                Logger.d(TAG, "Add button clicked");
                String userString = autoTextView.getText().toString();
                if (userString.length() < 2) {
                    Toast.makeText(context, "Please type in an activity", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addSelection(MetaString.getInstance(userString))) {
                    autoTextView.setText("");
                    // Update adapter
                    adapter.addPossibility(userString);
                    // TODO: save this new item at the persistence layer if kept at "Next moment"
                }
            }

        };

        addButton.setOnClickListener(addItemClickListener);


        ArrayList<View> views = new ArrayList<View>();
        views.add(questionView);

        return views;
    }

    protected boolean addSelection(final MetaString ms) {
        Logger.d(TAG, "Adding selection");

        if (selectionViews.containsKey(ms)) {
            Toast.makeText(context, "You already selected this item", Toast.LENGTH_SHORT).show();
            return false;
        }

        LinearLayout itemLayout = (LinearLayout)layoutInflater.inflate(
                R.layout.question_auto_list_item_selected_view, selectionLayout, false);
        ((TextView)itemLayout.findViewById(R.id.question_auto_list_selected_item_itemText))
                .setText(ms.getOriginal());

        ImageButton.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelection(ms);
            }
        };
        itemLayout.findViewById(R.id.question_auto_list_selected_item_itemDelete)
                .setOnClickListener(listener);

        selectionLayout.addView(itemLayout, 0);
        selectionViews.put(ms, itemLayout);

        return true;
    }

    protected void removeSelection(MetaString ms) {
        Logger.d(TAG, "Removing selection");
        if (!selectionViews.containsKey(ms)) {
            Log.v(TAG, "No item to remove");
            return;
        }

        selectionLayout.removeView(selectionViews.get(ms));
        selectionViews.remove(ms);
    }

    @Override
    public boolean validate() {
        Logger.i(TAG, "Validating choices");

        if (selectionViews.size() == 0) {
            Logger.v(TAG, "Nothing selected");
            Toast.makeText(context, errorPleaseSelect, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void saveAnswer() {
        Logger.i(TAG, "Saving question answer");

        for (MetaString ms : selectionViews.keySet()) {
            String s = ms.getDefinition();
            if (!initialPossibilities.contains(s)) {
                Logger.v(TAG, "Persisting possibility {} as a user possibility", s);
                parametersStorage.addUserPossibility(question.getQuestionName(), s);
            }
            answer.addChoice(s);
        }

        question.setAnswer(answer);
    }

}
