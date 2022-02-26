package com.beyondsw.palette.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;

import com.beyondsw.palette.PaletteView;
import com.beyondsw.palette.view.FloatMonkView;

/**
 * 悬浮窗在服务中创建，通过暴露接口FloatCallBack与Activity进行交互
 */
public class FloatMonkService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
        public void onCreate() {
            super.onCreate();
            showFloatingWindow();
        }

        private void showFloatingWindow() {
            if (Settings.canDrawOverlays(this)) {
                // 新建悬浮窗控件
                FloatMonkView floatMonkView = new FloatMonkView(getApplicationContext());
            }

    }
}