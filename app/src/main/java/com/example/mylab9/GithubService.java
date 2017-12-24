package com.example.mylab9;

import java.util.List;
import rx.Observable;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by 王杏婷 on 2017/12/19.
 */
//建立Github API接口
public interface GithubService {
    @GET("/users/{user}")
    Observable<Github> getUser(@Path("user") String user);

    @GET("/users/{user}/repos")
    Observable<List<Detail>> getUserRepos(@Path("user") String user);
}
