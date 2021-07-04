package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_btn).setOnClickListener(v -> {
            test2(MainActivity.this);
        });

    }


    @Confirm(value = "确定执行提交操作",showToast = true)
    @Test("zzz")
    public   void test2(Context context) {
        Toast.makeText(context, "签发成功", Toast.LENGTH_SHORT).show();
        throw new RuntimeException("测试崩溃");
    }


    public   void test1(Context context) {
        Toast.makeText(context, "签发成功", Toast.LENGTH_SHORT).show();
        throw new RuntimeException("测试崩溃");
    }

    @Confirm(value = "确定执行刪除操作",showToast = true)
    public   void test2(View view) {
        Toast.makeText(view.getContext(), "签发成功", Toast.LENGTH_SHORT).show();
    }


   /* public void delete(View view,View view2) {
      //  test(view);
        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
    }*/

/*    private void test(View v, float floatValue,String[] arg1,String[][] arg2,String[][][] arg3,int[][] arg4,int arg5,boolean arg6){
        String str=new String();
        List<String> strList=new ArrayList<>();
        String[] a=new String[]{};
        String[][] aa=new String[][]{};
        String[][][] aaa=new String[][][]{};
        int[][] ints=new int[][]{};


        Log.i("zzz","class str="+str.getClass().getName());
        Log.i("zzz","class strList="+strList.getClass().getName());
        Log.i("zzz","class a="+a.getClass().getName());
        Log.i("zzz","class aa="+aa.getClass().getName());
        Log.i("zzz","class aaa="+aaa.getClass().getName());
        Log.i("zzz","class ints="+ints.getClass().getName());
        Log.i("zzz","class arg5="+int.class.getName());
        Log.i("zzz","class arg6="+boolean.class.getName());

    }*/

   /* protected void realCall(Context context) {
        try {
            Method method=null;
            String methodName="delete";
            String annotationValue="{\"showToast\":\"true\",\"value\":\"确定执行提交操作\"}";
            Method[] declaredMethods = getClass().getDeclaredMethods();
            for(int i=0;i<declaredMethods.length;i++){
                if(declaredMethods[i].getName().equals(methodName)){
                    method=declaredMethods[i];
                    break;
                }
            }
            if(method==null){
                throw new RuntimeException("don't fond method  ["+methodName+"] in class ["+this.getClass().getName()+"]");
            }
            ConfirmUtil.doConfirm(annotationValue,this,method,context);
        } catch (Exception e) {
            try {
                ConfirmUtil.callError(this,e);
            }catch (Exception e2) {
                e2.printStackTrace();
            }

        }
    }*/

}