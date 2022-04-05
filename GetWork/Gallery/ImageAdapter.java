package com.shiranaor.GetWork.Gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.shiranaor.GetWork.Callbacks.Callback_ReadData;
import com.shiranaor.GetWork.Callbacks.Callback_Success;
import com.shiranaor.GetWork.MyFireBase.FireBase_Auth;
import com.shiranaor.GetWork.MyFireBase.FireBase_RealTime;
import com.shiranaor.GetWork.R;
import com.shiranaor.GetWork.data.model.Employee;
import com.shiranaor.GetWork.data.model.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;
// This adapter handles the initialize of each row in  the list view of all of the images on gallery

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private View view;

    public ImageAdapter(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        // check if user can delete this image

        FireBase_RealTime.isEmployee(new Callback_ReadData() {
            @Override
            public void success(Object data) {
                Boolean isEmployee = (Boolean) data;

                if (isEmployee ) {
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FireBase_RealTime.deleteUpload(uploadCurrent.getUploadsId(), uploadCurrent.getId(), new Callback_Success() {
                                @Override
                                public void success(String message) {
                                    FireBase_RealTime.deleteImage(uploadCurrent.getImageUrl());
                                    mUploads.remove(position);
                                    ImageAdapter.this.notifyItemRemoved(position);
                                    ImageAdapter.this.notifyItemRangeChanged(position,mUploads.size());
                                }

                                @Override
                                public void failed(String message) {

                                }
                            });
                        }
                    });
                } else {
                    holder.delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void failed(String message) {

            }
        });

        // load the image to the same position every time
        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    // Find all views of this line on the list view
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;
        public TextView delete;
        public LinearLayout image_line;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
            delete = itemView.findViewById(R.id.text_view_delete);
            image_line = itemView.findViewById(R.id.image_line);
        }
    }
}