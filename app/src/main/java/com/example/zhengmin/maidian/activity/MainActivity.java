package com.example.zhengmin.maidian.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zhengmin.maidian.ApiService;
import com.example.zhengmin.maidian.BaseApplication;
import com.example.zhengmin.maidian.R;
import com.example.zhengmin.maidian.adapter.MovieNormalAdapter;
import com.example.zhengmin.maidian.aop.HookHelper;
import com.example.zhengmin.maidian.model.MovieSubject;
import com.example.zhengmin.maidian.model.Movies;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends Activity {
    private ListView mListView;
    private List<MovieSubject> mData ;
    private MovieNormalAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        mListView = (ListView)  findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("data",mData.get(i));
                MainActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestData();
            }
        });
        requestData();
        try {
            try {
                HookHelper.hookActivity(this);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ((BaseApplication)(this.getApplication())).unbindService();
        }
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
                mAdapter = new MovieNormalAdapter(mData);
                mListView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {

            }
        });
    }
}
