package com.example.taiga.nokorimono;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //メインのGridView
    GridView gridView;
    //メインGridView用のカスタムアダプター
    GridMainAdapter gridMainAdapter;

    //データ確認用のDialog
    AlertDialog alertDialog;
    //Dialogレイアウト取得用のView
    View inputView;

    ImageView dialogImageV;
    TextView dialogTextV;
    EditText dialogEditV;
    TextView dialogSeekTextV;
    SeekBar dialogSeekV;
    ImageView dialogBatteryV;

    //NavigationDrawer
    DrawerLayout navigationDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    TextView drawerTextV;
    ImageView drawerImageV;

    ArrayList<ItemEntity> itemEntitieList;
    ArrayList<Bitmap> itemBitmaps;

    int clickPos = 0;

    boolean clickFlag = false;

    Toolbar toolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //関連付け
        navigationDrawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        gridView = (GridView) findViewById(R.id.main_grid);
        drawerTextV = (TextView) findViewById(R.id.drawer_user_name);
        drawerImageV = (ImageView) findViewById(R.id.drawer_user_image);

        // ツールバーをアクションバーとしてセット
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //google関連
        mAuth = FirebaseAuth.getInstance();
        drawerTextV.setText(mAuth.getCurrentUser().getEmail());
        Picasso.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).fit().centerCrop().into(drawerImageV);

        itemEntitieList = new ArrayList<>();
        itemBitmaps = new ArrayList<>();

        gridMainAdapter = new GridMainAdapter(this);

        //確認用Dialog用レイアウトの読み込み
        LayoutInflater comFactory = LayoutInflater.from(this);
        inputView = comFactory.inflate(R.layout.dialog_main, null);
        //確認用dialog内のレイアウトの関連付け
        dialogImageV = (ImageView) inputView.findViewById(R.id.diarog_image);
        dialogTextV = (TextView) inputView.findViewById(R.id.dialog_title);
        dialogEditV = (EditText) inputView.findViewById(R.id.diarog_memo);
        dialogSeekTextV = (TextView) inputView.findViewById(R.id.diarog_seek_text);
        dialogSeekV = (SeekBar) inputView.findViewById(R.id.diarog_seekbar);
        dialogBatteryV = (ImageView) inputView.findViewById(R.id.dialog_battery);
        //dialog内のシークバーのリスナー
        dialogSeekV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("items");
                ItemEntity itemEntity = itemEntitieList.get(clickPos);
                //シークバーの場所でtextの表示変更
                switch (getSeekGoodPoint(i)) {
                    case 0:
                        dialogSeekTextV.setText("もう無い");
                        dialogSeekTextV.setTextColor(Color.parseColor("#ff3333"));
                        dialogBatteryV.setImageResource(R.drawable.battery_0_720);
                        // Write a message to the database
                        itemEntity.setNokoriPoint(0);
                        break;
                    case 25:
                        dialogSeekTextV.setText("ちょっとある");
                        dialogSeekTextV.setTextColor(Color.parseColor("#000000"));
                        dialogBatteryV.setImageResource(R.drawable.battery_1_720);
                        // Write a message to the database
                        itemEntity.setNokoriPoint(1);
                        break;
                    case 50:
                        dialogSeekTextV.setText("まだある");
                        dialogSeekTextV.setTextColor(Color.parseColor("#000000"));
                        dialogBatteryV.setImageResource(R.drawable.battery_2_720);
                        // Write a message to the database
                        itemEntity.setNokoriPoint(2);
                        break;
                    case 75:
                        dialogSeekTextV.setText("けっこうある");
                        dialogSeekTextV.setTextColor(Color.parseColor("#000000"));
                        dialogBatteryV.setImageResource(R.drawable.battery_3_720);
                        // Write a message to the database
                        itemEntity.setNokoriPoint(3);
                        break;
                    case 100:
                        dialogSeekTextV.setText("たくさんある");
                        dialogSeekTextV.setTextColor(Color.parseColor("#000000"));
                        dialogBatteryV.setImageResource(R.drawable.battery_4_720);
                        // Write a message to the database
                        itemEntity.setNokoriPoint(4);
                        break;
                }
                itemEntitieList.set(clickPos, itemEntity);
                myRef.setValue(itemEntitieList);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //離れたタイミングでシークバーの場所を固定
                dialogSeekV.setProgress(getSeekGoodPoint(seekBar.getProgress()));
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("items");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //カスタムアダプターの初期化
                gridMainAdapter.clear();
                itemEntitieList.clear();
                //Userインスタンスの取得
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final ItemEntity item = data.getValue(ItemEntity.class);
                    gridMainAdapter.add(item);
                    itemEntitieList.add(item);
                }
                gridView.setAdapter(gridMainAdapter);

                if (clickFlag) {
                    //名前が8文字以上であれば縮小処理
                    if(gridMainAdapter.getItem(clickPos).getName().length()>8) {
                        dialogTextV.setText(gridMainAdapter.getItem(clickPos).getName().substring(0,8)+"...");
                    }
                    else{
                        dialogTextV.setText(gridMainAdapter.getItem(clickPos).getName());
                    }
                    dialogEditV.setText(itemEntitieList.get(clickPos).getMemo());
                    if (gridMainAdapter.getItem(clickPos).getImageUrl() != null) {
                        Picasso.with(MainActivity.this).load(gridMainAdapter.getItem(clickPos).getImageUrl()).fit().centerCrop().into(dialogImageV);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // データの取得に失敗
            }
        });

