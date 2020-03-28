package com.example.moviebuff;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moviebuff.DataModel.Results;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<Results> arraylist;
    private Integer position;
    private TextView movieName;
    private TextView rating;
    private TextView date;
    private TextView overview;
    private ImageView imgMovie;
    private ImageView pImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        initViews();

        //getting movies list object from intent extras
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
             arraylist= bundle.getParcelableArrayList("movie_list");
             position=bundle.getInt("position");
        }

        //setting up toolbar
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(arraylist.get(position).getTitle());
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        //Displaying data into views

        movieName.setText(arraylist.get(position).getTitle());
        rating.setText(String.valueOf(arraylist.get(position).getVoteAverage()));
        date.setText(arraylist.get(position).getReleaseDate());
        overview.setText(arraylist.get(position).getOverview());

        Glide.with(this)
                .load(arraylist.get(position).getBackdropPath())
                .placeholder(R.drawable.spin_load)
                .into(pImage);

        Glide.with(this)
                .load(arraylist.get(position).getPosterPath())
                .placeholder(R.drawable.spin_load)
                .into(imgMovie);
    }

    //Initializing Views
    private void initViews() {
        movieName=(TextView)findViewById(R.id.movie_name);
        rating=(TextView)findViewById(R.id.movie_rating);
        date=(TextView)findViewById(R.id.movie_date);
        imgMovie=(ImageView)findViewById(R.id.img_movie);
        pImage=(ImageView)findViewById(R.id.pImage);
        overview=(TextView)findViewById(R.id.overview);
    }
}
