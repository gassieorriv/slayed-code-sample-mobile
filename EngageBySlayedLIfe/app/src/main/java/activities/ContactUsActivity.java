package activities;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.view.menu.ActionMenuItemView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.slayed.life.engage.R;
import models.support.SupportNote;
import web.interfaces.SupportInterface;

public class ContactUsActivity  extends BaseActivity {

    private TextInputEditText note;
    private LinearProgressIndicator progressIndicator;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        setupDeadEndMenu("Contact Us");
        note = findViewById(R.id.note);
        progressIndicator = findViewById(R.id.progress_bar);
        context = this;
        setSave();
    }

    @SuppressLint("RestrictedApi")
    private void setSave() {
        ActionMenuItemView saveItem = deadEndAppBar.findViewById(R.id.save);
        saveItem.setTitle("Submit");
        deadEndAppBar.setOnMenuItemClickListener(item -> {
            Submit();
            return  false;
        });
    }

    public void Submit() {
        if(validateForm()) {
            progressIndicator.show();
            SupportInterface supportInterface = CreateSupportNote();
            SupportNote supportNote = new SupportNote();
            if(note != null && note.getText() != null) {
                supportNote.note = note.getText().toString();
            }
            supportNote.resolved = false;
            supportInterface.CreateSupportUserNote(supportNote);
        } else {
            Toast.makeText(this, "Note cannot be left blank.", Toast.LENGTH_SHORT).show();
        }
    }

    SupportInterface CreateSupportNote() {
        return new SupportInterface(obj -> {
            progressIndicator.hide();
           if(obj != null && !obj.toString().equals("-1")) {
                finish();
           } else {
               Toast.makeText(context, "Oops! Unable to submit a note at this time. Try reaching us another way.", Toast.LENGTH_SHORT).show();
           }
        });
    }

    private boolean validateForm() {
        return note.getText() == null || note.getText().toString().length() > 0;
    }

    public void Instagram(View view) {
        String instagramUrl = "https://www.instagram.com/slayed.life";
        String instagramPackageUrl = "http://instagram.com/_u/slayed.life";
        Uri uri = Uri.parse(instagramPackageUrl);
        Intent instagramIntent = new Intent(Intent.ACTION_VIEW, uri);
        String instagramPackage = "com.instagram.android";
        instagramIntent.setPackage(instagramPackage);

        try {
            startActivity(instagramIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl)));
        }
    }

    public void Twitter(View view) {
        String twitterAccount = "LifeSlayed";
        try {
             startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitterAccount)));
        }catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitterAccount)));
        }
    }

    public void Email(View view) {
        String email = "admin@slayed.life";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        startActivity(intent);
    }
}
