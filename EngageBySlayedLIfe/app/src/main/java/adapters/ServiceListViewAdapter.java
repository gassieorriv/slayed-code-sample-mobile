package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;
import com.slayed.life.engage.R;
import activities.NewServiceActivity;
import models.shop.Service;

public class ServiceListViewAdapter extends RecyclerView.Adapter<ServiceListViewAdapter.ViewHolder> {

    public Context context;
    public Service[] userService;
    public int position;
    private ChipGroup SizeGroup;
    public ServiceListViewAdapter(Context context, Service[] userService) {
        this.context = context;
        this.userService = userService;
    }

    public Object getItem(int position) {
        return userService[position];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAbsoluteAdapterPosition(), v.getId(), 0, "Delete");
        }
    }

    @NonNull
    @Override
    public ServiceListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_listview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceListViewAdapter.ViewHolder holder, int position) {
        Service service = (Service) getItem(position);
        ImageView productImage = holder.view.findViewById(R.id.product_image);
        MaterialCardView productCardView = holder.view.findViewById(R.id.product_card);
        SizeGroup = holder.view.findViewById(R.id.size_group);
        productImage.setImageURI(Uri.parse(service.images));
        MaterialTextView productName = holder.view.findViewById(R.id.product_name);
        MaterialTextView productDescription = holder.view.findViewById(R.id.product_description);
        productName.setText(service.name);
        productDescription.setText(service.description);
        productCardView.setOnLongClickListener(v -> false);
        Glide.with(context).load(service.images).into(productImage);
        addChipToGroup(service.price.toString());
        holder.view.setOnLongClickListener(v -> {
            setPosition(holder.getAbsoluteAdapterPosition());
            return false;
        });
        productCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewServiceActivity.class);
            intent.putExtra("service", service);
            context.startActivity(intent);
        });
    }

    private void setPosition(int position) {
        this.position = position;
    }

    public Service getService() { return userService[position]; }

    @Override
    public long getItemId(int position) {
        return userService[position].id;
    }

    @Override
    public int getItemCount() {
        return userService.length;
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void addChipToGroup(String sizeText) {
        Chip chip = new Chip(context);
        chip.setText(sizeText);
        chip.setTextColor(context.getResources().getColor(R.color.white));
        chip.setChipIcon(ContextCompat.getDrawable(context, R.drawable.ic_launcher_background));
        chip.setChipIconVisible(false);
        chip.setCloseIconVisible(false);
        chip.setChipBackgroundColor(context.getResources().getColorStateList(R.color.blue_100));
        SizeGroup.addView(chip);
    }
}
