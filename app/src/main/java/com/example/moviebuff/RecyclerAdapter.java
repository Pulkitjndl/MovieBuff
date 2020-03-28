package com.example.moviebuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.example.moviebuff.DataModel.Results;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private ArrayList<Results> results;
    private Context context;

    public RecyclerAdapter(Context context,ArrayList<Results> results) {
        this.results = results;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item,viewGroup,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        String posterPath=results.get(position).getPosterPath();

        Glide.with(context)
                .load(posterPath)
                .placeholder(R.drawable.spin_load)
                .into(holder.posterImage);

        //Handling display orientation

        int left=dpToPx(12);
        int right=dpToPx(12);
        int top=dpToPx(6);
        int bottom=dpToPx(6);
        int spanCount=2;

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCount=3;
            left=dpToPx(18);
            right=dpToPx(18);
            top=dpToPx(10);
            bottom=dpToPx(10);

            boolean isFirst3Item=position<spanCount;
            boolean isLast3Item=position>getItemCount()-spanCount;
            boolean isLeftSide=(position+1) % spanCount !=0;
            boolean isRightSide=!isLeftSide;

            if(isFirst3Item){
                top=dpToPx(18);
            }
            if(isLast3Item){
                bottom=dpToPx(18);
            }
            if(isLeftSide){
                right=dpToPx(10);
            }
            if(isRightSide){
                left=dpToPx(10);
            }

        }else {
            boolean isFirst2Item=position<spanCount;
            boolean isLast2Item=position>getItemCount()-spanCount;
            boolean isLeftSide=(position+1) % spanCount !=0;
            boolean isRightSide=!isLeftSide;

            if(isFirst2Item){
                top=dpToPx(12);
            }
            if(isLast2Item){
                bottom=dpToPx(12);
            }
            if(isLeftSide){
                right=dpToPx(6);
            }
            if(isRightSide){
                left=dpToPx(6);
            }
        }


        FrameLayout.LayoutParams layoutParams=(FrameLayout.LayoutParams)holder.cardView.getLayoutParams();
        layoutParams.setMargins(left,top,right,bottom);
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutParams.height=dpToPx(380);
        }
        holder.cardView.setLayoutParams(layoutParams);


        //Handling recycler item click event

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,DetailActivity.class);
                ActivityOptionsCompat options = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    options=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                            holder.posterImage, ViewCompat.getTransitionName(holder.posterImage));
                }
                Bundle bundle=new Bundle();
                bundle.putInt("position",position);
                bundle.putParcelableArrayList("movie_list",results);
                intent.putExtras(bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    context.startActivity(intent,options.toBundle());
                }
            }
        });
    }

    //Converting Dp to Pixel

    private int dpToPx(int dp){
        float px=dp*context.getResources().getDisplayMetrics().density;
        return (int) px;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    //View Holder

    class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView posterImage;

        RecyclerViewHolder(View itemView){
            super(itemView);
            cardView=(CardView)itemView.findViewById(R.id.card_view);
            posterImage=(ImageView) itemView.findViewById(R.id.poster_image);
        }
    }
}
