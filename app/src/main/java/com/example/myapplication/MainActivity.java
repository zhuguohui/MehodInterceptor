package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.handler.confirm.Confirm;
import com.example.myapplication.handler.login.LoginRequest;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_btn).setOnClickListener(v -> {
            commit(MainActivity.this);
        });

        findViewById(R.id.tv_comment).setOnClickListener(v->{
            comment(this);
        });

        findViewById(R.id.tv_delete).setOnClickListener(v->{
            delete(this);
        });
    }


    @Confirm(value = "确定执行提交操作",showToast = true)
    public   void commit(Context context) {
        Toast.makeText(context, "签发成功", Toast.LENGTH_SHORT).show();
    }

    @Test("aa")
    @LoginRequest
    public   void comment(Context context) {
        Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
    }

    @Confirm("确定执行删除操作")
    @LoginRequest
    public   void delete(Context context) {
        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
    }






}