package activities;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.slayed.life.engage.R;

public class UserDashboardActivity extends BaseActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        setupHomeMenu();
        setupSettingMenu();
    }
}