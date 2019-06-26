package com.example.zhengmin.maidian.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhengmin on 2018/3/13.
 */

public class MovieSubject implements Serializable{
    public String title;
    public String id;
    public String original_title;
    public String alt;
    public String year;
    public String subtype;
    public MovieImage images;

    public class MovieImage implements Serializable{
        public String small;
        public String large ;
        public String medium;
    }
}
