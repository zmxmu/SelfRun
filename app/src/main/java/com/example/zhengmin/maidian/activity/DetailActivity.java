package com.example.zhengmin.maidian.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhengmin.maidian.model.MovieSubject;
import com.example.zhengmin.maidian.R;
import com.squareup.picasso.Picasso;

public class DetailActivity extends Activity {
    private MovieSubject mData;
    private TextView mTitleTv;
    private TextView mIdTv;
    private TextView mOriginalTitleTv;
    private TextView mAltTv;
    private TextView mYearTv;
    private TextView mSubTypeTv;
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mData = (MovieSubject)getIntent().getSerializableExtra("data");
        mTitleTv = (TextView)findViewById(R.id.titleTv);
        mIdTv = (TextView)findViewById(R.id.idTv);
        mOriginalTitleTv = (TextView)findViewById(R.id.original_titleTv);
        mAltTv = (TextView)findViewById(R.id.altTv);
        mYearTv = (TextView)findViewById(R.id.yearTv);
        mSubTypeTv = (TextView)findViewById(R.id.subTypeTv);
        mImageView = (ImageView) findViewById(R.id.imageIV);
        if(mData!=null){
            mTitleTv.setText(mData.title);
            mIdTv.setText(mData.id);
            mOriginalTitleTv.setText(mData.original_title);
            mAltTv.setText(mData.alt);
            mYearTv.setText(mData.year);
            mSubTypeTv.setText(mData.subtype);
            Picasso.with(DetailActivity.this).load(mData.images.large).into(mImageView);
        }

    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }
}
