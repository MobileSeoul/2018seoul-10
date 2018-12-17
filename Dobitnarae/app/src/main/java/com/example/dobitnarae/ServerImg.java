package com.example.dobitnarae;
import java.io.File;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.dobitnarae.Retrofit.ImageInfo;
import com.example.dobitnarae.Retrofit.RetrofitInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerImg {

    static final String BaseURL = "";

    // 고객기준
    // 상점 이미지 가져오기
    public static void getStoreImageGlide(Context context, int storeID, ImageView imageView){
        Glide.with(context)
                .load(BaseURL + "store/" + storeID +".jpg")
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter()
                )
                .into(imageView);
    }

    // 옷 이미지 가져오기
    public static void getClothesImageGlide(Context context, int clothesID, ImageView imageView){
        Glide.with(context).load(BaseURL + "cloth/" + clothesID +".jpg")
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter()
                )
                .into(imageView);
    }

    // 대여점 기준
    // 상점 이미지 가져오기
    public static void getAdminStoreImageGlide(Context context, int storeID, ImageView imageView){
        Glide.with(context)
                .load(BaseURL + "store/" + storeID +".jpg")
                .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .fitCenter()
                        .skipMemoryCache(true)
                )
                .into(imageView);
    }

    // 옷 이미지 가져오기
    public static void getAdminClothesImageGlide(Context context, int clothesID, ImageView imageView){
        Glide.with(context).load(BaseURL + "cloth/" + clothesID +".jpg")
                .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .fitCenter()
                        .skipMemoryCache(true)
                )
                .into(imageView);
    }

    public static void uploadFile(Uri fileUri, String storeID, String clothesID, final Context context) {

        //creating a file
        File file = new File(getRealPathFromURI(fileUri, context));

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody storeIDField = RequestBody.create(MediaType.parse("text/plain"), storeID);
        RequestBody clothesIDField = RequestBody.create(MediaType.parse("text/plain"), clothesID);

        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //creating our api
        RetrofitInterface api = retrofit.create(RetrofitInterface.class);

        //creating a call and calling the upload image method
        Call<ImageInfo> call = api.uploadImage(requestFile, storeIDField, clothesIDField);

        //finally performing the call
        call.enqueue(new Callback<ImageInfo>() {
            @Override
            public void onResponse(Call<ImageInfo> call, Response<ImageInfo> response) {
                if (response.body() != null && !response.body().error) {
                    //Toast.makeText(context.getApplicationContext(), "File Uploaded Successfully...", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(context.getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ImageInfo> call, Throwable t) {
                //Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void uploadFileOnPath(String path, String storeID, String clothesID, final Context context) {

        //creating a file
        File file = new File(path);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody storeIDField = RequestBody.create(MediaType.parse("text/plain"), storeID);
        RequestBody clothesIDField = RequestBody.create(MediaType.parse("text/plain"), clothesID);

        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //creating our api
        RetrofitInterface api = retrofit.create(RetrofitInterface.class);

        //creating a call and calling the upload image method
        Call<ImageInfo> call = api.uploadImage(requestFile, storeIDField, clothesIDField);

        //finally performing the call
        call.enqueue(new Callback<ImageInfo>() {
            @Override
            public void onResponse(Call<ImageInfo> call, Response<ImageInfo> response) {
                if (response.body() != null && !response.body().error) {
                    //Toast.makeText(context.getApplicationContext(), "File Uploaded Successfully...", Toast.LENGTH_LONG).show();
                    //Camera.removeDir(context,"Pictures/img");

                } else {
                    //Toast.makeText(context.getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ImageInfo> call, Throwable t) {
                //Log.e("","" + call.toString() + ", " + t.getMessage());
                //Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Camera.removeDir(context,"Pictures/img");
                //Camera.removeDir(context,"Pictures");
            }
        });
    }

    private static String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);

        cursor.close();
        return result;
    }
}

