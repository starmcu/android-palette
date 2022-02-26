package com.beyondsw.palette.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.beyondsw.palette.PaletteView;
import com.beyondsw.palette.R;

public class FloatMonkView extends View implements View.OnClickListener, PaletteView.Callback {

    private volatile int[] location = null;
    private int x;
    private int y;
    private int h;
    private int w;
    private boolean mini;

    /**
     *
     */
    private void toMini() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {

            layoutParams.width = w;
            layoutParams.height = h;

            paint_layout.setVisibility(GONE);
            mMiniView.setVisibility(GONE);
            mFullView.setVisibility(VISIBLE);

            // 将悬浮窗控件添加到WindowManager
            windowManager.updateViewLayout(displayView, this.layoutParams);

            mini = true;

        });
    }

    /**
     *
     */
    private void toFull() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

            paint_layout.setVisibility(VISIBLE);
            mMiniView.setVisibility(VISIBLE);
            mFullView.setVisibility(GONE);

            // 将悬浮窗控件添加到WindowManager
            windowManager.updateViewLayout(displayView, this.layoutParams);

            mini = false;

        });
    }

    private View displayView;
    private View paint_layout;
    private View mUndoView;
    private View mRedoView;
    private View mPenView;
    private View mEraserView;
    private View mClearView;
    private View mMiniView;
    private View mFullView;

    private PaletteView mPaletteView;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public FloatMonkView(Context context) {
        super(context);
        // 获取WindowManager服务
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // 设置LayoutParam
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //悬浮窗弹出的位置
        layoutParams.gravity = Gravity.LEFT | Gravity.CENTER;
        //1、 注意：这一个flags的设置，之前搜索很多实现都没有设置这个，出现的情况就是在悬浮的view出现后  点击窗口其它地方没有反应，是因为不设置这个参数，悬浮窗弹出来后就占据整个窗口的焦点。
        //2、 实现悬浮窗可以移动的属性
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.x = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.y = WindowManager.LayoutParams.MATCH_PARENT;

        displayView = this.inflate(context, R.layout.float_monk_layout, null);
        paint_layout = displayView.findViewById(R.id.paint_layout);
        mPaletteView = displayView.findViewById(R.id.palette);
        mPaletteView.setCallback(this);
        mPenView = displayView.findViewById(R.id.pen);
        mPenView.setSelected(true);

        mUndoView = displayView.findViewById(R.id.undo);
        mRedoView = displayView.findViewById(R.id.redo);
        mEraserView = displayView.findViewById(R.id.eraser);
        mClearView = displayView.findViewById(R.id.clear);
        mMiniView = displayView.findViewById(R.id.mini);
        mFullView = displayView.findViewById(R.id.full);

        mUndoView.setOnClickListener(this);
        mRedoView.setOnClickListener(this);
        mPenView.setOnClickListener(this);
        mEraserView.setOnClickListener(this);
        mClearView.setOnClickListener(this);
        mMiniView.setOnClickListener(this);
        mFullView.setOnClickListener(this);

        mFullView.setOnTouchListener(new OnTouchListener() {

            private int x;
            private int y;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        int movedX = nowX - x;
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        layoutParams.x = layoutParams.x + movedX;
                        layoutParams.y = layoutParams.y + movedY;

                        // 更新悬浮窗控件布局
                        windowManager.updateViewLayout(displayView, layoutParams);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mUndoView.setEnabled(false);
        mRedoView.setEnabled(false);


        // 将悬浮窗控件添加到WindowManager
        windowManager.addView(displayView, layoutParams);

        if (location == null) {
            location = new int[2];
            mMiniView.getLocationOnScreen(location);
            x = location[0];
            y = location[1];
            h = mMiniView.getLayoutParams().height;
            w = mMiniView.getLayoutParams().width;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undo:
                mPaletteView.undo();
                break;
            case R.id.redo:
                mPaletteView.redo();
                break;
            case R.id.pen:
                v.setSelected(true);
                mEraserView.setSelected(false);
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                break;
            case R.id.eraser:
                v.setSelected(true);
                mPenView.setSelected(false);
                mPaletteView.setMode(PaletteView.Mode.ERASER);
                break;
            case R.id.clear:
                mPaletteView.clear();
                break;
            case R.id.mini:
                toMini();
                break;
            case R.id.full:
                toFull();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUndoRedoStatusChanged() {
        mUndoView.setEnabled(mPaletteView.canUndo());
        mRedoView.setEnabled(mPaletteView.canRedo());
    }

}
