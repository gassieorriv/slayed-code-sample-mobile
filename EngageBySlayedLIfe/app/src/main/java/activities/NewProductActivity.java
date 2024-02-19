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
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import org.json.JSONArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import Base.BaseEngage;
import models.shop.Product;
import models.shop.ProductSize;
import models.shop.StateTax;
import web.interfaces.ShopInterface;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static br.com.zbra.androidlinq.Linq.stream;

public class NewProductActivity extends BaseActivity {

    private TextInputEditText Name;
    private TextInputEditText Description;
    private TextInputEditText Sku;
    private TextInputEditText Size;
    private TextInputEditText Price;
    private TextInputEditText Shipping;
    private TextInputEditText Discount;
    private TextInputLayout States;
    private TextInputLayout ShippingType;
    private TextInputLayout DiscountType;
    private ImageView ProductImage;
    private TableLayout SizeTableLayout;
    private Context context;
    private LinearProgressIndicator progressIndicator;
    private File file;
    private String filePath;
    private String fileName;
    private final String fileType = "PRODUCT";
    private String bucket;
    private Product existingProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        Name = findViewById(R.id.product_name);
        Description = findViewById(R.id.product_description);
        Sku = findViewById(R.id.sku);
        Size = findViewById(R.id.sizes);
        Price = findViewById(R.id.price);
        Shipping = findViewById(R.id.shipping);
        SizeTableLayout = findViewById(R.id.sizeTableLayout);
        States = findViewById(R.id.states);
        ShippingType = findViewById(R.id.shipping_type);
        DiscountType = findViewById(R.id.discount_type);
        Discount = findViewById(R.id.discount);
        ProductImage = findViewById(R.id.product_image);
        progressIndicator = findViewById(R.id.progress_bar);
        context = this;
        bucket = BaseEngage.getAwsBucket(context);
        existingProduct = (Product)getIntent().getSerializableExtra("product");
        setupDeadEndMenu("Add Product");
        setSave();
        setupSize();
        setupStates();
        setupImage();
        setupShippingType();
        setupDiscountType();
        if(existingProduct != null) {
            setupEdit();
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void addSize(String sizeText, String quantityText, int id) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = null;

        if (inflater != null) {
            view = inflater.inflate(R.layout.layout_size_quantity, SizeTableLayout, false);
        }

        if (view != null) {
            MaterialTextView productSize = view.findViewById(R.id.product_size);
            TextInputLayout quantity = view.findViewById(R.id.product_quantity);
            productSize.setText(sizeText);
            if(!quantityText.equals("")) {
                productSize.setTag(id);
                quantity.getEditText().setText(quantityText);
            } else {
                productSize.setTag(-1);
            }
            view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.addView(view);
            SizeTableLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            ImageView close = view.findViewById(R.id.product_size_close);
            View finalView = view;
            close.setOnClickListener(v -> {
                row.removeView(finalView);
                SizeTableLayout.removeView(finalView);
            });
        }
    }

