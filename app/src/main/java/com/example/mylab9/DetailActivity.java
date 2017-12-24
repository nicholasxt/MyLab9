package com.example.mylab9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 王杏婷 on 2017/12/17.
 */

public class DetailActivity extends AppCompatActivity {

    String loginName = "";
    private GithubService githubService;
    ProgressBar detailProgress;
    RecyclerView detailList;
    List<Map<String, Object>> DetailList = new ArrayList<>();
    CommonAdapter DetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailList = (RecyclerView) findViewById(R.id.detail_list);
        detailProgress = findViewById(R.id.detail_progress);
        Retrofit GithubRetrofit = retrofitFactory.createRetrofit("https://api.github.com/");
        githubService = GithubRetrofit.create(GithubService.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            loginName = extras.getString("name");
        }
        detailList.setLayoutManager(new LinearLayoutManager(this));
        DetailAdapter = new CommonAdapter(this,R.layout.detail_item_layout,DetailList) {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> s) {
                TextView name = holder.getView(R.id.repo_name);
                name.setText(s.get("name").toString());
                TextView language = holder.getView(R.id.repo_language);
                language.setText(s.get("language").toString());
                TextView description = holder.getView(R.id.repo_description);
                description.setText(s.get("description").toString());
            }
        };
        detailList.setAdapter(DetailAdapter);

        githubService.getUserRepos(loginName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Detail>>() {
                    @Override
                    public void onCompleted() {
                        detailProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this,e.getMessage()+"确认你搜索的用户存在",Toast.LENGTH_LONG).show();
                        detailProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(List<Detail> details) {
                        for (int i = 0; i< details.size(); i++){
                            DetailAdapter.addData(details.get(i));
                        }
                    }
                });
    }//end of create
}
