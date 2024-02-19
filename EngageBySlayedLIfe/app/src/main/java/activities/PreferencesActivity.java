package activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import models.preferences.CurrentPreferences;
import web.interfaces.UserInterface;

public class PreferencesActivity extends BaseActivity {

    private UserInterface userInterface;
    private CurrentPreferences[] currentPreferencesList;
    private List<CurrentPreferences> newCurrentPreferencesList;
    private Context context;
    private TableLayout preferencesTableLayout;
    private LinearProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        context = this;
        newCurrentPreferencesList = new ArrayList<>();
        preferencesTableLayout = findViewById(R.id.preferencesTableLayout);
        progressIndicator = findViewById(R.id.progress_bar);
        setupDeadEndMenu("App Preferences");
        LoadUserPreferences();
        setSave();
    }

    private void setSave() {
        deadEndAppBar.setOnMenuItemClickListener(item -> {
            Submit();
            return false;
        });
    }

    private void Submit() {
        progressIndicator.show();
        for(int index = 0; index < preferencesTableLayout.getChildCount(); index++) {
            View preferenceView = preferencesTableLayout.getChildAt(index);
            MaterialTextView preferenceName = preferenceView.findViewById(R.id.preferences_name);
            SwitchMaterial preferenceValue = preferenceView.findViewById(R.id.preferences_active);
            CurrentPreferences preference = (CurrentPreferences)preferenceName.getTag();
            preference.active = preferenceValue.isChecked();
            newCurrentPreferencesList.add(preference);
        }

        userInterface = savePreferences();
        userInterface.UpdateUserPreferences(newCurrentPreferencesList);
    }

    private void LoadUserPreferences() {
        progressIndicator.show();
        userInterface = getPreferences();
        userInterface.GetUserPreferences();
    }

    private UserInterface savePreferences() {
        return new UserInterface(obj -> {
            progressIndicator.hide();
            if (obj.toString().equals("-1")) {
                Toast.makeText(context, "Oops! we couldn't save your preferences. Please try again.", Toast.LENGTH_SHORT).show();
             } else {
                finish();
            }
        });
    }

    private UserInterface getPreferences() {
        return new UserInterface(obj -> {
            if (obj.toString().equals("-1")) {
                Toast.makeText(context, "Oops! we couldn't load your preferences. Please try again.", Toast.LENGTH_SHORT).show();
                progressIndicator.hide();
            } else {
                JSONArray data = new JSONArray(obj.toString());
                currentPreferencesList = new Gson().fromJson(data.toString(), CurrentPreferences[].class);
                setPreferenceValues();
            }
        });
    }

    private void setPreferenceValues() {
        new Handler().postDelayed(() -> {
            for (CurrentPreferences preference : currentPreferencesList) {
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = null;

                if (inflater != null) {
                    view = inflater.inflate(R.layout.layout_preferences, preferencesTableLayout, false);
                }
                if (view != null) {
                    MaterialTextView preferenceName = view.findViewById(R.id.preferences_name);
                    SwitchMaterial preferenceValue = view.findViewById(R.id.preferences_active);
                    preferenceName.setText(preference.name);

                    if(preference.active != null) {
                        preferenceValue.setChecked(preference.active);
                    }

                    preferenceName.setTag(preference);

                    view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    row.addView(view);
                    preferencesTableLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
            progressIndicator.hide();
            preferencesTableLayout.requestLayout();
        },100);
    }
}