package com.voyager.helper.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.voyager.helper.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private List<String> images;
    private List<String> imagesName;
    protected PhotoListner photoListner;


    public GalleryAdapter(Context context, List<String> images,List<String> imagesName, PhotoListner photoListner) {
        this.context = context;
        this.images = images;
        this.photoListner = photoListner;
        this.imagesName=imagesName;
    }

    public GalleryAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_individual_gallery,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        String image=images.get(position);
        Glide.with(context)
                .load(images.get(position))
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ImageView_individual)
        ;

        holder.fileName.setText(imagesName.get(position));

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoListner.onShareButtonClicked("Share");
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ImageView_individual;
        TextView fileName;
        ImageButton shareButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageView_individual = itemView.findViewById(R.id.image_gallery);
            fileName=itemView.findViewById(R.id.file_name);
            shareButton=itemView.findViewById(R.id.share_button);

        }
    }

    public interface PhotoListner {
        void onShareButtonClicked(String path);
    }

}
