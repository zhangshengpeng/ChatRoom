package cn.edu.zjicm.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.IOException;

import cn.edu.zjicm.Permission.PermissionUtils;
import cn.edu.zjicm.Utils.SharedPreferencesUtils;
import cn.edu.zjicm.toos.Result;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InformationInActivity extends Activity {
    private TextView login_insert;
    private TextView in_login;
    private Button login;
    private EditText login_usename;
    private EditText login_pasword;
    private EditText inpassword;
    private Button insert;
    private EditText incname;
    private EditText inusername;
    private ConstraintLayout insetConstraintLayout;
    private ConstraintLayout loginConstraintLayout;
    public static int flag = 0;
    private LinearLayout linearLayout;
    private String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
        setContentView(R.layout.information_in_activity);
        init();
    }

    private void init() {
       login_insert = (TextView)findViewById(R.id.lgin_insert);
       in_login = (TextView)findViewById(R.id.in_login);
       login_usename = (EditText)findViewById(R.id.login_username);
       login_pasword = (EditText)findViewById(R.id.lgoin_password);
       login = (Button)findViewById(R.id.login);
       insert = (Button)findViewById(R.id.insert);
       incname = (EditText)findViewById(R.id.ic_niscname);
       inusername =(EditText)findViewById(R.id.in_username);
       inpassword = (EditText)findViewById(R.id.in_password);
       login_usename.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               login_pasword.requestFocus();
               return true;
           }
       });
       login_pasword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               return false;
           }
       });
       incname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               inusername.requestFocus();
               return true;
           }
       });
       inusername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               inpassword.requestFocus();
               return true;
           }
       });
       inpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               return false;
           }
       });
       insetConstraintLayout = (ConstraintLayout)findViewById(R.id.insert_constraintLayout);
       loginConstraintLayout = (ConstraintLayout)findViewById(R.id.login_constraintLayout);
       login_insert.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               insetConstraintLayout.setVisibility(View.VISIBLE);
               loginConstraintLayout.setVisibility(View.GONE);
           }
       });
       in_login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               insetConstraintLayout.setVisibility(View.GONE);
               loginConstraintLayout.setVisibility(View.VISIBLE);
           }
       });

       insert.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (inpassword.getText().toString().equals("")||inusername.getText().toString().equals("")){
                   Toast toast = Toast.makeText(InformationInActivity.this,"账户和密码不能空",Toast.LENGTH_LONG);
                   toast.setGravity(Gravity.CENTER,0,0);
                   toast.show();
               }else {
                   postInsert();
               }
           }
       });
       login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (login_usename.getText().toString().equals("")||login_pasword.getText().toString().equals("")){
                   Toast toast = Toast.makeText(InformationInActivity.this,"账户和密码不能空",Toast.LENGTH_LONG);
                   toast.setGravity(Gravity.CENTER,0,0);
                   toast.show();
               }else {
                   postLgion();
               }

           }
       });
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String[] permissionsNeedCheck = PermissionUtils.checkPermission(this, permissions);
        if (permissionsNeedCheck != null) {
            PermissionUtils.grantPermission(this, permissionsNeedCheck, PermissionUtils.REQUEST_GRANT_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS);
        }
    }
    private void postInsert(){
        OkHttpClient client = new OkHttpClient();
        final RequestBody requestBody = new FormBody.Builder()
                .add("id",inusername.getText().toString())
                .add("name",incname.getText().toString())
                .add("password",inpassword.getText().toString()).build();
        final Request request = new Request.Builder().url("http://101.132.116.167:666/Insert").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                InformationInActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("e",e.toString());
                        Toast toast=Toast.makeText(InformationInActivity.this,"网络请求失败",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String json = response.body().string();
                InformationInActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("json",json);
                        Result result = new Gson().fromJson(json,Result.class);
                        if (result.getStatus().equals("1")){
                            Toast toast=Toast.makeText(InformationInActivity.this,"注册成功",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }else {
                            Toast toast = Toast.makeText(InformationInActivity.this,"账号已存在",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }

                    }
                });
            }
        });
    }
    private void postLgion(){
        OkHttpClient client = new OkHttpClient();
        final RequestBody requestBody = new FormBody.Builder()
                .add("id",login_usename.getText().toString())
                .add("password",login_pasword.getText().toString()).build();
        final Request request = new Request.Builder().url("http://101.132.116.167:666/Login").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                InformationInActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("e",e.toString());
                        Toast toast=Toast.makeText(InformationInActivity.this,"网络请求失败",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String json = response.body().string();
                InformationInActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("json",json);
                        Result result = new Gson().fromJson(json,Result.class);
                        if (result.getStatus().equals("1")){
                            Toast toast = Toast.makeText(InformationInActivity.this,"登录成功",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            SharedPreferences.Editor editor = getSharedPreferences("Information",MODE_PRIVATE).edit();
                            editor.putString("name",result.getName());
                            editor.putString("url",result.getUrl());
                            editor.putString("id",result.getId());
                            editor.apply();
//                            SharedPreferencesUtils.getInstance().saveParam("name",result.getName());
//                            SharedPreferencesUtils.getInstance().saveParam("url",result.getUrl());
//                            SharedPreferencesUtils.getInstance().saveParam("id",result.getId());
                            Intent intent = new Intent(InformationInActivity.this,MainActivity.class);
                            InformationInActivity.this.startActivity(intent);
                            finish();
                        }
                        else {
                            Toast toast = Toast.makeText(InformationInActivity.this, "账号或密码错误", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    }
                });
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_GRANT_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS:
                if (PermissionUtils.isGrantedAllPermissions(permissions, grantResults)) {
                    Toast.makeText(this, "你允许了全部授权", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "你拒绝了部分权限，可能造成程序运行不正常", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
