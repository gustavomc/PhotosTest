package com.example.gustavo.photostest.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gustavo.photostest.R;
import com.example.gustavo.photostest.models.ListItem;

import java.util.List;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ViewHolder> {

    private List<ListItem> listPhotos;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ListItem item);
    }

    public PhotoListAdapter(List<ListItem> listPhotos) {
        this.listPhotos = listPhotos;
    }

    @Override
    public PhotoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (listPhotos.get(position).getName() != null)
            viewHolder.tvName.setText(listPhotos.get(position).getName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            PhotoListAdapter $this = PhotoListAdapter.this;

            @Override
            public void onClick(View v) {
                if ($this.mItemClickListener != null) {
                    $this.mItemClickListener.onItemClick(listPhotos.get(position));
                }
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvName = (TextView) itemLayoutView.findViewById(R.id.tv_name);
        }
    }

    @Override
    public int getItemCount() {
        return listPhotos.size();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}