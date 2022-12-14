package com.dylan.bottomdialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.dylan.bottomdialog.local.feg;
import com.dylan.dialog.BottomDialog;
import com.dylan.dialog.Item;
import com.dylan.dialog.OnItemClickListener;
import com.dylan.dialog.RawItem;

public class MainActivity extends AppCompatActivity {

    private String makeNum(){
        Random random=new Random();
        String num=random.nextInt(9999999)+"";
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < 7-num.length(); i++) {
            sb.append("0") ;
        }
        num =sb.toString()+num;
        return num;
    }

    private RawItem pengyouquan;
    private RawItem weixinhaoyou;
    private RawItem weibo;
    private RawItem qq;
    private RawItem qzone;
    private RawItem qzone4;
    private RawItem share;
    private RawItem delete;
    private RawItem add;
    private RawItem git;
    public List<RawItem> rawItems_share;
    public List<RawItem> rawItems_main;
    public BottomDialog HORIZONTAL,VERTICAL,HORIZONTAL1,GRID;
    public LinearLayout container;


    private Drawable getpic(int id){
        return ContextCompat.getDrawable(this, id);
    }

    private void showMyDialog(Context context, BottomDialog bottomDialog, String dialogid, String title, int rowcount, List<List<RawItem>> list, List<OnItemClickListener> listener){
        bottomDialog.removeView();
        bottomDialog.title(title);
        if(list.size()==listener.size()){
            for(int i = 0; i < rowcount; i++){
                try{
                    bottomDialog.inflateList(list.get(i),listener.get(i));
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,rowcount+"",Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(context,"????????????",Toast.LENGTH_SHORT).show();
        }
        bottomDialog.show(dialogid);
    }

    private void showMyDialog(Context context, BottomDialog bottomDialog, String dialogid, int title, int rowcount, List<List<RawItem>> list, List<OnItemClickListener> listener){
        bottomDialog.removeView();
        bottomDialog.title(title);
        if(rowcount==listener.size()){
            for(int i = 0; i < rowcount; i++){
                try{
                    bottomDialog.inflateList(list.get(i),listener.get(i));
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,rowcount+"",Toast.LENGTH_SHORT).show();
                }

            }
        }else{
            Toast.makeText(context,"????????????",Toast.LENGTH_SHORT).show();
        }
        bottomDialog.show(dialogid);
    }

    public void shownewdialog(String dialogid){
        List<List<RawItem>> itemall = new ArrayList<>();
        List<OnItemClickListener> listeners = new ArrayList<>();
        switch (dialogid){
            case "????????????":
                itemall.add(rawItems_share);
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Toast.makeText(MainActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
                showMyDialog(MainActivity.this, HORIZONTAL, "????????????", R.string.share_title, listeners.size(), itemall,listeners);
                break;

            case "????????????":
                itemall.add(rawItems_share);
                itemall.add(rawItems_main);
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Toast.makeText(MainActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });

                showMyDialog(MainActivity.this,VERTICAL,"????????????", R.string.share_title, listeners.size(), itemall,listeners);
                break;

            case "??????":
                itemall.add(rawItems_share);
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Toast.makeText(MainActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
//                        HORIZONTAL1.dismiss();
                    }
                });

                showMyDialog(MainActivity.this,HORIZONTAL1,"??????", R.string.title_item, listeners.size(), itemall,listeners);
                break;

            case "??????":
                itemall.add(rawItems_share);
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Toast.makeText(MainActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });

                showMyDialog(MainActivity.this,GRID,"??????", R.string.title_item, listeners.size(), itemall,listeners);
                break;

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pengyouquan = new RawItem(Integer.parseInt(makeNum()),"?????????vrverbtr?????????????????????",getpic(R.mipmap.ic_share_moments));
        weixinhaoyou = new RawItem(Integer.parseInt(makeNum()),"????????????",getpic(R.mipmap.ic_share_wechat));
        weibo = new RawItem(Integer.parseInt(makeNum()),"????????????",getpic(R.mipmap.ic_share_weibo));
        qq = new RawItem(Integer.parseInt(makeNum()),"qq",getpic(R.mipmap.ic_share_qq));
        qzone = new RawItem(Integer.parseInt(makeNum()),"qq??????",getpic(R.mipmap.ic_share_qzone));
        qzone4 = new RawItem(Integer.parseInt(makeNum()),"qq??????",getpic(R.mipmap.ic_share_qzone));
        share = new RawItem(Integer.parseInt(makeNum()),"??????",getpic(R.mipmap.ic_action_social_share));
        delete = new RawItem(Integer.parseInt(makeNum()),"??????",getpic(R.mipmap.ic_action_action_delete));
        add = new RawItem(Integer.parseInt(makeNum()),"??????",getpic(R.mipmap.ic_action_content_add));
        git = new RawItem(Integer.parseInt(makeNum()),"github??????",getpic(R.mipmap.ic_github));
        rawItems_share = Arrays.asList(pengyouquan,weixinhaoyou,weibo,qq,qzone,qzone4);
        rawItems_main = Arrays.asList(share,delete,add,git);
        HORIZONTAL = new BottomDialog(MainActivity.this, BottomDialog.HORIZONTAL, "????????????");
        VERTICAL = new BottomDialog(MainActivity.this, BottomDialog.HORIZONTAL, "????????????");
        HORIZONTAL1 = new BottomDialog(MainActivity.this, BottomDialog.VERTICAL, "??????");
        GRID = new BottomDialog(MainActivity.this, BottomDialog.GRID, "??????");


        findViewById(R.id.horizontal_single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<List<RawItem>> itemall = new ArrayList<>();
                List<OnItemClickListener> listeners = new ArrayList<>();
                itemall.add(rawItems_share);
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Toast.makeText(MainActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });

                showMyDialog(MainActivity.this, HORIZONTAL, "????????????", R.string.share_title, listeners.size(), itemall,listeners);

            }
        });

        findViewById(R.id.horizontal_multi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feg iosLoadingDialog = new feg().setOnTouchOutside(true);
                iosLoadingDialog.show(getFragmentManager(), "AndroidLoadingDialog");

                List<List<RawItem>> itemall = new ArrayList<>();
                List<OnItemClickListener> listeners = new ArrayList<>();
                itemall.add(rawItems_share);
                itemall.add(rawItems_main);
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Toast.makeText(MainActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });



                showMyDialog(MainActivity.this,VERTICAL,"????????????", R.string.share_title, listeners.size(), itemall,listeners);
            }
        });

        findViewById(R.id.vertical).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<List<RawItem>> itemall = new ArrayList<>();
                List<OnItemClickListener> listeners = new ArrayList<>();
                itemall.add(rawItems_share);
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {

                        Toast.makeText(MainActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
//                        HORIZONTAL1.dismiss();
                    }
                });

                showMyDialog(MainActivity.this,HORIZONTAL1,"??????", R.string.title_item, listeners.size(), itemall,listeners);
            }
        });

        findViewById(R.id.grid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<List<RawItem>> itemall = new ArrayList<>();
                List<OnItemClickListener> listeners = new ArrayList<>();
                itemall.add(rawItems_share);
                listeners.add(new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        Toast.makeText(MainActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });

                showMyDialog(MainActivity.this,GRID,"??????", R.string.title_item, listeners.size(), itemall,listeners);
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String isshowingdialog = BottomDialog.dialogid;
        outState.putString("isshowing",isshowingdialog);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try{
            String dialog_shown = savedInstanceState.getString("isshowing");
            if(!dialog_shown.equals("dialogdismiss")){
                shownewdialog(dialog_shown);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
