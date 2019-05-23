package com.ysp.androidview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ysp.androidview.view.RulerView;
import com.ysp.androidview.view.TempControlView;

public class MainActivity extends AppCompatActivity {
    private TempControlView tempControl;
    private RulerView rv_water_volume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempControl = findViewById(R.id.tv_water_temp);//水温
        rv_water_volume=findViewById(R.id.rv_water_volume);//水量
        tempControl.setAngleRate(1);// 设置几格代表温度1度
        tempControl.setTemp(45, 99, 60);

        rv_water_volume.setSelectedValue(500);//设置水量ML
    }
}
