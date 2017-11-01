package com.example.taiga.nokorimono;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

public class RegistActivity extends AppCompatActivity {
    private static final int RESULT_PICK_IMAGEFILE = 1000;

    ImageView imageView;

    EditText nameEditV;
    EditText memoEditV;

    ItemEntity intentItem;

    String imageUri="";

    Bitmap temp;

    //storage
    private StorageReference mStorage;

    private ProgressDialog mProgressDialog;

    ArrayList<ItemEntity> itemEntities;

    int intentPos;

    String usrHashId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        imageView = (ImageView) findViewById(R.id.item_image);
        nameEditV = (EditText) findViewById(R.id.regist_name);
        memoEditV = (EditText) findViewById(R.id.regist_memo);

        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        intentPos = intent.getIntExtra("pos", -1);
        usrHashId=intent.getStringExtra("usrHashId");
        Bundle bundle = intent.getBundleExtra("item_bundle");
        if (bundle != null) {
            itemEntities = bundle.getParcelableArrayList("items");
        } else {
            itemEntities = new ArrayList<>();
        }
        if (intentPos != -1) {
            nameEditV.setText(itemEntities.get(intentPos).getName());
            memoEditV.setText(itemEntities.get(intentPos).getMemo());
            intentItem=itemEntities.get(intentPos);
            String dlUri = itemEntities.get(intentPos).getImageUrl();
            if (dlUri != null) {
                Picasso.with(RegistActivity.this).load(dlUri).fit().centerCrop().into(imageView);
                imageUri=dlUri;
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backEvent();
            return super.onKeyDown(keyCode, event);
        } else {
            return false;
        }
    }

    public void backActivity(View v) {
        backEvent();
    }

    public void backEvent(){
        String name = nameEditV.getText().toString();
        String memo = memoEditV.getText().toString();

        ItemEntity itemEntity;

        if (name.length() != 0) {
            if(intentPos!=-1) {
                itemEntity = intentItem;
            }
            else{
                itemEntity=new ItemEntity();
            }

            itemEntity.setName(name);

            if (memo.length() != 0) {
                itemEntity.setMemo(memo);
            }
            if(imageUri.length()!=0){
                itemEntity.setImageUrl(imageUri);
            }
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("items").child(usrHashId);

            if (intentPos == -1) {
                itemEntities.add(itemEntity);
            } else {
                itemEntities.set(intentPos, itemEntity);
            }

            myRef.setValue(itemEntities);
        }
        else{
            Toast.makeText(this,"名前を入力してください",Toast.LENGTH_SHORT);
        }

        finish();
    }

    public void goImageFolder(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK) {

            mProgressDialog.setMessage("画像を送信中...！");
            mProgressDialog.show();

            Uri uri = null;
            uri = resultData.getData();

            StorageReference filepath = mStorage.child("photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.dismiss();
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    imageUri=downloadUri.toString();
                    Toast.makeText(RegistActivity.this, "Upload done !", Toast.LENGTH_SHORT);
                }
            });
            try {
                Bitmap bmp = getBitmapFromUri(uri);
                temp = bmp;
                imageView.setImageBitmap(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
