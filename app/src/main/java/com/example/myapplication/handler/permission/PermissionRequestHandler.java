package com.example.myapplication.handler.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.handler.login.LoginConfig;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Method;

import io.reactivex.functions.Consumer;

/**
 * Created by zhuguohui
 * Date: 2021/7/2
 * Time: 14:31
 * Desc:
 */
public class PermissionRequestHandler {

    static class PermissionValue{
        String[] value;

        public String[] getPermissions() {
            return value;
        }

        public void setPermissions(String[] permissions) {
            this.value = permissions;
        }
    }

    public static void onMethodIntercepted(String info, Object caller, Method method, Object... objects) {
        Context context = (Context) caller;
        RxPermissions rxPermissions=new RxPermissions((Activity) context);
        PermissionValue pv=new Gson().fromJson(info,PermissionValue.class);
        rxPermissions.request(pv.value)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if(aBoolean){
                            try {
                                method.setAccessible(true);
                                method.invoke(caller, objects);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(context, "权限不足 功能无法使用", Toast.LENGTH_SHORT).show();
                        }
                    }
                },throwable -> {
                    throwable.printStackTrace();
                });
    }


    public static void onMethodInterceptedError(Object caller, Exception e) {
        e.printStackTrace();
        Toast.makeText((Context) caller, "处理权限获取失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }


}
