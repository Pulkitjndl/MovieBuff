package com.example.moviebuff;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;


import com.example.moviebuff.Api.ApiService;
import com.example.moviebuff.DataModel.MovieResult;
import com.example.moviebuff.DataModel.Results;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    int cacheSize = 10 * 1024 * 1024; // 10 MB
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Results> movieResult;
    public static String BASE_URL="https://api.themoviedb.org";
    public static String category="popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }
    public Activity getActivity(){
        Context context = this;
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


    //initializing views
    private void initViews(){
        pd=new ProgressDialog(this);
        pd.setMessage("Fetching Data..");
        pd.setCancelable(false);
        pd.show();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        movieResult = new ArrayList<>();
        adapter = new RecyclerAdapter(MainActivity.this, movieResult);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                initViews();
                loadData();
            }
        });

    }

    //fetching data from api

    private void loadData(){

            try{
                Cache cache = new Cache(getCacheDir(), cacheSize);

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cache(cache)
                        .addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Interceptor.Chain chain)
                                    throws IOException {
                                Request request = chain.request();
                                if (!isNetworkAvailable()) {
                                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale \
                                    request = request
                                            .newBuilder()
                                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                            .build();
                                }
                                return chain.proceed(request);
                            }
                        })
                        .build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService apiService = retrofit.create(ApiService.class);

                Call<MovieResult> call= apiService.getMovies(category,BuildConfig.ApiKey);
                call.enqueue(new Callback<MovieResult>() {
                    @Override
                    public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                        MovieResult movieResult=response.body();
                        ArrayList<Results> results=movieResult.getResults();
                        recyclerView.setAdapter(new RecyclerAdapter(getActivity(), results));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeContainer.isRefreshing()){
                            swipeContainer.setRefreshing(false);
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<MovieResult> call, Throwable t) {
                        Log.e("ResponseError",t.getMessage());
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

            }catch (Exception e){
                Log.d("Error", e.getMessage());
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
    }

    //checking active network state

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }
}
