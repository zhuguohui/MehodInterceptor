package com.example.myapplication.handler;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.lang.reflect.Method;

/**
 * Created by zhuguohui
 * Date: 2021/7/2
 * Time: 14:31
 * Desc:
 */
public class ConfirmUtil {


    public static void onMethodIntercepted(String info, Object caller, Method method, Object... objects) {
        Context context = (Context) caller;
        new AlertDialog.Builder(context)
                .setTitle(info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            method.setAccessible(true);
                            method.invoke(caller,objects);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


    public static void onMethodInterceptedError(Object caller,Exception e){
        e.printStackTrace();
        Toast.makeText((Context) caller,"处理注解失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
    }




}
