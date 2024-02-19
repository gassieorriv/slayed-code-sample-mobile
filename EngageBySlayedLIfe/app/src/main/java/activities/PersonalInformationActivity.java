package activities;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import org.json.JSONObject;
import Base.BaseEngage;
import fragments.fragment_date;
import models.users.User;
import web.interfaces.UserInterface;

public class PersonalInformationActivity extends BaseActivity {

    private TextInputEditText userName;
    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText email;
    private TextInputEditText dob;
    private TextInputEditText phone;
    private LinearProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        setupDeadEndMenu("Personal Information");
        userName = findViewById(R.id.username);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.dob);
        phone = findViewById(R.id.phone);
        progressIndicator = findViewById(R.id.progress_bar);
        setValues();
        setSave();
    }

    private void setValues() {
        userName.setText(BaseEngage.user.userName);
        firstName.setText(BaseEngage.user.firstName);
        lastName.setText(BaseEngage.user.lastName);
        email.setText(BaseEngage.user.email);
        dob.setText(BaseEngage.user.dob.replace("T00:00:00",""));
        phone.setText(BaseEngage.user.phone);
    }

    private void setSave() {
        deadEndAppBar.setOnMenuItemClickListener(item -> {
            Submit();
            return  false;
        });
    }

    public void Submit() {
        if(validateForm()) {
            progressIndicator.setVisibility(View.VISIBLE);
            UserInterface userInterface = updateUser();
            BaseEngage.user.firstName = firstName.getText().toString();
            BaseEngage.user.lastName = lastName.getText().toString();
            BaseEngage.user.phone = phone.getText().toString();
            BaseEngage.user.email = email.getText().toString();
            if(BaseEngage.user != null && BaseEngage.user.dob != null && !BaseEngage.user.dob.contains("T00:00:00")) {
                BaseEngage.user.dob = dob.getText().toString() + "T00:00:00";
            } else {
                BaseEngage.user.dob = dob.getText().toString();
            }
            userInterface.UpdateUser();
        } else {
            Toast.makeText(this, "Missing required information", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm() {
        if(firstName.getText() != null && firstName.getText().toString().length() <= 0) {
            return false;
        }

        if(lastName.getText() != null && lastName.getText().toString().length() <= 0) {
            return false;
        }

        if(email.getText() != null && email.getText().toString().length() <= 0) {
            return false;
        }

        if(phone.getText() != null && phone.getText().toString().length() <= 0) {
            return false;
        }

        if(dob.getText() != null && dob.getText().toString().length() <= 0) {
            return false;
        }

        return true;
    }

    public UserInterface updateUser() {
        return new UserInterface(obj -> {
            progressIndicator.setVisibility(View.GONE);
            if(obj != null && !obj.toString().equals("-1")) {
                JSONObject data = new JSONObject(obj.toString());
                BaseEngage.user = new Gson().fromJson(data.toString(), User.class);
            } else {
                Toast.makeText(this, "An error occurred while updating. Please try again later.", Toast.LENGTH_LONG).show();
            }
            finish();
        });
    }

    public void showStartDatePickerDialog(View view) {
        DialogFragment dateFragment = new fragment_date();
        dateFragment.show(getSupportFragmentManager(), "startDate");
    }

    public void getDate(String value) {
        dob.setText(value);
    }
}