package activities;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.slayed.life.engage.R;
import Base.BaseEngage;
import fragments.fragment_business_dashboard;
import fragments.fragment_user_dashboard;
import models.Authorization;
import sql.auth;

public class BaseActivity extends AppCompatActivity {

    private Fragment fragment;
    public MaterialToolbar deadEndAppBar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    void setupSettingMenu() {

        /* Drawer navigation */
        DrawerLayout drawerLayout = findViewById(R.id.side_navigation);
        MaterialToolbar topAppBar = drawerLayout
                                   .findViewById(R.id.top_navigation)
                                   .findViewById(R.id.topAppBar);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                drawerLayout.setElevation(0);
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
               if(!drawerLayout.isOpen()) {
                   drawerLayout.setElevation(1000);
               }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        /* drawer side navigation */
        NavigationView navigationView = drawerLayout
                                       .findViewById(R.id.sideNavigationView);

        navigationView.setNavigationItemSelectedListener(item -> false);

        /* top app bar constant menu */
        topAppBar.setNavigationOnClickListener(v -> drawerLayout.open());
        topAppBar.setOnMenuItemClickListener(item -> {
            Intent intent;
            switch (item.getTitle().toString().toLowerCase()) {
                case "personal information":
                     intent = new Intent(this, PersonalInformationActivity.class);
                     startActivity(intent);
                     break;
                case "contact us":
                    intent = new Intent(this, ContactUsActivity.class);
                    startActivity(intent);
                    break;
                case "privacy policy":
                    intent = new Intent(this, PrivacyPolicyActivity.class);
                    startActivity(intent);
                    break;
                case "preferences":
                    intent = new Intent(this, PreferencesActivity.class);
                    startActivity(intent);
                    break;
                case "connected accounts":
                    intent = new Intent(this, ConnectedAccountsActivity.class);
                    startActivity(intent);
                    break;
                case "sign out":
                    signOut();
                    break;
            }
         return true;
        });
    }

    void setupHomeMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_user_navigation)
                                                   .findViewById(R.id.bottom_navigation);
        bottomNavigationView.setElevation(10);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String tag = "not_home";
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getTitle().toString().toLowerCase()) {
                case "home":
                    tag = "home";
                    fragment = fragment_user_dashboard.newInstance();
                    break;
                case "business":
                    fragment = fragment_business_dashboard.newInstance();
                    break;
                case "message":
                    fragment = fragment_user_dashboard.newInstance();
                    break;
                case "post":
                    fragment = fragment_user_dashboard.newInstance();
                    break;
                case "promotions":
                    fragment = fragment_user_dashboard.newInstance();
                    break;
            }
            transaction.replace(R.id.fragment_container, fragment, tag).commit();
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    void setupDeadEndMenu(String name) {
        deadEndAppBar = findViewById(R.id.top_navigation)
                .findViewById(R.id.topAppBar);
        deadEndAppBar.setTitle(name);
        deadEndAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void signOut() {
        Authorization client = auth.Get(this);
        if(client.authType == 1) {
            LoginManager.getInstance().logOut();
        } else {
            String googleClientId = new BaseEngage().getGoogleClient(this);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(googleClientId)
                    .requestServerAuthCode(googleClientId)
                    .build();
            GoogleSignInClient googleSigninClient = GoogleSignIn.getClient(this, gso);
            googleSigninClient.signOut();
        }
        auth.Delete(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
