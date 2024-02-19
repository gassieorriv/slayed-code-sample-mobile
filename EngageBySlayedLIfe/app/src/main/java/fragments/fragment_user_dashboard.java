package fragments;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.slayed.life.engage.R;

import Base.BaseEngage;
import Utilities.Utilities;
import okhttp3.internal.Util;

import static Base.BaseEngage.*;

public class fragment_user_dashboard extends Fragment {
    private MaterialCardView levelCard;
    private MaterialTextView level;
    private MaterialTextView nextLevel;
    private LinearProgressIndicator levelIndicator;
    private MaterialCardView pointsCard;
    private MaterialTextView points;
    private MaterialButton redeemPointsButton;
    private MaterialCardView followingCard;
    private MaterialTextView following;
    private MaterialTextView followingPercent;
    private MaterialCardView followerCard;
    private MaterialTextView followers;
    private MaterialTextView followerPercent;
    private MaterialCardView salesCard;
    private MaterialTextView sales;
    private MaterialButton salesButton;
    private MaterialCardView upcomingAppointmentCard;
    private MaterialTextView upcomingAppointment;
    private MaterialCardView appointmentRequestCard;
    private MaterialTextView appointmentRequest;

    public fragment_user_dashboard() {

    }

    public static fragment_user_dashboard newInstance() {
        fragment_user_dashboard fragment = new fragment_user_dashboard();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_dashboard, container, false);
        levelCard = view.findViewById(R.id.level_card);
        level = view.findViewById(R.id.level);
        nextLevel = view.findViewById(R.id.next_level);
        levelIndicator = view.findViewById(R.id.level_indicator);
        pointsCard = view.findViewById(R.id.points_card);
        points = view.findViewById(R.id.points);
        redeemPointsButton = view.findViewById(R.id.redeem_points_button);
        followingCard = view.findViewById(R.id.following_card);
        following = view.findViewById(R.id.following);
        followingPercent = view.findViewById(R.id.following_percent);
        followerCard = view.findViewById(R.id.follower_card);
        followerPercent = view.findViewById(R.id.follower_percent);
        followers = view.findViewById(R.id.followers);
        salesCard = view.findViewById(R.id.sales_card);
        sales = view.findViewById(R.id.sales);
        salesButton = view.findViewById(R.id.sales_button);
        upcomingAppointmentCard = view.findViewById(R.id.upcoming_appointment_card);
        upcomingAppointment = view.findViewById(R.id.upcoming_appointments);
        appointmentRequestCard = view.findViewById(R.id.appointment_request_card);
        appointmentRequest = view.findViewById(R.id.appointment_request);
        setFields();
        setUserLevel();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setFields() {
        following.setText("" + user.totalFollowing);
        followers.setText("" + user.totalFollowers);
    }

    private void setUserLevel() {
        level.setText(Utilities.getUserLevelName());
        nextLevel.setText(Utilities.getNextLevelName());
        levelIndicator.setMax(Utilities.getMaxLevel());
        levelIndicator.setProgressCompat(user.totalFollowers, true);
        levelIndicator.setProgress(user.totalFollowers);
    }
}