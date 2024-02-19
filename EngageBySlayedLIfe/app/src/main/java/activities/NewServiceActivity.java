package activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import org.json.JSONArray;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import Base.BaseEngage;
import models.shop.Service;
import models.shop.StateTax;
import web.interfaces.ShopInterface;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static br.com.zbra.androidlinq.Linq.stream;

public class NewServiceActivity extends BaseActivity {

    private TextInputEditText Name;
    private TextInputEditText Description;
    private TextInputEditText Price;
    private TextInputEditText Discount;
    private TextInputLayout States;
    private TextInputLayout Duration;
    private TextInputLayout DiscountType;
    private ImageView ServiceImage;
    private Context context;
    private LinearProgressIndicator progressIndicator;
    private File file;
    private String filePath;
    private String fileName;
    private final String fileType = "Service";
    private String bucket;
    private Service existingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);
        Name = findViewById(R.id.service_name);
        Description = findViewById(R.id.service_description);
        Price = findViewById(R.id.price);
        States = findViewById(R.id.states);
        DiscountType = findViewById(R.id.discount_type);
        Discount = findViewById(R.id.discount);
        ServiceImage = findViewById(R.id.service_image);
        progressIndicator = findViewById(R.id.progress_bar);
        Duration = findViewById(R.id.duration);
        context = this;
        bucket = BaseEngage.getAwsBucket(context);
        existingService = (Service) getIntent().getSerializableExtra("service");
        setupDeadEndMenu("Add Service");
        setSave();
        setupStates();
        setupDuration();
        setupImage();
        setupDiscountType();
        if(existingService != null) {
            setupEdit();
        }
    }

    private void setupStates() {
        if (BaseEngage.StateTaxes == null) {
            ShopInterface shopInterface = getTaxes();
            shopInterface.GetStateTaxes();
        } else if(existingService != null) {
            setState();
        }

        List<String> stateList = Arrays.asList(getResources().getStringArray(R.array.states_array));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.layout_listview, stateList);
        ((AutoCompleteTextView)States.getEditText()).setAdapter(adapter);
    }

    private void setState() {
        String text = stream(BaseEngage.StateTaxes).where(x -> x.id == existingService.taxId).first().name;
        States.getEditText().setText(text);
        States.requestLayout();
    }

    private void setupDuration() {
        List<String> stateList = Arrays.asList(getResources().getStringArray(R.array.time_array));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.layout_listview, stateList);
        ((AutoCompleteTextView) Duration.getEditText()).setAdapter(adapter);
    }

    private void setupImage() {
        ServiceImage.setOnClickListener(v -> SelectServiceImage());
    }

    private void setupDiscountType() {
        String[] discountTypeList = new String[] {"%", "$"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.layout_listview, discountTypeList);
        ((AutoCompleteTextView)DiscountType.getEditText()).setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    private void setupEdit() {
        if(existingService != null) {
            if (existingService.name != null) {
                Name.setText(existingService.name);
            }

            if(existingService.description != null) {
                Description.setText(existingService.description);
            }

            if(existingService.price != null) {
                Price.setText(String.format("%.2f", existingService.price));
            }

            if(existingService.discount != null && existingService.discount > 0) {
                Discount.setText(String.valueOf(existingService.discount));
                DiscountType.getEditText().setText(existingService.discountType);
            }

            if(existingService.images != null) {
                ServiceImage.setAdjustViewBounds(true);
                Glide.with(context).load(existingService.images).into(ServiceImage);
            }

            Duration.getEditText().setText(getDurationValue(existingService.duration));
        }
    }

    private void setSave() {
        deadEndAppBar.setOnMenuItemClickListener(item -> {
            Submit();
            return  false;
        });
    }

    public void Submit() {
        if (validateForm()) {
            progressIndicator.setVisibility(View.VISIBLE);
            ShopInterface serviceInterface = CreateService();
            Service service = new Service();
            service.name = Name.getText().toString();
            service.userId = BaseEngage.user.id;
            service.active = true;
            service.description = Description.getText().toString();
            if(existingService == null) {
                filePath = "service/" + service.userId + "/" + new Date().getTime() + "/";
                fileName = "service.png";
                service.images = BaseEngage.baseAWSUrl + bucket + "/" + filePath + fileName;
            } else {
                service.images = existingService.images;
            }
            if (Discount.getText().length() > 0) {
                service.discount = Float.parseFloat(Discount.getText().toString());
            } else {
                service.discount = null;
            }
            service.price = Float.parseFloat(Price.getText().toString());
            service.discountType = DiscountType.getEditText().getText().toString();
            service.taxId = stream(BaseEngage.StateTaxes).where(x -> x.name.equals(States.getEditText().getText().toString())).first().id;
            service.duration = parseDuration();
            if(existingService != null) {
                service.id = existingService.id;
                serviceInterface.UpdateService(service);
            } else {
                serviceInterface.CreateService(service);
            }
        } else {
            Toast.makeText(context, "Make sure all required fields are filled out.", Toast.LENGTH_SHORT).show();
        }
    }

    public ShopInterface CreateService() {
        return new ShopInterface(obj -> {
            progressIndicator.setVisibility(View.GONE);
            if(obj != null && !obj.toString().equals("-1")) {

                if(filePath != null && file != null) {
                    BaseEngage.uploadWithTransferUtility(context, filePath, fileName, file, fileType);
                }

                if(existingService != null) {
                    Toast.makeText(context, "Service Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Service Created", Toast.LENGTH_SHORT).show();
                }

                finish();
            } else {
                Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ShopInterface getTaxes() {
        return new ShopInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                JSONArray data = new JSONArray(obj.toString());
                BaseEngage.StateTaxes = new Gson().fromJson(data.toString(), StateTax[].class);
                if(existingService != null && existingService.taxId > 0) {
                    new Handler().postDelayed(this::setState, 100);
                }
            } else {
                Toast.makeText(context, "Could not get taxes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void SelectServiceImage() {
        if (ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermissionLauncher.launch(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE});
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            FilePickerLauncher.launch(intent);
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if(Name != null && Name.getText().toString().equals("")) {
            result = false;
        }

        if(Description != null && Description.getText().toString().equals("")) {
            result = false;
        }

        if(Price != null && Price.getText().toString().equals("")) {
            result = false;
        }

        if(Discount != null && !Discount.getText().toString().equals("")) {
            if(DiscountType.getEditText().getText().toString().equals("")) {
                result = false;
            }
        }

        if(States != null && States.getEditText().getText().toString().equals("")) {
            return  false;
        }

        return  result;
    }

    private int parseDuration() {
        switch (Duration.getEditText().getText().toString())
        {
            case "15 minutes":
                return  15;
            case "30 minutes":
                return 30;
            case "45 minutes":
                return 45;
            case "1 hour":
                return 60;
            case "1–1/2 hour":
                return 90;
            case "2 hours":
                return 120;
            case "2-1/2 hours":
                return 150;
            case "3 hours":
                return 180;
            case "Half Day":
                return 240;
            case "All Day":
                return 480;
            default:
                return  30;
        }
    }

    private String getDurationValue(int duration) {
        switch (duration)
        {
            case 15:
            return "15 minutes";
            case 30:
            return "30 minutes";
            case 45:
            return "45 minutes";
            case 60:
            return "1 hour";
            case 90:
            return "1–1/2 hour";
            case 120:
            return "2 hours";
            case 150:
            return "2-1/2 hours";
            case 180:
            return "3 hours";
            case 240:
            return "Half Day";
            case 480:
            return "All Day";
            default:
                return "";
        }
    }

    ActivityResultLauncher<Intent>  CropLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result != null && result.getData() != null && result.getData().hasExtra("File")) {
                    file = (File) Objects.requireNonNull(result.getData().getExtras()).get("File");
                    ServiceImage.setAdjustViewBounds(true);
                    ServiceImage.setImageURI(null);
                    ServiceImage.setImageURI(Uri.fromFile(file));
                }
            });

    ActivityResultLauncher<Intent>  FilePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    final Uri selectedContent = result.getData().getData();
                    final DisplayMetrics displayMetrics = new DisplayMetrics();
                    if (selectedContent != null) {
                        String filePath = BaseEngage.getPath(context, selectedContent);
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        Intent intent = new Intent(this, CropActivity.class);
                        if(filePath != null) {
                            final File file = new File(filePath);
                            intent.putExtra("File", file);
                            intent.putExtra("removeAspect", true);
                            intent.putExtra("height", 275);
                            intent.putExtra("width", 200);
                            intent.putExtra("type", 1);
                            CropLauncher.launch(intent);
                        } else {
                            Toast.makeText(context, "An error occurred uploading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private final ActivityResultLauncher<String[]> requestStoragePermissionLauncher
            = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
        boolean readPermission = isGranted.get(READ_EXTERNAL_STORAGE);
        boolean writePermission = isGranted.get(WRITE_EXTERNAL_STORAGE);
        if (readPermission && writePermission) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            FilePickerLauncher.launch(intent);
        } else {
            Toast.makeText(context, "Permission must be granted to select photo", Toast.LENGTH_SHORT).show();
        }
    });
}