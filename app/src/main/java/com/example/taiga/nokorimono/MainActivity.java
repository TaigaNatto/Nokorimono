package com.example.taiga.nokorimono;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
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

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //関連付け
        gridView=(GridView)findViewById(R.id.main_grid);
        //カスタムアダプターの初期化
        gridMainAdapter=new GridMainAdapter(this);

        // ツールバーをアクションバーとしてセット
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_add_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"追加します",Toast.LENGTH_SHORT);
            }
        });
        setSupportActionBar(toolbar);

        //Dialog用レイアウトの読み込み
        LayoutInflater factory = LayoutInflater.from(this);
        inputView = factory.inflate(R.layout.dialog_main, null);

        //dialog内のレイアウトの関連付け
        dialogImageV=(ImageView)inputView.findViewById(R.id.diarog_image);
        dialogTextV=(TextView)inputView.findViewById(R.id.dialog_title);
        dialogEditV=(EditText)inputView.findViewById(R.id.diarog_memo);
        dialogSeekTextV=(TextView)inputView.findViewById(R.id.diarog_seek_text);
        dialogSeekV=(SeekBar)inputView.findViewById(R.id.diarog_seekbar);
        dialogBatteryV=(ImageView)inputView.findViewById(R.id.dialog_battery);
        //dialog内のシークバーのリスナー
        dialogSeekV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //シークバーの場所でtextの表示変更
                switch (getSeekGoodPoint(i)){
                    case 0:
                        dialogSeekTextV.setText("もう無い");
                        dialogBatteryV.setImageResource(R.drawable.battery_0_720);
                        break;
                    case 25:
                        dialogSeekTextV.setText("ちょっとある");
                        dialogBatteryV.setImageResource(R.drawable.battery_1_720);
                        break;
                    case 50:
                        dialogSeekTextV.setText("まだある");
                        dialogBatteryV.setImageResource(R.drawable.battery_2_720);
                        break;
                    case 75:
                        dialogSeekTextV.setText("けっこうある");
                        dialogBatteryV.setImageResource(R.drawable.battery_3_720);
                        break;
                    case 100:
                        dialogSeekTextV.setText("たくさんある");
                        dialogBatteryV.setImageResource(R.drawable.battery_4_720);
                        break;
                }
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

        /*** 検証用ダミーデータ ***/
        for(int i=1;i<=20;i++){
            gridMainAdapter.add(String.valueOf(i));
        }
        /*** ***/

        //カスタムアダプターのセット
        gridView.setAdapter(gridMainAdapter);

        //ダイアログ表示用のアイテムクリックリスナー
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                dialogTextV.setText(gridMainAdapter.getItem(pos).toString());
                showDialog(MainActivity.this);
            }
        });

    }

    //Dialog表示する
    public void showDialog(Context context){
        if(alertDialog==null) {
            //Dialog生成
            alertDialog = new AlertDialog.Builder(context)
                    .setView(inputView)
                    .create();
        }
        alertDialog.show();
    }

    //シークバーを配置するいい感じの場所を返してくれるメソッド
    public int getSeekGoodPoint(int nowPoint){
        if(nowPoint>=0&&nowPoint<=12){
            return 0;
        }
        else if(nowPoint>12&&nowPoint<=37){
            return 25;
        }
        else if(nowPoint>37&&nowPoint<=62){
            return 50;
        }
        else if(nowPoint>62&&nowPoint<=87){
            return 75;
        }
        else{
            return 100;
        }
    }
}