//        /*** 検証用ダミーデータ ***/
//        for (int i = 1; i <= 20; i++) {
//            gridMainAdapter.add(String.valueOf(i));
//        }
//        /*** ***/
//
//        //カスタムアダプターのセット
//        gridView.setAdapter(gridMainAdapter);

        //ダイアログ表示用のアイテムクリックリスナー
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                clickPos = pos;
                clickFlag = true;
                if (gridMainAdapter.getItem(pos).getImageUrl() != null) {
                    Picasso.with(MainActivity.this).load(gridMainAdapter.getItem(pos).getImageUrl()).fit().centerCrop().into(dialogImageV);
                }
                else{
                    dialogImageV.setImageResource(R.color.colorPrimary);
                }
                //名前が8文字以上であれば縮小処理
                if(gridMainAdapter.getItem(pos).getName().length()>8) {
                    dialogTextV.setText(gridMainAdapter.getItem(pos).getName().substring(0,8)+"...");
                }
                else{
                    dialogTextV.setText(gridMainAdapter.getItem(pos).getName());
                }
                dialogEditV.setText(gridMainAdapter.getItem(pos).getMemo());
                //残りポイントで残高変更
                switch (gridMainAdapter.getItem(pos).getNokoriPoint()) {
                    case 0:
                        dialogSeekV.setProgress(0);
                        break;
                    case 1:
                        dialogSeekV.setProgress(25);
                        break;
                    case 2:
                        dialogSeekV.setProgress(50);
                        break;
                    case 3:
                        dialogSeekV.setProgress(75);
                        break;
                    case 4:
                        dialogSeekV.setProgress(100);
                        break;
                }
                //確認ダイアログの表示
                showDialog(MainActivity.this);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
//        if(alertDialog!=null){
//            alertDialog.dismiss();
//        }
    }

    public void addItem(View v) {
        //todo 追加アクティビティへ遷移
        Intent intent = new Intent(this, RegistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("items", itemEntitieList);
        intent.putExtra("item_bundle", bundle);
        startActivity(intent);
    }

    public void openDrawer(View v){
        navigationDrawer.openDrawer(Gravity.START);
    }

    //編集ボタンおした時
    public void editItem(View v) {
        //編集画面にいく
        Intent intent = new Intent(this, RegistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("items", itemEntitieList);
        intent.putExtra("item_bundle", bundle);
        intent.putExtra("pos", clickPos);
        startActivity(intent);
    }

    //削除ボタンおした時
    public void deleteItem(View v) {
        //todo 削除処理
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("本当に削除しますか")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OK button pressed
                        clickFlag = false;
                        alertDialog.dismiss();
                        itemEntitieList.remove(clickPos);
                        // Write a message to the database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("items");
                        myRef.setValue(itemEntitieList);
                    }
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    //googleサインアウト
    public void signoutGoogle(View v) {
        //todo ユーザー情報削除処理
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
//        mAuth.getCurrentUser().delete()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                });
    }

    //Dialog表示する
    public void showDialog(Context context) {
        if (alertDialog == null) {
            //Dialog生成
            alertDialog = new AlertDialog.Builder(context)
                    .setView(inputView)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (clickFlag) {
                                clickFlag = false;
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("items");
                                ItemEntity itemEntity = itemEntitieList.get(clickPos);
                                itemEntity.setMemo(dialogEditV.getText().toString());
                                itemEntitieList.set(clickPos, itemEntity);
                                myRef.setValue(itemEntitieList);
                            }
                        }
                    })
                    .create();
        }
        alertDialog.show();
    }

    //シークバーを配置するいい感じの場所を返してくれるメソッド
    public int getSeekGoodPoint(int nowPoint) {
        if (nowPoint >= 0 && nowPoint <= 12) {
            return 0;
        } else if (nowPoint > 12 && nowPoint <= 37) {
            return 25;
        } else if (nowPoint > 37 && nowPoint <= 62) {
            return 50;
        } else if (nowPoint > 62 && nowPoint <= 87) {
            return 75;
        } else {
            return 100;
        }
    }

    //リロード
    public void reloadGridView() {
        //カスタムアダプターの初期化
        gridMainAdapter.clear();
        for (int i = 0; i < itemEntitieList.size(); i++) {
            gridMainAdapter.add(itemEntitieList.get(i));
        }
        gridView.setAdapter(gridMainAdapter);
    }
}
