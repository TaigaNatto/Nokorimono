package com.example.taiga.nokorimono;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

public class RegistActivity extends AppCompatActivity {
    private static final int RESULT_PICK_IMAGEFILE = 1000;

    ImageView imageView;

    EditText nameEditV;
    EditText memoEditV;

    Bitmap temp;

    FirebaseStorage storage;

    ArrayList<ItemEntity> itemEntities;

    int intentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        imageView = (ImageView) findViewById(R.id.item_image);
        nameEditV = (EditText) findViewById(R.id.regist_name);
        memoEditV = (EditText) findViewById(R.id.regist_memo);

        storage = FirebaseStorage.getInstance();

        itemEntities=new ArrayList<>();

        Intent intent =getIntent();
        intentPos=intent.getIntExtra("pos",-1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("items");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity m=new MainActivity();
                if(!m.clickFlag) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ItemEntity item = data.getValue(ItemEntity.class);
                        itemEntities.add(item);
                    }
                    if(intentPos!=-1){
                        nameEditV.setText(itemEntities.get(intentPos).getName());
                        memoEditV.setText(itemEntities.get(intentPos).getMemo());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // データの取得に失敗
            }
        });
    }

    public void backActivity(View v) {

        String name=nameEditV.getText().toString();
        String memo=memoEditV.getText().toString();

        ItemEntity itemEntity=new ItemEntity();

        if (name.length() != 0) {
            itemEntity.setName(name);
        }
        if(memo.length()!=0){
            itemEntity.setMemo(memo);
        }
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("items");

        if(itemEntities==null){
            itemEntities=new ArrayList<>();
        }

        itemEntities.add(itemEntity);

        myRef.setValue(itemEntities);

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
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                try {
                    Bitmap bmp = getBitmapFromUri(uri);
                    temp = bmp;
                    imageView.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
