package cn.edu.zjicm.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import cn.edu.zjicm.Utils.SharedPreferencesUtils;
import cn.edu.zjicm.chat.InformationInActivity;
import cn.edu.zjicm.chat.R;
import cn.edu.zjicm.Utils.ChatUtils;
import cn.edu.zjicm.toos.Result;
import cn.edu.zjicm.view.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class PersionFragment extends Fragment {
    @Nullable

    private CircleImageView  uploadImg;
    private ProgressDialog progressDialog;
    private String id;
    private TextView breake_passwrd;
    private TextView personalized_signature;
    private TextView person_name;
    private TextView dialog_text;
    private Button button_deselect;
    private Button button_change;
    private String toId;
    private String operation;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person,container,false);

//        sharedPreferences = getActivity().getSharedPreferences("Information",MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        id = sharedPreferences.getString("id","");
        id =(String)SharedPreferencesUtils.getInstance().getParam("id","");
        uploadImg = (CircleImageView)view.findViewById(R.id.headImg);
        personalized_signature = (TextView)view.findViewById(R.id.personalized_signature);
        person_name = (TextView)view.findViewById(R.id.person_name);
        person_name.setText((String)SharedPreferencesUtils.getInstance().getParam("name",""));
        person_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDialog();
            }
        });
        RequestOptions options = new RequestOptions().error(R.drawable.ic_default_minerva)
                .signature(new ObjectKey(ChatUtils.imgUrlKey));
        String url = (String)SharedPreferencesUtils.getInstance().getParam("url","");
        Log.i("url", url);
        Glide.with(getActivity()).load(ChatUtils.imgUrl+url).apply(options).into(uploadImg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(getActivity()).clearDiskCache();

            }
        }).start();

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
        breake_passwrd = (TextView)view.findViewById(R.id.brake_passwoed);
        breake_passwrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtils.getInstance().saveParam("name","");
                SharedPreferencesUtils.getInstance().saveParam("url","");
                SharedPreferencesUtils.getInstance().saveParam("id","");
                Intent intent = new Intent(getActivity(), InformationInActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }


    //图片选择处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/Download");
        Log.i("zsp_directory",directory.getPath());
        if(!directory.exists()) { directory.mkdirs(); }
        File file = new File(directory,  id+".jpg");
        if(requestCode == 0) {

            if(data != null) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(data.getData(), "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("scale", true);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 300);
                intent.putExtra("outputY", 300);
                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true); // no face detection
                startActivityForResult(intent, 1);
            }
        }
        else if(requestCode == 1) {
            if(resultCode == -1) {
                progressDialog = ProgressDialog.show(getActivity(), "", "正在上传头像......");
                OkHttpClient client = new OkHttpClient();

                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                RequestBody requestBody = new MultipartBody.Builder().setType(MediaType.parse("multipart/form-data"))
                        .addFormDataPart("userID","1")
                        .addFormDataPart("img", file.getName(), fileBody)
                        .build();

                Request request = new Request.Builder()
                        .url("http://101.132.116.167:666/uploadImg")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("zspe", e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String json = response.body().string();
                        progressDialog.dismiss();
                        Log.i("zsp",json);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                SharedPreferencesUtils.getInstance().saveParam("url",json);
                                ChatUtils.imgUrlKey= System.currentTimeMillis();
                                RequestOptions options = new RequestOptions().error(R.drawable.ic_default_minerva)
                                        .signature(new ObjectKey(ChatUtils.imgUrlKey)).encodeQuality(70);
                                String url = (String)SharedPreferencesUtils.getInstance().getParam("url","");
                                Glide.with(getActivity()).load(ChatUtils.imgUrl+url).apply(options).into(uploadImg);
                            }
                        });
                    }
                });
            }

        }
    }
    public void changeDialog(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_dialog,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        dialog_text = (TextView)view.findViewById(R.id.dialog_text);
        button_change = (Button)view.findViewById(R.id.button_change);
        button_deselect = (Button)view.findViewById(R.id.button_deselect);
        button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toId = dialog_text.getText().toString();
                postChangeName(toId,dialog);
            }
        });
        button_deselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(900,500);

    }
    public void postChangeName(String toId, final AlertDialog dialog){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("id",(String)SharedPreferencesUtils.getInstance().getParam("id",""))
                .add("name",toId).build();
        Request request = new Request.Builder().url(ChatUtils.imgUrl+"/changeName").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast=Toast.makeText(getActivity(),"网络请求失败",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Result result = new Gson().fromJson(json,Result.class);
                        SharedPreferencesUtils.getInstance().saveParam("name",result.getName());
                        person_name.setText(result.getName());

                    }
                });

            }
        });
    }
}
