package activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.slayed.life.engage.R;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import Base.BaseEngage;

public class CropActivity extends AppCompatActivity {

    private File file;
    private Context context;
    private CropImageView cropImageView;
    private int width;
    private int height;
    private int type;
    private boolean removeFixedAspectRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_crop);
        file = (File) getIntent().getExtras().get("File");
        width = getIntent().getIntExtra("width", 200);
        height = getIntent().getIntExtra("height", 200);
        type = getIntent().getIntExtra("type", 0);
        removeFixedAspectRatio = getIntent().getBooleanExtra("removeAspect",false);
        cropImageView = findViewById(R.id.cropImageView);

        if (file != null)
            setupPage();
        else
            finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == 2000) {
            setResult(2000);
            finish();
        }
    }

    public void setupPage() {
        if (getIntent().hasExtra("File")) {
            cropImageView.setImageUriAsync(Uri.fromFile(file));
            if (type == 0) {
                cropImageView.setCropShape(CropImageView.CropShape.OVAL);
            }
            if(!removeFixedAspectRatio) {
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(width, height);
            } else {
                cropImageView.setFixedAspectRatio(false);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void CropImage(View view) {
        Bitmap image = cropImageView.getCroppedImage();
        try {
            long time = Calendar.getInstance().getTimeInMillis();
            String FileName = "event_" + time;

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            File realFile = new java.io.File(context
                    .getApplicationContext().getFileStreamPath(FileName + ".jpg")
                    .getPath());
            realFile.createNewFile();
            realFile.setWritable(true);
            realFile.setReadable(true);

            FileOutputStream fo = new FileOutputStream(realFile);
            fo.write(outStream.toByteArray());
            fo.close();


            Intent responseIntent = new Intent();
            responseIntent.putExtra("File", realFile);
            setResult(3130, responseIntent);
            finish();
        } catch (Exception e) {
            Toast.makeText(context, "Error Cropping image", Toast.LENGTH_SHORT).show();
        }
    }

    public void RotateImage(View view) {
        file = BaseEngage.RotateImage(file.getName(), this, file);
        cropImageView.setImageUriAsync(Uri.fromFile(file));
        if(!removeFixedAspectRatio) {
            cropImageView.setFixedAspectRatio(true);
            cropImageView.setAspectRatio(width, height);
        }
    }
}
