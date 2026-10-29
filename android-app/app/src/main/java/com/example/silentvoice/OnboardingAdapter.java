package com.example.silentvoice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {

    List<Integer> lightImages;
    List<Integer> darkImages;
    List<String> descriptions;
    boolean isDark;

    public OnboardingAdapter(List<Integer> lightImages, List<Integer> darkImages, List<String> descriptions, boolean isDark) {
        this.lightImages = lightImages;
        this.darkImages = darkImages;
        this.descriptions = descriptions;
        this.isDark = isDark;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
       // TextView desc;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imgSlide);
            //desc = view.findViewById(R.id.txtSlideDesc);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_slide, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.img.setImageResource(isDark ? darkImages.get(position) : lightImages.get(position));
      //  holder.desc.setText(descriptions.get(position));

        if (isDark) {
         //   holder.desc.setTextColor(0xFFD1D5DB);
        } else {
        //    holder.desc.setTextColor(0xFF6B7280);
        }
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }
}