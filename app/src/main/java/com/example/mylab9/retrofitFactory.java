package com.example.mylab9;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 王杏婷 on 2017/12/19.
 */
//创建Retrofit客户端的方法
public class retrofitFactory {
    public static Retrofit createRetrofit(String baseUrl){
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())//数据转换的接口
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//消息处理的回调接口
                .client(createOKHttp())
                .build();
    }
    public static OkHttpClient createOKHttp(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }
}
