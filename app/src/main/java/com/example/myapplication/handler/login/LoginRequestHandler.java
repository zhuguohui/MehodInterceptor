package com.example.myapplication.handler.login;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication.LoginActivity;

import java.lang.reflect.Method;

/**
 * Created by zhuguohui
 * Date: 2021/7/2
 * Time: 14:31
 * Desc:
 */
public class LoginRequestHandler {


    public static void onMethodIntercepted(String info, Object caller, Method method, Object... objects) {
        Context context = (Context) caller;
        if (LoginConfig.haveLogin) {
            try {
                method.setAccessible(true);
                method.invoke(caller, objects);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "请登录", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, LoginActivity.class));
        }

    }


    public static void onMethodInterceptedError(Object caller, Exception e) {
        e.printStackTrace();
        Toast.makeText((Context) caller, "处理登录失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }


}
