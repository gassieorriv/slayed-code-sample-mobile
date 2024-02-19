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
import adapters.ServiceListViewAdapter;
import models.shop.Service;
import web.interfaces.ShopInterface;

public class ManageServiceActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_service);
        context = this;
        recyclerView = findViewById(R.id.service_list);
        setupDeadEndMenu("Manage Services");
        setAdd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getServices();
    }

    public void getServices() {
        ShopInterface shopInterface = getUserServices();
        shopInterface.GetServices(0, 20);
    }

    @SuppressLint("RestrictedApi")
    private void setAdd() {
        ActionMenuItemView saveItem = deadEndAppBar.findViewById(R.id.save);
        saveItem.setTitle("Add");
        deadEndAppBar.setOnMenuItemClickListener(item -> {
            NewService();
            return  false;
        });
    }

    private void NewService() {
        Intent intent = new Intent(context, NewServiceActivity  .class);
        startActivity(intent);
    }

    private ShopInterface getUserServices() {
        return  new ShopInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                JSONArray data = new JSONArray(obj.toString());
                Service[] services = new Gson().fromJson(data.toString(), Service[].class);
                ServiceListViewAdapter adapter = new ServiceListViewAdapter(this, services);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(context, "Error loading services",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            Service service = ((ServiceListViewAdapter) Objects.requireNonNull(recyclerView.getAdapter())).getService();
            service.deleted = true;
            ShopInterface shop = deleteService();
            shop.UpdateService(service);
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    private ShopInterface deleteService() {
        return new ShopInterface(obj -> {
            if(obj != null && !obj.toString().equals("-1")) {
                getServices();
            } else {
                Toast.makeText(context, "Error deleting service. please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
