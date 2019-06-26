package com.example.zhengmin.maidian.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zhengmin.maidian.R;
import com.example.zhengmin.maidian.model.MovieSubject;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by zhengmin on 2018/3/13.
 */

public class MovieNormalAdapter extends BaseAdapter {
    private List<MovieSubject> mMovieList;

    public MovieNormalAdapter(List<MovieSubject>  data){
        mMovieList = data;
    }



    @Override
    public int getCount()  {
        return mMovieList.size();
    }

    @Override
    public Object getItem(int i) {
        return mMovieList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh ;
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item,viewGroup,false);
            vh = new ViewHolder(view);
            view.setTag(vh);
        }
        else{
            vh = (ViewHolder)view.getTag();
        }

        vh.movieTitle.setText(mMovieList.get(i).title);
        return view;
    }

    class ViewHolder{
        TextView movieTitle;
        public ViewHolder(View view) {
            movieTitle = (TextView) view.findViewById(R.id.textViewTitle);
        }
    }
}

