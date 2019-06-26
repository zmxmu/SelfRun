package com.example.zhengmin.maidian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.example.zhengmin.maidian.ApiService;
import com.example.zhengmin.maidian.BaseApplication;
import com.example.zhengmin.maidian.R;
import com.example.zhengmin.maidian.adapter.MovieAdapter;
import com.example.zhengmin.maidian.model.MovieSubject;
import com.example.zhengmin.maidian.model.Movies;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RecycleViewActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<MovieSubject> mData ;
    private MovieAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView)  findViewById(R.id.recycleView);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        StaggeredGridLayoutManager sgLayoutmanager = new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sgLayoutmanager);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestData();
            }
        });
        requestData();
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }
    void requestData(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.douban.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<Movies> call = apiService.requestComingSoonMovies();
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                if(response!=null && response.body()!=null ){
                    mData = response.body().subjects;
                }
                mAdapter = new MovieAdapter(mData);
                mAdapter.setOnItemClickListener(new MovieAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        Intent i = new Intent(RecycleViewActivity.this,DetailActivity.class);
                        i.putExtra("data",mData.get(position));
                        RecycleViewActivity.this.startActivity(i);
                    }

                    @Override
                    public void onLongClick(int position) {

                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {

            }
        });
    }

}
