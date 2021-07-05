package com.example.myapplication.handler.confirm;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;

import java.lang.reflect.Method;

/**
 * Created by zhuguohui
 * Date: 2021/7/2
 * Time: 14:31
 * Desc:
 */
public class ConfirmUtil {

  static class ConfirmValue{
      String value;
      boolean showToast;

      public String getValue() {
          return value;
      }

      public void setValue(String value) {
          this.value = value;
      }

      public boolean isShowToast() {
          return showToast;
      }

      public void setShowToast(boolean showToast) {
          this.showToast = showToast;
      }
  }

    public static void onMethodIntercepted(String annotationJson, Object caller, Method method, Object... objects) {

        Context context = (Context) caller;
        ConfirmValue cv=new Gson().fromJson(annotationJson,ConfirmValue.class);
        new AlertDialog.Builder(context)
                .setTitle(cv.value)
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
