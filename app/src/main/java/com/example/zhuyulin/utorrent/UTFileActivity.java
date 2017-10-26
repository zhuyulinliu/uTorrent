package com.example.zhuyulin.utorrent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import com.example.zhuyulin.utorrent.adapter.TFileAdapter;
import com.example.zhuyulin.utorrent.adapter.WrapContentLinearLayoutManager;
import com.example.zhuyulin.utorrent.listener.OnCheckBoxChangeListener;
import com.example.zhuyulin.utorrent.model.TFile;
import com.example.zhuyulin.utorrent.utils.OkHttpUtils;
import com.example.zhuyulin.utorrent.utils.TorrentUtils;
import com.example.zhuyulin.utorrent.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UTFileActivity extends AppCompatActivity {
    private String hash;

    private List<TFile> fileList = new ArrayList<>();
    private TFileAdapter adapter;
    private OnCheckBoxChangeListener listener;

    @BindView(R.id.recyclerView_tfile)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.utorrent_activity_utfile);
        ButterKnife.bind(this);

        toolbar.setLogo(R.drawable.ic_utorrent_logo);
        toolbar.setTitle("文件列表");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        hash = intent.getStringExtra("hash");

        adapter = new TFileAdapter(fileList);
        initListener();
        adapter.setListener(listener);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        initData();
    }


    private void initData() {
        fileList.clear();
        if (TextUtils.isEmpty(hash)){
            finish();
            return;
        }
        String url = new UrlUtils().setAction("getfiles").setHashList(Arrays.asList(hash)).toString();
        OkHttpUtils.getInstance().setUrl(url).build()
                .execute(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.w("UTFileActivity",responseData);
                        try{
                            JSONObject jsonObject = new JSONObject(responseData);
                            String files = jsonObject.getString("files");
                            fileList.addAll(TorrentUtils.parseFiles(files));
                            Log.w("UTFileActivity","fileList"+fileList.size());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
    }
    private void initListener() {
        listener = new OnCheckBoxChangeListener() {
            @Override
            public void checked(int position) {
                String url = new UrlUtils().setAction("setprio").setHashList(Arrays.asList(hash)).setP(2).setf(position).toString();
                OkHttpUtils.getInstance().setUrl(url).build()
                        .execute(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (!TextUtils.isEmpty(response.body().string())) {
                                    try {
                                        Thread.sleep(1000);
                                    }catch (InterruptedException e){}
                                    getData();
                                }
                            }
                        });
            }

            @Override
            public void unchecked(int position) {
                String url = new UrlUtils().setAction("setprio").setHashList(Arrays.asList(hash)).setP(0).setf(position).toString();
                OkHttpUtils.getInstance().setUrl(url).build()
                        .execute(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (!TextUtils.isEmpty(response.body().string())) {
                                    try {
                                        Thread.sleep(1000);
                                    }catch (InterruptedException e){}
                                    getData();
                                }
                            }
                        });
            }
        };
    }

    private void getData() {
        if (TextUtils.isEmpty(hash)){
            finish();
            return;
        }
        String url = new UrlUtils().setAction("getfiles").setHashList(Arrays.asList(hash)).toString();
        OkHttpUtils.getInstance().setUrl(url).build()
                .execute(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.w("UTFileActivity",responseData);
                        try{
                            JSONObject jsonObject = new JSONObject(responseData);
                            String files = jsonObject.getString("files");
                            List<TFile> newList =  TorrentUtils.parseFiles(files);
                            for (int i = 0; i < newList.size() ; i++){
                                fileList.get(i).setTFile(newList.get(i));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
    }
}
