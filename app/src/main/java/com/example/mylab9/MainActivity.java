package com.example.mylab9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Button clear_btn,fetch_btn;
    EditText editText;
    RecyclerView recyclerView;
    ProgressBar waitProgress;
    SharedPreferences sp;
    public static int MODE = MODE_PRIVATE;
    public static String PREFERENCE_NAME = "search_history";
    List<Map<String,Object>> GithubList = new ArrayList<>();
    CommonAdapter GithubAdapter;
    private GithubService githubService;
    private int cntLogin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(PREFERENCE_NAME,MODE);
        //如果存在则打开它，否则创建新的Preferences
        if (sp.getString("cntLogin","").equals("")){
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("cntLogin",Integer.toString(0));//存放数据
            editor.commit();//完成提交
        }else {
            cntLogin = Integer.parseInt(sp.getString("cntLogin","0"));
        }
        init();
        Retrofit GithubRetrofit = retrofitFactory.createRetrofit("https://api.github.com/");
        githubService = GithubRetrofit.create(GithubService.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GithubAdapter = new CommonAdapter(this,R.layout.main_item_layout,GithubList) {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> s) {
                TextView name = holder.getView(R.id.item_name);
                name.setText(s.get("name").toString());
                TextView id = holder.getView(R.id.item_id);
                id.setText(s.get("id").toString());
                TextView blog = holder.getView(R.id.item_blog);
                blog.setText(s.get("blog").toString());
            }
        };
        recyclerView.setAdapter(GithubAdapter);
        GithubAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("name",GithubAdapter.getData(position,"name"));
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(int position) {
                GithubAdapter.removeData(position);
                return true;
            }
        });
        initList();
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GithubAdapter.clearData();
                SharedPreferences.Editor editor = sp.edit();
                cntLogin = 0;
                editor.clear();//清空
                editor.commit();
            }
        });
        fetch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = editText.getText().toString();
                waitProgress.setVisibility(View.VISIBLE);
                githubService.getUser(user)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Github>() {//订阅者
                            @Override
                            public void onCompleted() {
                                waitProgress.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(MainActivity.this,e.getMessage()+"确认你搜索的用户存在",Toast.LENGTH_LONG).show();
                                waitProgress.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onNext(Github github) {
                                GithubAdapter.addData(github);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString(Integer.toString(cntLogin),github.getLogin());
                                cntLogin += 1;
                                editor.putString("cntLogin",Integer.toString(cntLogin));
                                editor.commit();
                            }
                        });
            }
        });

    }//end Create
    private void init(){
        clear_btn = (Button) findViewById(R.id.clear_btn);
        fetch_btn = (Button) findViewById(R.id.fetch_btn);
        editText = (EditText) findViewById(R.id.editText);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        waitProgress = findViewById(R.id.progress);
    }
    void initList(){
        for (int i = 0; i<cntLogin;i++){
            String user = sp.getString(Integer.toString(i),"");
            waitProgress.setVisibility(View.VISIBLE);
            githubService.getUser(user)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Github>() {
                        @Override
                        public void onCompleted() {
                            waitProgress.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(MainActivity.this,e.getMessage()+"确认你搜索的用户存在",Toast.LENGTH_LONG).show();
                            waitProgress.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onNext(Github github) {
                            GithubAdapter.addData(github);
                        }
                    });
        }
    }
}
