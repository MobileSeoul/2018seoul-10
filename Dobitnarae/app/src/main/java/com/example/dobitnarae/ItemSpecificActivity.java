package com.example.dobitnarae;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemSpecificActivity extends AppCompatActivity {
    private Camera camera;
    private Uri photoURI, resultUri;
    private String resultAdd;
    private ImageView imageViewStore;

    private LinearLayout btnReduce, btnAdd;
    private TextView selectCnt;
    private Store store;

    private Activity activity;

    private int index;
    private Clothes item;
    private ArrayList<Clothes> items;

    private ArrayList<String> categoryList;
    private CharSequence[] items2;
    private List SelectedItems;
    private int categoryData, defaultItem;
    private TextView tvCategory;
    private boolean uploadFlag = false;

    public ItemSpecificActivity() {
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
        index = intent.getIntExtra("clothesid", 0);
        store = (Store) intent.getSerializableExtra("store");

        items = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
        for (Clothes item: items) {
            if(item.getCloth_id() == index)
                this.item = item;
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //뒤로가기
        ImageButton backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        ((TextView)findViewById(R.id.tv_add)).setText("변경");
        ((TextView)findViewById(R.id.toolbar_title)).setText(store.getName());
        ((TextView)findViewById(R.id.tv_cloth_title)).setText("옷 정보 변경");

        // 이미지
        imageViewStore = findViewById(R.id.reserve_clothes_img);
        ServerImg.getAdminClothesImageGlide(getApplicationContext(), item.getCloth_id(), imageViewStore);

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

        // 카테고리 선택
        categoryList = new ArrayList<String>();
        for(int i =0; i<Constant.CATEGORY_CNT; i++){
            if(i == 0)
                continue;
            categoryList.add(Constant.CATEGORY[i]);
        }
        categoryData = item.getCategory();

        items2 =  categoryList.toArray(new String[ categoryList.size()]);

        SelectedItems  = new ArrayList();
        defaultItem = categoryData-1;
        SelectedItems.add(defaultItem);

        tvCategory = findViewById(R.id.tv_category);
        tvCategory.setText(categoryList.get(categoryData-1));
        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        // 옷 이름
        final EditText name = findViewById(R.id.reserve_clothes_name);
        name.setText(item.getName());

        // 옷 설명
        final EditText description = findViewById(R.id.reserve_clothes_introduction);
        description.setText(item.getIntro());

        final EditText price = findViewById(R.id.item_price);
        price.setText(""+item.getPrice());

        btnReduce = findViewById(R.id.counting_btn_reduce);
        btnAdd = findViewById(R.id.counting_btn_add);
        selectCnt = findViewById(R.id.reserve_clothes_cnt);
        selectCnt.setText(""+item.getCount());

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

        final RadioGroup rg = (RadioGroup) findViewById(R.id.rg);
        final RadioButton rb1 = (RadioButton) findViewById(R.id.rb1);
        final RadioButton rb2 = (RadioButton) findViewById(R.id.rb2);
        // 저장된 정보에 따른 디폴드값 설정
        if(item.getSex()==Constant.MAN)
            rg.check(R.id.rb1);
        else
            rg.check(R.id.rb2);

        LinearLayout dataUpdate = (LinearLayout)findViewById(R.id.order_clothes_basket);
        dataUpdate.setOnClickListener(new View.OnClickListener() {
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
                                item.setName(name.getText().toString());
                                item.setCategory(categoryData);
                                if(rb1.isChecked())
                                    item.setSex(Constant.MAN);
                                else if(rb2.isChecked())
                                    item.setSex(Constant.WOMAN);
                                item.setCount(Integer.parseInt(selectCnt.getText().toString()));
                                item.setPrice(Integer.parseInt(price.getText().toString()));
                                item.setIntro(description.getText().toString());
                                NaverTranslate test = new NaverTranslate();
                                item.setTransName(test.translatedResult(item.getName()));
                                item.setTransIntro(test.translatedResult(item.getIntro()));

                                JSONTask.getInstance().updateCloth(item);
                                // 업로드 후 백그라운드에서 콜백메소드 실행 후 임시파일 및 디렉토리 삭제
                                if(uploadFlag)
                                    ServerImg.uploadFileOnPath(camera.getmCurrentPhotoPath(), String.valueOf(store.getId()), String.valueOf(item.getCloth_id()), getApplicationContext());
                                Toast.makeText(getApplicationContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();
                                ItemManagementFragment.changeFlg = true;
                                // 바로 종료시 에러있음
                                //finish();
                            }
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "에러: 올바른 가격을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LinearLayout dataDelete = (LinearLayout)findViewById(R.id.order_clothes_reserve);
        dataDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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