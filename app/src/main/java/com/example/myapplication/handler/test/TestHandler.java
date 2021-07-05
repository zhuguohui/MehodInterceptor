package com.example.myapplication.handler.test;

import android.content.Context;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by zhuguohui
 * Date: 2021/7/2
 * Time: 14:31
 * Desc:
 */
public class TestHandler {


    public static void onMethodIntercepted(String info, Object caller, Method method, Object... objects) {
        Context context = (Context) caller;
        Toast.makeText(context, "测试成功", Toast.LENGTH_SHORT).show();
        try {
            method.setAccessible(true);
            method.invoke(caller, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void onMethodInterceptedError(Object caller, Exception e) {
        e.printStackTrace();
        Toast.makeText((Context) caller, "处理登录失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }


}
