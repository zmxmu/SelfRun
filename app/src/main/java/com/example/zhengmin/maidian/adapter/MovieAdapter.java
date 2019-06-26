package com.example.zhengmin.maidian.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhengmin.maidian.R;
import com.example.zhengmin.maidian.model.MovieSubject;

import java.util.List;

/**
 * Created by zhengmin on 2018/3/13.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<MovieSubject> mMovieList;
    private OnItemClickListener mOnItemClickListener;
    public MovieAdapter(List<MovieSubject>  data){
        mMovieList = data;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MovieSubject item  = mMovieList.get(position);
        holder.movieTitle.setText(item.title);
        if( mOnItemClickListener!= null){
            holder.itemView.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });

            holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView movieTitle;

        public ViewHolder(View view) {
            super(view);
            movieTitle = (TextView) view.findViewById(R.id.textViewTitle);
        }
    }
    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener=onItemClickListener;
    }
}

