package com.example.myapplication.handler.login;

import android.content.Context;
import android.content.Intent;

import com.example.myapplication.LoginActivity;

/**
 * Created by zhuguohui
 * Date: 2021/7/5
 * Time: 16:24
 * Desc:
 */
public class LoginConfig {
    public static boolean haveLogin = false;
    static OnLoginStateChangeListener listener;

   public interface OnLoginStateChangeListener{
        void onStateChange();
    }

    public static void requestLogin(Context context,OnLoginStateChangeListener onLoginStateChangeListener){
       listener=onLoginStateChangeListener;
       context.startActivity(new Intent(context, LoginActivity.class));
    }

    public static void onStateChange(){
       if(listener!=null){
           listener.onStateChange();
           listener=null;//移除
       }
    }
}
