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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;
import com.slayed.life.engage.R;
import activities.NewProductActivity;
import models.shop.Product;
import com.bumptech.glide.Glide;

public class ProductListViewAdapter extends RecyclerView.Adapter<ProductListViewAdapter.ViewHolder>{

    public Context context;
    public Product[] userProduct;
    private ChipGroup SizeGroup;
    public int position;
    public ProductListViewAdapter(Context context, Product[] userProduct) {
        this.context = context;
        this.userProduct = userProduct;
    }

    public Object getItem(int position) {
        return userProduct[position];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public View view;
        public ViewHolder(View v) {
            super(v);
            v.setOnCreateContextMenuListener(this);
            view = v;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAbsoluteAdapterPosition(), v.getId(), 0, "Delete");
        }
    }

    @NonNull
    @Override
    public ProductListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_listview, parent, false);
        return new ProductListViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListViewAdapter.ViewHolder holder, int position) {
        Product product = (Product) getItem(position);
        ImageView productImage = holder.view.findViewById(R.id.product_image);
        MaterialCardView productCardView = holder.view.findViewById(R.id.product_card);
        SizeGroup = holder.view.findViewById(R.id.size_group);
        productImage.setImageURI(Uri.parse(product.images));
        MaterialTextView productName = holder.view.findViewById(R.id.product_name);
        MaterialTextView productDescription = holder.view.findViewById(R.id.product_description);
               productName.setText(product.name);
        productDescription.setText(product.description);
        productCardView.setOnLongClickListener(v -> false);
        Glide.with(context).load(product.images).into(productImage);
        addChipToGroup(product.price.toString());
        holder.view.setOnLongClickListener(v -> {
            setPosition(holder.getAbsoluteAdapterPosition());
            return false;
        });
        productCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewProductActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    private void setPosition(int position) {
        this.position = position;
    }

    public Product getProduct() { return userProduct[position]; }

    @Override
    public long getItemId(int position) {
        return userProduct[position].id;
    }

    @Override
    public int getItemCount() {
        return userProduct.length;
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

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.view.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }
}
