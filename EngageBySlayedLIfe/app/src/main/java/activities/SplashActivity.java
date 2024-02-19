package activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import org.json.JSONObject;
import Base.BaseEngage;
import models.users.User;
import sql.auth;
import web.interfaces.UserInterface;

public class SplashActivity extends AppCompatActivity {

    private Context context;
    private Intent intent;
    UserInterface userInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        auth.Create((ContextWrapper)context);
        userInterface = authorizeUser();
        userInterface.AuthorizeUser();
    }

    private UserInterface authorizeUser() {
       return new UserInterface(obj -> {
           if(!obj.toString().equals("-1")) {
              switch (obj.toString().toLowerCase()) {
                  case "true":
                        userInterface = createAndVerifyUser();
                        userInterface.GetUserById();
                      break;
                  case "false":
                  default:
                     navigateToLogin();
              }
           } else {
               navigateToLogin();
           }
        });
    }

    private void navigateToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private UserInterface createAndVerifyUser() {
        return  new UserInterface(obj -> {
            try {
                if (obj != null && !obj.toString().equals("-1")) {
                    JSONObject data = new JSONObject(obj.toString());
                    BaseEngage.user = new Gson().fromJson(data.toString(), User.class);
                    intent = new Intent(context, UserDashboardActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }
            } catch(Exception ignore) {
                intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
