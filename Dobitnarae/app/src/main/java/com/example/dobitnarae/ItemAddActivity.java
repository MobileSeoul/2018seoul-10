package com.example.dobitnarae;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemAddActivity extends AppCompatActivity {
    private Camera camera;
    private Uri photoURI, resultUri;
    private ImageView imageViewStore;

    private LinearLayout btnReduce, btnAdd;
    private TextView selectCnt;
    private Store store;

    private Activity activity;

    private Clothes item;

    private ArrayList<String> categoryList;
    private CharSequence[] items2;
    private List SelectedItems;
    private int categoryData, defaultItem;
    private TextView tvCategory;

    private EditText name, description, price;
    private RadioGroup rg;
    private RadioButton rb1, rb2;
    private boolean uploadFlag = false;

    public ItemAddActivity() {
        this.camera = new Camera();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        activity = this;

        // 가로모드 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        Intent intent = getIntent();
        store = (Store) intent.getSerializableExtra("store");

        item = new Clothes();

        setSupportActionBar((Toolbar) findViewById(R.id.tool_bar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //뒤로가기
        ((ImageButton)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        ((TextView)findViewById(R.id.toolbar_title)).setText(store.getName());

        imageViewStore = (ImageView) findViewById(R.id.reserve_clothes_img);
        imageViewStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        camera.captureCamera(activity);
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        camera.getAlbum(activity);
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(activity)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });

        camera.checkPermission(activity);

        name = findViewById(R.id.reserve_clothes_name);
        description = findViewById(R.id.reserve_clothes_introduction);

        categoryList = new ArrayList<String>();
        for(int i =0; i<Constant.CATEGORY_CNT; i++){
            if(i == 0)
                continue;
            categoryList.add(Constant.CATEGORY[i]);
        }

        items2 =  categoryList.toArray(new String[ categoryList.size()]);

        tvCategory = findViewById(R.id.tv_category);
        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        price = findViewById(R.id.item_price);

        btnReduce = findViewById(R.id.counting_btn_reduce);
        btnAdd = findViewById(R.id.counting_btn_add);
        selectCnt = findViewById(R.id.reserve_clothes_cnt);

        rg = (RadioGroup) findViewById(R.id.rg);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);

        clearSetting();

        // 수량 추가, 감소 버튼 이벤트
        btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = Integer.parseInt((String) selectCnt.getText()) - 1;
                if (cnt == 0)
                    btnReduce.setClickable(false);
                selectCnt.setText( "" + cnt);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = Integer.parseInt((String) selectCnt.getText()) + 1;
                if (cnt == 1)
                    btnReduce.setClickable(true);
                selectCnt.setText("" + cnt);
            }
        });

        ((LinearLayout)findViewById(R.id.order_clothes_basket)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value;
                try{
                    value = Integer.parseInt(price.getText().toString());

                    if(name.getText().toString().getBytes().length <= 0){
                        Toast.makeText(getApplicationContext(), "에러: 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    } else {
                        if(description.getText().toString().getBytes().length <= 0){
                            Toast.makeText(getApplicationContext(), "에러: 설명을 입력하세요", Toast.LENGTH_SHORT).show();
                        } else {
                            if(value <= 0){
                                Toast.makeText(getApplicationContext(), "에러: 올바른 가격을 입력하세요", Toast.LENGTH_SHORT).show();
                            } else {
                                item = new Clothes(0, store.getId(), categoryData, name.getText().toString(), description.getText().toString(), Integer.parseInt(price.getText().toString()), Integer.parseInt(selectCnt.getText().toString()), Constant.MAN);
                                if(rb1.isChecked())
                                    item.setSex(Constant.MAN);
                                else if(rb2.isChecked())
                                    item.setSex(Constant.WOMAN);
                                NaverTranslate test = new NaverTranslate();
                                item.setTransName(test.translatedResult(item.getName()));
                                item.setTransIntro(test.translatedResult(item.getIntro()));

                                clearSetting();
                                imageViewStore.setBackground(null);
                                JSONTask.getInstance().insertCloth(item, store.getId());
                                ArrayList<Clothes> items = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
                                int index = items.size();
                                item = items.get(index-1);

                                if(uploadFlag)
                                    ServerImg.uploadFileOnPath(camera.getmCurrentPhotoPath(), String.valueOf(store.getId()), String.valueOf(item.getCloth_id()), getApplicationContext());

                                Toast.makeText(getApplicationContext(), "추가되었습니다. 새로고침 해주세요.", Toast.LENGTH_SHORT).show();
                                ItemManagementFragment.changeFlg = true;
                            }
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "에러: 올바른 가격을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((LinearLayout)findViewById(R.id.order_clothes_reserve)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void clearSetting(){
        name.setText("");
        description.setText("");
        price.setText("");
        selectCnt.setText("1");
        rg.check(R.id.rb1);
        defaultItem = 0;
        SelectedItems  = new ArrayList();
        SelectedItems.add(defaultItem);
        categoryData = 1;
        tvCategory.setText(categoryList.get(categoryData-1));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            newConfig.orientation = Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case Constant.REQUEST_TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        // 갤러리에 추가만 시킴
                        camera.galleryAddPic(activity);
                    } catch (Exception e){
                        //Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.REQUEST_TAKE_ALBUM:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getData() != null){
                        try {
                            photoURI = data.getData();
                            InputStream i = getContentResolver().openInputStream(photoURI);

                            Bitmap bitmap = camera.resize(getApplicationContext(), photoURI, 200);

                            camera.createImageFileByBitmap(bitmap);
                            camera.copyFile(activity, i, camera.getmCurrentPhotoPath());
                            File f = new File(camera.getmCurrentPhotoPath());
                            resultUri = Uri.fromFile(f);
                            CropImage.activity(resultUri)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setCropMenuCropButtonTitle("자르기")
                                    //.setActivityTitle("이미지 업로드")
                                    .setOutputUri(resultUri)
                                    .start(this);
                        } catch (Exception e) {
                            //Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result= CropImage.getActivityResult(data);
                if(resultCode == Activity.RESULT_OK) {
                    imageViewStore.setImageURI(resultUri);
                    uploadFlag = true;
                } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error = result.getError();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case Constant.MY_PERMISSION_CAMERA:
                for(int i = 0; i< grantResults.length; i++){
                    // grantResult[]: 허용된 권한은 0, 거부한 권한은 -1
                    if(grantResults[i] < 0){
                        Toast.makeText(this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면
                break;
        }
    }

    public void categoryDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("카테고리 선택");
        builder.setSingleChoiceItems(items2, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        builder.setPositiveButton("선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!SelectedItems.isEmpty())
                            categoryData = (int) SelectedItems.get(0) + 1;
                        tvCategory.setText(categoryList.get(categoryData-1));
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}