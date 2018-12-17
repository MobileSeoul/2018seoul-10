package com.example.dobitnarae;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Camera {
    private String mCurrentPhotoPath;

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public void captureCamera(Activity activity){
        String state = Environment.getExternalStorageState();
        // 외장메모리 검사
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex ){
                    //Log.e("captureCamera Error", ex.toString());
                }
                if(photoFile != null) {
                    // getUriForFile의 두 번째 인자는 Manifest provider authorites와 일치해야함
                    Uri providerURI = FileProvider.getUriForFile(activity, activity.getPackageName(), photoFile);
                    //imageURI = providerURI;

                    // 인텐트에 전달할 때는 FileProvider의 Return값인 content로만, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    activity.startActivityForResult(takePictureIntent, Constant.REQUEST_TAKE_PHOTO);
                }
            }
        }else {
            Toast.makeText(activity, "저장공간이 접근 불가능한 기기입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws  IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "img");

        if(!storageDir.exists()){
            //Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    public File createImageFileByBitmap(Bitmap bitmap) throws  IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "img");
        OutputStream out = null;
        if(!storageDir.exists()){
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imageFileName);
        out = new FileOutputStream(imageFile);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    //파일 & 폴더 삭제
    public static void removeDir(Context context, String dirName) {
        String mRootPath = Environment.getExternalStorageDirectory() + File.separator + dirName;
        //Toast.makeText(context, mRootPath, Toast.LENGTH_SHORT).show();

        File file = new File(mRootPath);

        File[] childFileList = file.listFiles();
        if(childFileList.length > 0) {
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    removeDir(context, childFile.getAbsolutePath());    //하위 디렉토리
                } else {
                    childFile.delete();    //하위 파일
                }
            }
        }
        file.delete();    //root 삭제
    }

    public void getAlbum(Activity activity) {
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        activity.startActivityForResult(intent, Constant.REQUEST_TAKE_ALBUM);
    }

    public void galleryAddPic(Activity activity){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화
        File f = new File(mCurrentPhotoPath);
        Uri contentURI = Uri.fromFile(f);
        mediaScanIntent.setData(contentURI);
        activity.sendBroadcast(mediaScanIntent);
        Toast.makeText(activity, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    /**
     * 파일 복사
     * @param save_file
     * @return
     */
    public boolean copyFile(Context context, InputStream i , String save_file){
        boolean result;
        if(i!=null){
            try {
                InputStream fis = i;
                FileOutputStream newfos = new FileOutputStream(save_file);
                int readcount=0;
                byte[] buffer = new byte[1024];
                while((readcount = fis.read(buffer,0,1024))!= -1){
                    newfos.write(buffer,0,readcount);
                }
                newfos.close();
                fis.close();
                //Toast.makeText(context, "파일이 복사되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            //Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

    public void checkPermission(final Activity activity) {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{...}의 요청으로 넘어감
            if((ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    || (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA))){
                new AlertDialog.Builder(activity)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                activity.startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, Constant.MY_PERMISSION_CAMERA);
            }
        }
    }

    // 이미지 리사이즈
    public Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }
}
