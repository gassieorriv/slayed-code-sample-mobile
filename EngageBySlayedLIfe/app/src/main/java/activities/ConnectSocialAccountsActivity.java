package activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import Base.BaseEngage;
import models.Authorization;
import models.social.TokenResponse;
import models.social.TwitterAccess;
import web.interfaces.SocialInterface;

public class ConnectSocialAccountsActivity extends AppCompatActivity {

    private TwitterAuthClient twitterAuthClient;
    private Context context;
    private TwitterAccess twitterAccess;
    private MaterialButton twitterButton;
    private MaterialButton instagramButton;
    private LinearProgressIndicator progressIndicator;
    private CallbackManager callbackManager;
    private Authorization authorization;
    private final Scope[] youtubeScope = new Scope[]{
            new Scope("https://www.googleapis.com/auth/youtube"),
            new Scope("https://www.googleapis.com/auth/youtube.channel-memberships.creator"),
            new Scope("https://www.googleapis.com/auth/youtube.upload"),
            new Scope("https://www.googleapis.com/auth/youtube.readonly")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_social_accounts);
        context = this;
        twitterButton = findViewById(R.id.twitter_connect);
        instagramButton = findViewById(R.id.instagram_connect);
        twitterAccess = new TwitterAccess();
        progressIndicator = findViewById(R.id.progress_bar);
        authorization = BaseEngage.getAuthroizedUser();
        CheckForSocialAccounts();
        setupFacebookLogin();
        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET))).build();
        Twitter.initialize(config);

    }

    private void CheckForSocialAccounts() {
       
    }

    public void Skip(View view) {
        Intent intent = new Intent(this, UserDashboardActivity.class);
        startActivity(intent);
    }

    public void ConnectTwitter(View view) {
        new Thread(() -> {
                twitterAuthClient = new TwitterAuthClient();
                twitterAuthClient.authorize((Activity) context, new Callback<TwitterSession>() {

                @Override
                public void success(Result<TwitterSession> twitterSessionResult) {
                    showProgressIndicator();
                    TwitterAuthToken token = twitterSessionResult.data.getAuthToken();
                    twitterAccess.secret = token.secret;
                    twitterAccess.token = token.token;
                    SocialInterface socialInterface = updateTwitterInfo();
                    socialInterface.UpdateTwitterInformation(twitterAccess);
                }

                @Override
                public void failure(TwitterException e) {
                    Toast.makeText(context, "Cannot connect your twitter account right now. Try again later", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    public void ConnectInstagram(View view) {
        Authorization authorization = BaseEngage.getAuthroizedUser();
        if(authorization.authType == 1) {
            showProgressIndicator();
            SocialInterface socialInterface = updateInstagramInfo();
            socialInterface.SocialConnectInstagram(authorization.accessToken);
        } else {
            loginFacebook();
        }
    }

    private SocialInterface updateTwitterInfo() {
        return new SocialInterface(obj -> {
            hideProgressIndicator();
            if(obj.toString().equals("-1")) {
                Toast.makeText(context, "Cannot connect your twitter account right now. Try again later", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Connected successfully!", Toast.LENGTH_LONG).show();
                hideTwitterButton();
                CheckForSocialAccounts();
            }
        });
    }

    private SocialInterface updateInstagramInfo() {
        return  new SocialInterface(obj -> {
            hideProgressIndicator();
            if(obj.toString().equals("-1")) {
                Toast.makeText(context, "Cannot connect your instagram account right now. Please try again later", Toast.LENGTH_LONG).show();
            } else {
                switch (obj.toString()) {
                    case "0":
                        Toast.makeText(context, "Unable to connect your instagram account, but we found your business account. Certain functionality will not be available until you get fully connected.", Toast.LENGTH_LONG).show();
                        break;
                    case "100":
                        Toast.makeText(context, "We found your personal Instagram, but not a business account. Certain functionality will not be available. Don't worry, you can connect it at anytime.", Toast.LENGTH_LONG).show();
                        hideInstagramButton();
                        CheckForSocialAccounts();
                        break;
                    case "200":
                        Toast.makeText(context, "Unable to connect your instagram account. Please try again later.", Toast.LENGTH_LONG).show();
                        hideInstagramButton();
                        CheckForSocialAccounts();
                        break;
                    case "250":
                        Toast.makeText(context, "Some of your accounts were connected. Please try connecting the rest later.", Toast.LENGTH_LONG).show();
                        CheckForSocialAccounts();
                        break;
                    case "300":
                        Toast.makeText(context, "Connected successfully!", Toast.LENGTH_LONG).show();
                        hideInstagramButton();
                        CheckForSocialAccounts();
                        break;
                }
            }
        });
    }

    private void showProgressIndicator() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressIndicator() {
        progressIndicator.setVisibility(View.GONE);
    }

    private void hideTwitterButton() {
        twitterButton.setVisibility(View.GONE);
    }

    private void hideInstagramButton() {
        instagramButton.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else if(requestCode == 140) {
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void loginFacebook() {
        LoginManager.getInstance()
                .logIn(this, Arrays.asList(BaseEngage.facebookLoginPermssions));
    }

    private void setupFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                showProgressIndicator();
                AccessToken accessToken =  loginResult.getAccessToken();
                SocialInterface socialInterface = updateInstagramInfo();
                socialInterface.SocialConnectInstagram(accessToken.getToken());
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ConnectYoutube(View view) {
            int RC_SIGN_IN = 100;
            if(authorization.authType == 2) {
             convertAuthCodeToAccessToken(authorization.authCode);
            } else {
                String googleClientId = new BaseEngage().getGoogleClient(this);
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestIdToken(googleClientId)
                        .requestServerAuthCode(googleClientId)
                        .requestProfile()
                        .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"), youtubeScope)
                        .build();
                GoogleSignInClient googleSigninClient = GoogleSignIn.getClient(this, gso);
                Intent signInIntent = googleSigninClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String authCode = account.getServerAuthCode();
            convertAuthCodeToAccessToken(authCode);
        }
        catch (ApiException exception) {
            Toast.makeText(this, "Oops! Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            exception.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void convertAuthCodeToAccessToken(String authCode) {
        try {
        InputStream clientSecretFile = new BaseEngage().getClientSecret(context);
        JsonFactory factory = JacksonFactory.getDefaultInstance();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(factory, new InputStreamReader(clientSecretFile));
        BaseEngage.setGoogleAuth( clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret(), authCode);
        SocialInterface socialInterface = getAccessToken();
        socialInterface.GetAccessTokenFromAuthCode();
        }
        catch (FileNotFoundException e) {
            Toast.makeText(this, "Oops! Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Oops! Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private SocialInterface getAccessToken() {
        return  new SocialInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {

                /* TODO: Finish youtube implementation. Still need to get the users basic data and create a connected account. */
                TokenResponse response = new Gson().fromJson(obj.toString(), TokenResponse.class);
                SocialInterface socialInterface = connectYoutube();
            } else {
                Toast.makeText(context, "Oops! an error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private SocialInterface connectYoutube(){
        return  new SocialInterface(obj -> {

        });
    }
}
