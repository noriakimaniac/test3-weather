package com.example.weather;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MyConcernList extends AppCompatActivity {

    ArrayAdapter simpleAdapter;
    ListView MyConcernList;
    private List<String> provinceList = new ArrayList<>();
    private List<String> cityList = new ArrayList<>();

    private void InitConcern() {       //进行数据填装
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Concern1.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor  = db.rawQuery("select * from Concern1",null);
        while(cursor.moveToNext()){
            String province= cursor.getString(cursor.getColumnIndex("province"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            provinceList.add(province);
            cityList.add(city);
        }
    }

    public void RefreshList(){
        provinceList.removeAll(provinceList);
        cityList.removeAll(cityList);
        simpleAdapter.notifyDataSetChanged();
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Concern.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor  = db.rawQuery("select * from Concern1",null);
        while(cursor.moveToNext()){
            String province= cursor.getString(cursor.getColumnIndex("province"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            provinceList.add(province);
            cityList.add(city);
        }
    }


    @Override
    protected void onStart(){
        super.onStart();
        RefreshList();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_concern_list);
        MyConcernList = findViewById(R.id.MyConcernList);
        registerForContextMenu(MyConcernList);
        InitConcern();

        simpleAdapter = new ArrayAdapter(MyConcernList.this,android.R.layout.simple_list_item_1,provinceList);

        MyConcernList.setAdapter(simpleAdapter);
        MyConcernList.setOnItemClickListener(new AdapterView.OnItemClickListener(){      //配置ArrayList点击按钮
            @Override
            public void  onItemClick(AdapterView<?> parent, View view , int position , long id){
                String tran = provinceList.get(position);
                Intent intent = new Intent(MyConcernList.this, com.example.weather.Weather.class);
                intent.putExtra("trancitycode",tran);
                startActivity(intent);
            }
        });
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.getMenuInflater().inflate(R.menu.main, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView textId = null;

        AdapterView.AdapterContextMenuInfo info = null;
        View itemView = null;

        switch (item.getItemId()) {
            case R.id.delete:
                //删除单词
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                String tran = provinceList.get(1);
                MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Concern.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                db.delete("Concern1", "province=?", new String[] {"110106"});
                RefreshList();
                simpleAdapter = new ArrayAdapter(MyConcernList.this,android.R.layout.simple_list_item_1,provinceList);
                MyConcernList.setAdapter(simpleAdapter);
                break;
        }
        return true;
    }
}
