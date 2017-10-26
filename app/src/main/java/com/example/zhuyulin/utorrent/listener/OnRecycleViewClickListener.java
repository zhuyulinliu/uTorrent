package com.example.zhuyulin.utorrent.listener;

/**
 * Created by zhuyulin on 2017/10/21.
 */

public interface OnRecycleViewClickListener {
    void getFileList(int position);
    void start(int position);
    void stop(int position);
    void pause(int position);
    void remove(int position);
}
