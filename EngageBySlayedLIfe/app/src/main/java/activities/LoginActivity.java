package activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import org.json.JSONObject;
import java.util.Arrays;
import Base.BaseEngage;
import models.Authorization;
import models.users.GoogleUser;
import models.users.User;
import sql.auth;
import web.interfaces.AuthorizationInterface;
import web.interfaces.UserInterface;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private Context context;
    private Authorization authorization;
    private AuthorizationInterface authorizationInterface;
    private UserInterface userInterface;
    private AppCompatButton facebookLogin;
    private AppCompatButton googleLogin;
    private LinearProgressIndicator progressIndicator;
    private GoogleSignInClient googleSigninClient;
    private GoogleSignInAccount account;
    private final int RC_SIGN_IN = 100;
    private final Scope[] youtubeScope = new Scope[]{
            new Scope("https://www.googleapis.com/auth/youtube"),
            new Scope("https://www.googleapis.com/auth/youtube.channel-memberships.creator"),
            new Scope("https://www.googleapis.com/auth/youtube.upload"),
            new Scope("https://www.googleapis.com/auth/youtube.readonly")
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN) {
            progressIndicator.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        this.authorization = new Authorization();
        facebookLogin = findViewById(R.id.facebook_login);
        googleLogin = findViewById(R.id.google_login);
        progressIndicator = findViewById(R.id.progress_bar);
        setupFacebookLogin();
        setupGoogleLogin();
    }

    public void loginFacebook(View view) {
        LoginManager.getInstance()
                .logIn(this, Arrays.asList(BaseEngage.facebookLoginPermssions));
    }

    public void loginGoogle(View view) {
        Intent signInIntent = googleSigninClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setupFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                startLoading();
                AccessToken accessToken =  loginResult.getAccessToken();
                authorization.authType = 1;
                authorization.engageUserId = 0;
                authorization.accessToken = accessToken.getToken();
                authorization.uid = accessToken.getUserId();
                authorizationInterface = authorizeAPI();
                authorizationInterface.FacebookApiAuthorization(authorization.accessToken);
            }

            @Override
            public void onCancel()
            {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Login cancelled", duration);
                toast.show();
            }

            @Override
            public void onError(FacebookException error) {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "error logging in", duration);
                toast.show();
            }
        });
    }

    private AuthorizationInterface authorizeAPI() {
        return  new AuthorizationInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                JSONObject data = new JSONObject(obj.toString());
                authorization.apiToken = data.getString("authenticationToken");
                auth.Delete((ContextWrapper)context);
                auth.Insert(authorization, (ContextWrapper)context);
                userInterface = createAndVerifyUser();
                switch (authorization.authType) {
                    case 1:
                        userInterface.CreateAndVerifyFacebookProfile();
                        break;
                    case 2:
                        GoogleUser googleUser = new GoogleUser();
                        googleUser.email = account.getEmail();
                        googleUser.familyName = account.getFamilyName();
                        googleUser.givenName = account.getGivenName();
                        if(account != null && account.getPhotoUrl() != null) {
                            googleUser.photo = account.getPhotoUrl().toString();
                        }
                        if(account != null) {
                            googleUser.id = account.getId();
                        }
                        userInterface.CreateAndVerifyGoogleProfile(googleUser);
                        break;
                }
            } else {
                endLoading();
                Toast.makeText(context, "An error occurred logging in. Please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private UserInterface createAndVerifyUser() {
        return  new UserInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                JSONObject data = new JSONObject(obj.toString());
                BaseEngage.user = new Gson().fromJson(data.toString(), User.class);
                authorization.engageUserId = BaseEngage.user.id;
                auth.Insert(authorization, (ContextWrapper)context);
                navigateToMissingInformation();
            } else {
                Toast.makeText(context,"An error occurred while logging in. Please contact support", Toast.LENGTH_SHORT).show();
                endLoading();
            }
        });
    }

    private void setupGoogleLogin() {
        String googleClientId = new BaseEngage().getGoogleClient(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(googleClientId)
                .requestServerAuthCode(googleClientId)
                .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"), youtubeScope)
                .requestProfile()
                .build();
        googleSigninClient = GoogleSignIn.getClient(this, gso);
    }

    private void navigateToMissingInformation() {
        Intent missingInfoIntent = new Intent(this, MissingInformationActivity.class);
        startActivity(missingInfoIntent);
        endLoading();
    }

    private void startLoading() {
        facebookLogin.setClickable(false);
        googleLogin.setClickable(false);
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void endLoading() {
        progressIndicator.setVisibility(View.GONE);
        facebookLogin.setClickable(true);
        googleLogin.setClickable(true);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            authorization.authType = 2;
            if(account != null) {
                authorization.accessToken = account.getIdToken();
                authorization.uid = account.getId();
                authorization.authCode = account.getServerAuthCode();
            }
            authorizationInterface = authorizeAPI();
            authorizationInterface.GoogleApiAuthorization(authorization.accessToken);
        }
        catch (ApiException e) {
            Toast.makeText(this, "Oops! Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
