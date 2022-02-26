package com.beyondsw.palette;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.Settings;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.beyondsw.palette.service.FloatMonkService;

public class MonkActivity extends Activity {

    private Button open_float;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        requestWindowFeature(Window. FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams. FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_monk);
        open_float = findViewById(R.id.open_float);
        open_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFloatingService();
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        });
    }

    /**
     * 申请浮窗权限 2022年2月26日00:02:52
     */
    public void startFloatingService() {
        if (!Settings.canDrawOverlays(MonkActivity.this)) {
            Toast.makeText(MonkActivity.this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            startService(new Intent(MonkActivity.this, FloatMonkService.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(MonkActivity.this, FloatMonkService.class));
            }
        }
    }

}