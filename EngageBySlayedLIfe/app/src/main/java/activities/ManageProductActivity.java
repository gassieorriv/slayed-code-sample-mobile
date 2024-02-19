package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.gson.Gson;
import com.slayed.life.engage.R;
import org.json.JSONArray;

import java.util.Objects;

import adapters.ProductListViewAdapter;
import models.shop.Product;
import web.interfaces.ShopInterface;

public class ManageProductActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);
        recyclerView = findViewById(R.id.product_list);
        context = this;
        setupDeadEndMenu("Manage Products");
        setAdd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProducts();
    }

    public void getProducts() {
        ShopInterface shopInterface = getUserProducts();
        shopInterface.GetProducts(0, 20);
    }

    @SuppressLint("RestrictedApi")
    private void setAdd() {
        ActionMenuItemView saveItem = deadEndAppBar.findViewById(R.id.save);
        saveItem.setTitle("Add");
        deadEndAppBar.setOnMenuItemClickListener(item -> {
            NewProduct();
            return  false;
        });
    }

    private void NewProduct() {
        Intent intent = new Intent(context, NewProductActivity.class);
        startActivity(intent);
    }

    private ShopInterface getUserProducts() {
        return  new ShopInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                JSONArray data = new JSONArray(obj.toString());
                Product[] products = new Gson().fromJson(data.toString(), Product[].class);
                ProductListViewAdapter adapter = new ProductListViewAdapter(this, products);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(context, "Error loading products",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            Product product = ((ProductListViewAdapter) Objects.requireNonNull(recyclerView.getAdapter())).getProduct();
            product.deleted = true;
            ShopInterface shop = deleteProduct();
            shop.UpdateProduct(product);
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    private ShopInterface deleteProduct() {
        return new ShopInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                getProducts();
            } else {
                Toast.makeText(context, "Error deleting product. please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
