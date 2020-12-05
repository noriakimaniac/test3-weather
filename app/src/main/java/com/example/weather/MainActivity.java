package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private List<Integer> adcodeList = new ArrayList<>();

    private List<String> provinceList = new ArrayList<>();
    private List<String> cityList = new ArrayList<>();
    ArrayAdapter simpleAdapter;
    Button OK,MyConcern;
    EditText Research;
   // ListView ProvinceList;


    private void parseJSONWithJSONObject(String jsonData){

        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Integer adcode = jsonObject.getInt("adcode");
                String province = jsonObject.getString("province");
                String city = jsonObject.getString("city");

                    adcodeList.add(adcode);
                    provinceList.add(province);
                    cityList.add(city);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getJson(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OK = findViewById(R.id.ok);
        Research = findViewById(R.id.research);
        //ProvinceList = findViewById(R.id.provincelist);
        OK.setOnClickListener(this);
        MyConcern = findViewById(R.id.myconcern);
        MyConcern.setOnClickListener(this);

        String responseData = getJson("data1.json",this);
        parseJSONWithJSONObject(responseData);


        simpleAdapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,provinceList);
        ListView ProvinceList = (ListView) findViewById(R.id.provincelist);
        ProvinceList.setAdapter(simpleAdapter);

        ProvinceList.setOnItemClickListener(new AdapterView.OnItemClickListener(){      //配置ArrayList点击按钮
            @Override
            public void  onItemClick(AdapterView<?> parent, View view , int position , long id){
                int tran = adcodeList.get(position);
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("tran",tran);
                startActivity(intent);
            }
        });




    }
    @Override
    public void onClick(View v){

        switch (v.getId()){
            case R.id.ok:
                String researchcitycode = String.valueOf(Research.getText());
                if(researchcitycode.length()>6){
                    Toast.makeText(this,"数字长度不能大于六位！",Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, com.example.weather.Weather.class);
                    intent.putExtra("trancitycode", researchcitycode);
                    startActivity(intent);
                }
                break;
            case R.id.myconcern:

               /* SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                String citycode = pref.getString("citycode","");
               Intent  intent = new Intent(MainActivity.this, com.example.weather.Weather.class);
                intent.putExtra("trancitycode",citycode);
                startActivity(intent);
*/

                Intent intent = new Intent(MainActivity.this, com.example.weather.MyConcernList.class);
                startActivity(intent);
                break;
        }
    }



}