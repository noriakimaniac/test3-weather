package com.example.weather;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather extends AppCompatActivity implements View.OnClickListener{
    TextView Textshow;
    String researchcitycode;
    Button Concern,refresh;
    String CityshowString;

    private String city;

    int databaseadcode;
    String databasedata;
    int sign = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Textshow = findViewById(R.id.TextView);
        Concern = findViewById(R.id.concern1);
        Concern.setOnClickListener(this);
        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(this);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        researchcitycode = extras.getString("trancitycode");


        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Weather1.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //同上，获得可写文件
        Cursor cursor  = db.query("Weather1",new String[]{"adcode","data"},"adcode=?",new String[]{researchcitycode+""},null,null,null);

        if(cursor.moveToNext()) {       //逐行查找，得到匹配信息
            do {
                databaseadcode = cursor.getInt(cursor.getColumnIndex("adcode"));
                databasedata = cursor.getString(cursor.getColumnIndex("data"));
            } while (cursor.moveToNext());
        }
        int tranformat = 0;
        tranformat = Integer.parseInt(researchcitycode);

        if(databaseadcode ==  tranformat ){
            sign = 1;
            showResponse(databasedata);
        }else {
            sign = 0;
            sendRequestWithOkHttp();
        }

    }
    static int i = 0;
    static String s0,s1,s2;

    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    while(true) {

                        if (i == 0) {
                            SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("p0",researchcitycode);
                            editor.apply();
                            String s0 = sharedPreferences.getString("p0", "");
                            i = 1;
                            break;
                        }
                        if (i == 1) {
                            SharedPreferences sharedPreferences1 = getSharedPreferences("data1", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                            editor1.putString("p1",researchcitycode);
                            editor1.apply();
                            String s1 = sharedPreferences1.getString("p1", "");
                            i =2;
                            break;
                        }
                        if (i == 2) {
                            SharedPreferences sharedPreferences2 = getSharedPreferences("data2", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                            editor2.putString("p2",researchcitycode);
                            editor2.apply();
                            String s2 = sharedPreferences2.getString("p2", "");
                            i = 0;
                            break;
                        }
                    }
                    if(researchcitycode==s0)
                    {

                    }




                    Request request = new Request.Builder()
                            .url("https://restapi.amap.com/v3/weather/weatherInfo?key=69273d6512d8026ed0371488b06cf7a7&city="+researchcitycode)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("data is", responseData);
                    if(responseData.length()<100)
                    {
                        tt();
                    }
                    else {
                        showResponse(responseData);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }).start();
    }


    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String quStr=response.substring(response.indexOf("[")+1,response.indexOf("]"));
                String quStr2=quStr.substring(quStr.indexOf("{")+1,quStr.indexOf("}"));
                String str[]=quStr2.split(",");
                String string="";
                for (int i=0;i<str.length;i++){
                    string=str[i]+"\n"+string;
                }
                Textshow.setText(string);

            }
        });
    }
    public void tt()
    {
        String quStr2="输入错误,请查找正确的地址ID";
        Textshow.setText(quStr2);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.concern1:
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Concern.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("province", researchcitycode);
                values.put("city", 1);
                db.insert("Concern1", null, values);
                Toast.makeText(this, "关注成功！", Toast.LENGTH_LONG).show();
                break;
            case  R.id.refresh:
                sign = 3;
                sendRequestWithOkHttp();
                Log.d("MainActivity","数据库刷新成功");
                Toast.makeText(this,"数据刷新成功",Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
   public boolean onOptionsItemSelected(MenuItem item){

        return super.onOptionsItemSelected(item);
    }
}