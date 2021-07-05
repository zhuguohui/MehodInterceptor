package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.handler.confirm.Confirm;
import com.example.myapplication.handler.login.RequestLogin;
import com.example.myapplication.handler.permission.RequestPermission;
import com.example.myapplication.handler.test.Test;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_commit).setOnClickListener(v -> {
            commit(MainActivity.this);
        });

        findViewById(R.id.tv_comment).setOnClickListener(v -> {
            comment(this);
        });

        findViewById(R.id.tv_delete).setOnClickListener(v -> {
            delete(this);
        });

        findViewById(R.id.tv_share).setOnClickListener(v->{
            share(this);
        });
    }

    @Confirm(value = "确定执行提交操作")
    public void commit(Context context) {
        Toast.makeText(context, "签发成功", Toast.LENGTH_SHORT).show();
    }

    @RequestLogin
    public void comment(Context context) {
        Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
    }

    @RequestPermission( {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION})
    public void delete(Context context) {
        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
    }

    @Confirm("确定执行分享操作")
    @RequestLogin
    @RequestPermission( {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION})
    public void share(Context context) {
        Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
    }


}