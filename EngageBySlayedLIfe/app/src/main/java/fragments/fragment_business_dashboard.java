package fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import Base.BaseEngage;
import activities.ManageProductActivity;
import activities.ManageScheduleActivity;
import activities.ManageServiceActivity;
import models.users.UserPaymentAccount;
import web.interfaces.StripeInterface;

public class fragment_business_dashboard extends Fragment {

    private MaterialButton connectStripeButton;
    private MaterialButton verifyStripeButton;
    private MaterialButton verifyStripeButton2;
    private MaterialButton moreInformation;
    private MaterialTextView connectedLabel;
    private MaterialTextView disconnectedLabel;
    private MaterialCardView noAccountCardView;
    private MaterialCardView accountCardView;
    private MaterialCardView productCardView;
    private MaterialCardView serviceCardView;
    private MaterialCardView scheduleCardView;
    private Context context;

    public fragment_business_dashboard() {
        // Required empty public constructor
    }

    public static fragment_business_dashboard newInstance() {
        fragment_business_dashboard fragment = new fragment_business_dashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_business_dashboard, container, false);
        context = view.getContext();
        connectStripeButton = view.findViewById(R.id.connect_stripe_button);
        verifyStripeButton = view.findViewById(R.id.connect_verify_button);
        verifyStripeButton2 = view.findViewById(R.id.connect_verify_button2);
        noAccountCardView = view.findViewById(R.id.no_payment_account_card);
        accountCardView = view.findViewById(R.id.account_card);
        connectedLabel = view.findViewById(R.id.connected);
        disconnectedLabel = view.findViewById(R.id.disconnected);
        moreInformation = view.findViewById(R.id.need_more_information);
        productCardView = view.findViewById(R.id.product_card);
        serviceCardView = view.findViewById(R.id.service_card);
        scheduleCardView = view.findViewById(R.id.schedle_card);

        setupConnectAccount();
        setupProduct();
        setupService();
        setupManageSchedule();
        return  view;
    }

    private void setupProduct() {
        productCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManageProductActivity.class);
            context.startActivity(intent);
        });
    }

    private void setupService() {
        serviceCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManageServiceActivity.class);
            context.startActivity(intent);
        });
    }

    private void setupManageSchedule() {
        scheduleCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManageScheduleActivity.class);
            context.startActivity(intent);
        });
    }

    private void setupConnectAccount() {
        if(BaseEngage.user != null && BaseEngage.user.userPaymentAccount != null) {
            if(BaseEngage.user.userPaymentAccount.isAcceptingPayments) {
                connectedLabel.setVisibility(View.VISIBLE);
            } else {
                moreInformation.setOnClickListener(v -> GetStripeAccountLink());
                moreInformation.setVisibility(View.VISIBLE);
                disconnectedLabel.setVisibility(View.VISIBLE);
                verifyStripeButton2.setOnClickListener(v -> GetAccountStatus());
            }
            accountCardView.setVisibility(View.VISIBLE);
        } else {
            connectStripeButton.setVisibility(View.VISIBLE);
            verifyStripeButton.setOnClickListener(v -> GetAccountStatus());
            connectStripeButton.setOnClickListener(v -> GetStripeAccountLink());
            noAccountCardView.setVisibility(View.VISIBLE);
        }
    }

    private StripeInterface getConnectedAccountLink() {
        return new StripeInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                String url = obj.toString();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(url));
                verifyStripeButton.setVisibility(View.VISIBLE);
                verifyStripeButton2.setVisibility(View.VISIBLE);
                connectStripeButton.setVisibility(View.GONE);
                moreInformation.setVisibility(View.GONE);
            } else {
                Toast.makeText(context, "An error occurred retrieving link", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private StripeInterface getAccountStatus() {
        return new StripeInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                accountCardView.setVisibility(View.GONE);
                noAccountCardView.setVisibility(View.GONE);
                moreInformation.setVisibility(View.GONE);
                disconnectedLabel.setVisibility(View.GONE);
                connectedLabel.setVisibility(View.GONE);
                verifyStripeButton2.setVisibility(View.GONE);
                verifyStripeButton.setVisibility(View.GONE);
                BaseEngage.user.userPaymentAccount = new Gson().fromJson(obj.toString(), UserPaymentAccount.class);
                setupConnectAccount();
            } else {
                Toast.makeText(context, "An error occurred while verifying. Please contact support.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetAccountStatus() {
        StripeInterface stripeInterface = getAccountStatus();
        stripeInterface.GetAccountStatus();
    }

    private  void GetStripeAccountLink() {
        StripeInterface stripeInterface = getConnectedAccountLink();
        stripeInterface.GetConnectAccountLink();
    }
}
