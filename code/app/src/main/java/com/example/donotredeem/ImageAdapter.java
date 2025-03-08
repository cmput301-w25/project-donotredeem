package com.example.donotredeem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter class for displaying images in a RecyclerView with infinite scrolling.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<Integer> imageList;

    /**
     * Constructs an ImageAdapter with the provided list of image resources.
     *
     * @param imageList List of drawable resource IDs representing images.
     */
    public ImageAdapter(List<Integer> imageList) {
        this.imageList = imageList;
    }

    /**
     * Creates a new ViewHolder instance when needed.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new instance of ImageViewHolder.
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * Binds image data to the ViewHolder at the given position.
     *
     * @param holder   The ViewHolder to update.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        int realPosition = position % imageList.size(); // Keep looping through images
        holder.imageView.setImageResource(imageList.get(realPosition));
    }

    /**
     * Returns the number of items in the adapter.
     *
     * @return The maximum integer value to create an infinite scrolling effect.
     */
    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // Infinite loop
    }

    /**
     * ViewHolder class that holds the image view for each item in the RecyclerView.
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        
        /**
         * Constructs an ImageViewHolder and initializes the ImageView.
         *
         * @param itemView The View representing an individual item.
         */
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.moodImage); // Change ID to match your XML
        }
    }
}