    private void setupSize() {
        Size.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(Size != null && Size.getText() != null && Size.getText().length() > 0) {
                    addSize(Size.getText().toString(), "", -1);
                    Size.setText("");
                }
                return true;
            }
            return false;
        });
    }

    private void setupStates() {
        if (BaseEngage.StateTaxes == null) {
            ShopInterface shopInterface = getTaxes();
            shopInterface.GetStateTaxes();
        } else if(existingProduct != null) {
            setState();
        }

        List<String> stateList = Arrays.asList(getResources().getStringArray(R.array.states_array));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.layout_listview, stateList);
        ((AutoCompleteTextView)States.getEditText()).setAdapter(adapter);
    }

    private void setState() {
        String text = stream(BaseEngage.StateTaxes).where(x -> x.id == existingProduct.taxId).first().name;
        States.getEditText().setText(text);
        States.requestLayout();
    }

    private void setupImage() {
        ProductImage.setOnClickListener(v -> SelectProductImage());
    }

    private void setupShippingType() {
        String[] shippingTypeList = new String[] {"%", "$"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.layout_listview, shippingTypeList);
        ((AutoCompleteTextView)ShippingType.getEditText()).setAdapter(adapter);
    }

    private void setupDiscountType() {
        String[] discountTypeList = new String[] {"%", "$"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.layout_listview, discountTypeList);
        ((AutoCompleteTextView)DiscountType.getEditText()).setAdapter(adapter);
    }

    private void setupExistingSize() {
        new Handler().postDelayed(() -> {
            for (int i = 0; i < existingProduct.productSize.size(); i++) {
                ProductSize productSize = existingProduct.productSize.get(i);
                addSize(productSize.size, String.valueOf(productSize.quantity), productSize.id);
            }
        }, 100);
    }

    @SuppressLint("DefaultLocale")
    private void setupEdit() {
        if(existingProduct != null) {
            if (existingProduct.name != null) {
                Name.setText(existingProduct.name);
            }

            if(existingProduct.description != null) {
                Description.setText(existingProduct.description);
            }

            if(existingProduct.sku != null) {
                Sku.setText(existingProduct.sku);
                Sku.setEnabled(false);
            }

            if(existingProduct.price != null) {
                Price.setText(String.format("%.2f", existingProduct.price));
            }

            if(existingProduct.shipping != null && existingProduct.shipping > 0) {
                Shipping.setText(String.valueOf(existingProduct.shipping));
                ShippingType.getEditText().setText(existingProduct.shippingType);
            }

            if(existingProduct.discount != null && existingProduct.discount > 0) {
                Discount.setText(String.valueOf(existingProduct.discount));
                DiscountType.getEditText().setText(existingProduct.discountType);
            }

            if(existingProduct.images != null) {
                ProductImage.setAdjustViewBounds(true);
                Glide.with(context).load(existingProduct.images).into(ProductImage);
            }

            if(existingProduct.productSize.size() > 0) {
                setupExistingSize();
            }
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
            ShopInterface productInterface = CreateProduct();
            Product product = new Product();
            product.name = Name.getText().toString();
            product.userId = BaseEngage.user.id;
            product.active = true;
            product.description = Description.getText().toString();
            product.sku = Sku.getText().toString();
            filePath = "product/" + product.userId + "/" + product.sku + "/";
            fileName = "product.png";
            product.images = BaseEngage.baseAWSUrl + bucket + "/" + filePath + fileName;
            if (Discount.getText().length() > 0) {
                product.discount = Float.parseFloat(Discount.getText().toString());
            } else {
                product.discount = null;
            }
            product.price = Float.parseFloat(Price.getText().toString());
            product.shipping = Float.parseFloat(Shipping.getText().toString());
            product.shippingType = ShippingType.getEditText().getText().toString();
            product.discountType = DiscountType.getEditText().getText().toString();
            product.productSize = new ArrayList<>();
            for (int i = 0; i < SizeTableLayout.getChildCount(); i++) {
                TextInputLayout quantity = SizeTableLayout.getChildAt(i).findViewById(R.id.product_quantity);
                MaterialTextView size = SizeTableLayout.getChildAt(i).findViewById(R.id.product_size);
                if(size != null && quantity != null) {
                    ProductSize productSize = new ProductSize();
                    productSize.size = size.getText().toString();
                    productSize.quantity = Integer.parseInt(quantity.getEditText().getText().toString());
                    productSize.active = true;
                    int id = (int) size.getTag();
                    if (id > 0) {
                        productSize.id = id;
                        productSize.productId = existingProduct.id;
                    }
                    product.productSize.add(productSize);
                }
            }

            if(existingProduct != null) {
                for(int j = 0; j < existingProduct.productSize.size(); j++) {
                    ProductSize x = existingProduct.productSize.get(j);
                    boolean hasValue = false;
                    for(int i = 0; i < product.productSize.size(); i++) {
                        if(x.id == product.productSize.get(i).id) {
                            hasValue = true;
                        }
                    }
                    if(!hasValue) {
                        x.active = false;
                        product.productSize.add(x);
                    }
                }
            }
            product.taxId = stream(BaseEngage.StateTaxes).where(x -> x.name.equals(States.getEditText().getText().toString())).first().id;

            if(existingProduct != null) {
                product.id = existingProduct.id;
                productInterface.UpdateProduct(product);
            } else {
                productInterface.CreateProduct(product);
            }
        } else {
            Toast.makeText(context, "Make sure all required fields are filled out.", Toast.LENGTH_SHORT).show();
        }
    }

    public ShopInterface CreateProduct() {
        return new ShopInterface(obj -> {
            progressIndicator.setVisibility(View.GONE);
            if(obj != null && !obj.toString().equals("-1")) {

                if(filePath != null && file != null) {
                    BaseEngage.uploadWithTransferUtility(context, filePath, fileName, file, fileType);
                }

                if(existingProduct != null) {
                    Toast.makeText(context, "Product Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Product Created", Toast.LENGTH_SHORT).show();
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
               if(existingProduct != null && existingProduct.taxId > 0) {
                   new Handler().postDelayed(this::setState, 100);
               }
           } else {
               Toast.makeText(context, "Could not get taxes", Toast.LENGTH_SHORT).show();
           }
        });
    }

    public void SelectProductImage() {
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

        if(Shipping != null && Shipping.getText().toString().equals("")) {
            result = false;
        } else if(ShippingType.getEditText().getText().toString().equals("")) {
            result = false;
        }

        if(Discount != null && !Discount.getText().toString().equals("")) {
            if(DiscountType.getEditText().getText().toString().equals("")) {
                result = false;
            }
        }

        if(SizeTableLayout.getChildCount() <= 0) {
            return false;
        }

        if(States != null && States.getEditText().getText().toString().equals("")) {
            return  false;
        }

        return  result;
    }

    ActivityResultLauncher<Intent>  CropLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result != null && result.getData() != null && result.getData().hasExtra("File")) {
                    file = (File) Objects.requireNonNull(result.getData().getExtras()).get("File");
                    ProductImage.setAdjustViewBounds(true);
                    ProductImage.setImageURI(null);
                    ProductImage.setImageURI(Uri.fromFile(file));
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
