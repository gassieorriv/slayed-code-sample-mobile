package activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import org.json.JSONObject;
import Base.BaseEngage;
import fragments.fragment_date;
import models.users.User;
import web.interfaces.UserInterface;

public class MissingInformationActivity extends AppCompatActivity {
    TextInputEditText userName;
    TextInputEditText firstName;
    TextInputEditText lastName;
    TextInputEditText email;
    TextInputEditText dob;
    TextInputEditText phone;
    MaterialButton button;
    boolean hasMissingFields = false;
    LinearProgressIndicator progressIndicator;
    UserInterface userInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_information);
        userName = findViewById(R.id.username);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.dob);
        phone = findViewById(R.id.phone);
        button = findViewById(R.id.submit);
        progressIndicator = findViewById(R.id.progress_bar);
        determineMissingFields();
        if(!hasMissingFields) {
            Intent intent = new Intent(this, ConnectSocialAccountsActivity.class);
            startActivity(intent);
        }
    }

    public void showStartDatePickerDialog(View view) {
        DialogFragment dateFragment = new fragment_date();
        dateFragment.show(getSupportFragmentManager(), "startDate");
    }

    public void getDate(String value) {
        dob.setText(value);
    }

    private void determineMissingFields() {
        if(BaseEngage.user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Oops! looks like an issue occurred during login. Please try again", Toast.LENGTH_LONG).show();
        }

        if(BaseEngage.user.email != null) {
            email.setVisibility(View.GONE);
            email.setText(BaseEngage.user.email);
        } else {
            hasMissingFields = true;
        }

        if(BaseEngage.user.phone != null) {
            phone.setVisibility(View.GONE);
            phone.setText(BaseEngage.user.phone);
        } else {
            hasMissingFields = true;
        }

        if(BaseEngage.user.firstName != null) {
            firstName.setVisibility(View.GONE);
            firstName.setText(BaseEngage.user.firstName);
        } else {
            hasMissingFields = true;
        }

        if(BaseEngage.user.lastName != null) {
            lastName.setVisibility(View.GONE);
            lastName.setText(BaseEngage.user.lastName);
        } else {
            hasMissingFields = true;
        }

        if(BaseEngage.user.dob != null) {
            dob.setVisibility(View.GONE);
            dob.setText(BaseEngage.user.dob);
        } else {
            hasMissingFields = true;
        }
    }

    public void Submit(View view) {
        if(validateForm()) {
           // progressIndicator.setVisibility(View.VISIBLE);
            button.setEnabled(false);
            userInterface = updateUser();
            BaseEngage.user.firstName = firstName.getText().toString();
            BaseEngage.user.lastName = lastName.getText().toString();
            BaseEngage.user.phone = phone.getText().toString();
            BaseEngage.user.email = email.getText().toString();
            BaseEngage.user.userName = userName.getText().toString();
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
        if(userName.getText() != null && userName.getText().toString().length() <= 0) {
            return false;
        }
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
            button.setEnabled(true);
         //   progressIndicator.setVisibility(View.GONE);
            if(obj != null && !obj.toString().equals("-1")) {
                JSONObject data = new JSONObject(obj.toString());
                BaseEngage.user = new Gson().fromJson(data.toString(), User.class);
            } else {
                Toast.makeText(this, "An error occurred updating. We'll try again later.", Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(this, ConnectSocialAccountsActivity.class);
            startActivity(intent);
        });
    }
}
