# MehodInterceptor
MehodInterceptor是一个使用ASM来动态修改字节码，以达到方法拦截。通过该框架，可以控制某个方法是否执行。

比如某些业务有一些通用的判断逻辑：比如弹出确认提示，判断用户是否登录，判断APP是否具有某些权限。只有这些判断通过，才会执行该方法。否则不执行。

这些通用的逻辑，现在可以通过注解的方式添加到方法上。

比如这样：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210707163259372.png)
不再需要写其他的代码，最后的效果是这样的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2021070716355187.gif)

# 使用
该框架已经发布到 mavenCentral()了。只需要在根目录的gradle集成。

```java
buildscript {
    repositories {
        google()
        //框架所在的中央仓库
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:4.1.0"
        //本框架
        classpath 'io.github.zhuguohui:method-interceptor:1.0.1'

    }
}
```

在需要使用的module中如下配置即可

```java

apply plugin:"com.zhuguohui.methodinterceptor"

methodInterceptor {
    include= ["com.example.myapplication"]
    handlers= [
            //处理提醒
            "com.example.myapplication.handler.confirm.Confirm":"com.example.myapplication.handler.confirm.ConfirmUtil",
            //处理登录
            "com.example.myapplication.handler.login.RequestLogin":"com.example.myapplication.handler.login.LoginRequestHandler",
            //处理权限
           "com.example.myapplication.handler.permission.RequestPermission":"com.example.myapplication.handler.permission.PermissionRequestHandler",
            //test
           "com.example.myapplication.handler.test.Test":"com.example.myapplication.handler.test.TestHandler"]
}
```
## 参数说明
|  |  |
|--|--|
|  include|  传入你想处理的类所在的包名本质上是一个set，可以传入多个 |
|handlers|传入一个map，key是你定义的注解,value是注解的处理器。如果一个方法被该注解注释，就修改字节码，在改方法被执行的时候。将该方法传递给处理器。由处理控制执行|

## 示例

### 注解
注解中定义值，这些值在方法被调用的时候会被格式化成JSON数据返回给处理器。
```java

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Confirm {
    String value();
}

```
处理器必须有两个静态方法。**onMethodIntercepted** 和 **onMethodInterceptedError**

**方法参数**
|  |  |
|--|--|
|annotationJson  | 被格式化成JSON的注解中的值 |
|caller|方法的拥有者，方法声明在那个类中，就会传递该类的this指针|
|method|被注解注释的方法|
|objects|方法执行的所有参数|


### 处理器

```java
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

```
# 注意事项

 1. 目前注解不能修饰静态方法  （后续考虑支持）
 2. 目前方法参数不支持基本类型 (后续会考虑支持)
 3. 函数不能有返回值，需要返回的可以通过传递接口利用回调实现 (使用场景决定的)
 
 如果是基本类型，需要使用对应的包装类型。比如
 

```java
  //不支持
    public void add(int a,int b){
      
    }

    //支持
    public void add(Integer a,Integer b){

    }
```

# 调试
因为涉及到字节码，如果感觉生成的方法不对可以build以后再这个位置查看生成的代码。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210707165714147.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzIyNzA2NTE1,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210707165751280.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzIyNzA2NTE1,size_16,color_FFFFFF,t_70)

# 原理

简单的说一句如果一个方法被如下注释

```java
  @RequestLogin
    public void comment(Context context) {
        Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
    }
```

通过ASM 会把正在的方法 comment 复制成一个其他名字的方法。


```java
  private void _confirm_index46_commit(Context context) {
        Toast.makeText(context, "签发成功", 0).show();
    }
```
而原来的方法会被改造成这样

```cpp
public void comment(Context context) {
        try {
            Method var9 = null;
            String var3 = "_requestlogin_index48_comment";
            String var4 = "{}";
            Method[] var5 = this.getClass().getDeclaredMethods();

            for(int var6 = 0; var6 < var5.length; ++var6) {
                if (var5[var6].getName().equals(var3)) {
                    var9 = var5[var6];
                    break;
                }
            }

            if (var9 == null) {
                throw new RuntimeException("don't find method  [" + var3 + "] in class [" + this.getClass().getName() + "]");
            }

            LoginRequestHandler.onMethodIntercepted(var4, this, var9, new Object[]{context});
        } catch (Exception var8) {
            Exception var2 = var8;

            try {
                LoginRequestHandler.onMethodInterceptedError(this, var2);
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

    }
```

当然以上的逻辑是可以叠加的，也就是注解可以叠加使用。

```java
   @Confirm("确定执行分享操作")
    @RequestLogin
    @RequestPermission( {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION})
    public void share(Context context) {
        Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
    }
```

最后真正的方法是这样

```java
private void _requestpermission_index54__requestlogin_index53__confirm_index52_share(Context context) {
        Toast.makeText(context, "分享成功", 0).show();
    }
```







