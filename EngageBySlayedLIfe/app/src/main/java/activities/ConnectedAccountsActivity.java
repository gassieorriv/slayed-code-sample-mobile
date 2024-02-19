package activities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import Base.BaseEngage;
import models.social.ConnectedSocialAccount;
import web.interfaces.SocialInterface;

public class ConnectedAccountsActivity extends BaseActivity {

    private Context context;
    private TableLayout connectedAccountTableLayout;
    private LinearProgressIndicator progressIndicator;
    private List<ConnectedSocialAccount> newConnectedAccountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_accounts);
        context = this;
        connectedAccountTableLayout = findViewById(R.id.connectedAccountTableLayout);
        progressIndicator = findViewById(R.id.progress_bar);
        newConnectedAccountList = new ArrayList<>();
        setupDeadEndMenu("Connected Accounts");
        setConnectedAccounts();
        setSave();
    }

    @SuppressLint("SetTextI18n")
    private void setConnectedAccounts() {
        progressIndicator.show();
        if(BaseEngage.user != null && BaseEngage.user.connectedSocialAccounts != null && BaseEngage.user.connectedSocialAccounts.length > 0) {
            new Handler().postDelayed(() -> {
                for (ConnectedSocialAccount connectedAccount : BaseEngage.user.connectedSocialAccounts) {
                    TableRow row = new TableRow(this);
                    row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = null;

                    if (inflater != null) {
                        view = inflater.inflate(R.layout.layout_connected_account, connectedAccountTableLayout, false);
                    }
                    if (view != null) {
                        MaterialTextView connectedAccountName = view.findViewById(R.id.connectedAccount_name);
                        MaterialTextView connectedAccountFollowing = view.findViewById(R.id.connectedAccount_following);
                        MaterialTextView connectedAccountFollowers = view.findViewById(R.id.connectedAccount_followers);
                        SwitchMaterial connectedAccountValue = view.findViewById(R.id.connectedAccount_active);
                        ImageView connectedAccountImage = view.findViewById(R.id.connectedAccount_image);
                        connectedAccountName.setText(connectedAccount.name);

                        if(connectedAccount.picture != null) {
                            Picasso.get()
                                    .load(connectedAccount.picture)
                                    .into(connectedAccountImage);

                        } else {
                            if(connectedAccount.socialAccount != null) {
                                switch (connectedAccount.socialAccount.id) {
                                    case 1:
                                        Picasso.get()
                                                .load(R.drawable.facebook)
                                                .into(connectedAccountImage);
                                        break;
                                    case 2:
                                        Picasso.get()
                                                .load(R.drawable.googleg_standard_color_18)
                                                .into(connectedAccountImage);
                                        break;
                                    case 3:
                                    case 4:
                                        Picasso.get()
                                                .load(R.drawable.instagram)
                                                .into(connectedAccountImage);
                                       break;
                                }
                            }
                        }

                        if(connectedAccount.socialAccount != null &&
                          (connectedAccount.socialAccount.id == 1 || connectedAccount.socialAccount.id == 2)) {
                            connectedAccountName.setText(BaseEngage.user.email);
                            connectedAccountValue.setEnabled(false);
                        }
                        connectedAccountFollowing.setText("Following: " + connectedAccount.following);
                        connectedAccountFollowers.setText("Followers: " + connectedAccount.followers);
                        connectedAccountValue.setChecked(connectedAccount.active);
                        connectedAccountName.setTag(connectedAccount);

                        view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        row.addView(view);
                        connectedAccountTableLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                }
                progressIndicator.hide();
                connectedAccountTableLayout.requestLayout();
            }, 100);
        }
    }

    private void setSave() {
        deadEndAppBar.setOnMenuItemClickListener(item -> {
            Submit();
            return false;
        });
    }

    private void Submit() {
        progressIndicator.show();
        for(int index = 0; index < connectedAccountTableLayout.getChildCount(); index++) {
            View connectedAccountView = connectedAccountTableLayout.getChildAt(index);
            MaterialTextView accountName = connectedAccountView.findViewById(R.id.connectedAccount_name);
            SwitchMaterial connectedAccountValue = connectedAccountView.findViewById(R.id.connectedAccount_active);
            ConnectedSocialAccount account = (ConnectedSocialAccount)accountName.getTag();
            account.active = connectedAccountValue.isChecked();
            newConnectedAccountList.add(account);
        }

        SocialInterface socialInterface = saveConnectedAccounts();
        socialInterface.UpdateAccountStatus(newConnectedAccountList);
    }

    private SocialInterface saveConnectedAccounts() {
        return new SocialInterface(obj -> {
            progressIndicator.hide();
            if(obj != null && !obj.toString().equals("-1")) {
                JSONArray data = new JSONArray(obj.toString());
                BaseEngage.user.connectedSocialAccounts = new Gson().fromJson(data.toString(), ConnectedSocialAccount[].class);
                finish();
            } else {
                Toast.makeText(context,"An error occurred while logging in. Please contact support", Toast.LENGTH_SHORT).show();
            }
        });
    }
}