package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grpc.Contract;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.activities.FullscreenImageActivity;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;

public class MenuItemImagesAdapter extends RecyclerView.Adapter<MenuItemImagesAdapter.ViewHolder> {

    private Data data;
    private String menuName;
    private List<Integer> imageIds;

    public MenuItemImagesAdapter(Context context, List<Integer> imageIds, String menuName) {
        this.imageIds = imageIds;
        this.menuName = menuName;
        data = (Data) context.getApplicationContext();
    }

    public void addImage(Integer imageId) {
        imageIds.add(imageId);
        notifyItemChanged(imageIds.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Integer imageId = imageIds.get(position);
        Bitmap bitmap = data.getImage(imageId);
        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setVisibility(View.VISIBLE);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FullscreenImageActivity.class);
                Bundle extras = new Bundle();
                extras.putString("menuName", menuName);
                extras.putInt("imageId", imageId);
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageIds.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final View view;
        ImageView imageView;


        ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            imageView = view.findViewById(R.id.itemImageView);
        }
    }
}
