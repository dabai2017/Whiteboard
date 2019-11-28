package com.dabai.artboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dabai.artboard.BoardBase.DoodleView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.chip.Chip;


public class MainActivity extends AppCompatActivity {

    private DoodleView mDoodleView;
    ConstraintLayout constraintLayout;

    LinearLayout doodle_linelayout;

    CardView toolcard;


    private AlertDialog mColorDialog;
    private AlertDialog mPaintDialog;
    private AlertDialog mShapeDialog;


    Chip chip2, chip3, chip5;
    private int lastSize, lastColor;
    private DoodleView.ActionType lastAt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();


        mDoodleView = findViewById(R.id.doodle_doodleview);
        constraintLayout = findViewById(R.id.cons);
        toolcard = findViewById(R.id.tool_cardlayout);

        doodle_linelayout = findViewById(R.id.doodle_linelayout);

        chip2 = findViewById(R.id.chip2);
        chip3 = findViewById(R.id.chip3);
        chip5 = findViewById(R.id.chip5);

        mDoodleView.setSize(13);

        init();



        if(!get_sharedString("f","t").equals("f")) {

            new AlertDialog.Builder(this)
                    .setTitle("帮助")
                    .setMessage("音量键：显示/隐藏工具栏")
                    .setCancelable(false)
                    .setPositiveButton("了解", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            set_sharedString("f", "f");
                        }
                    })
                    //.setNeutralButton("取消", null)
                    .show();
        }


        }

    private void init() {
        //监听事件

        chip2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                mDoodleView.setSize(13);
                f5();

                return true;
            }
        });
        chip3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                mDoodleView.setType(DoodleView.ActionType.Path);
                f5();

                return true;
            }
        });


        lastSize = mDoodleView.getCurrentSize();
        lastAt = mDoodleView.getmActionType();
        lastColor = mDoodleView.getCurrentColor();
        chip5.setText("橡皮擦");

        chip5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chip5.getText().equals("橡皮擦")) {

                    chip5.setText("画笔");

                    lastSize = mDoodleView.getCurrentSize();
                    lastAt = mDoodleView.getmActionType();
                    lastColor = mDoodleView.getCurrentColor();

                    mDoodleView.setType(DoodleView.ActionType.Path);
                    mDoodleView.setSize(100);
                    mDoodleView.setColor("#ffffff");

                } else {
                    chip5.setText("橡皮擦");

                    mDoodleView.setType(lastAt);
                    mDoodleView.setSize(lastSize);
                    mDoodleView.setColor(getHexString(lastColor));

                }


            }
        });


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDoodleView.onTouchEvent(event);
    }


    @Override
    protected void onResume() {
        super.onResume();
/*
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

*/

        f5();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

// 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            set_sharedString("tool", "true");
            f5();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            set_sharedString("tool", "false");
            f5();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void f5() {
        if (get_sharedString("tool", "false").equals("true")) {
            toolcard.setVisibility(View.VISIBLE);
        } else {
            toolcard.setVisibility(View.GONE);
        }


        chip2.setText("画笔大小:" + mDoodleView.getCurrentSize());
        chip3.setText("画笔形状:" + mDoodleView.getmActionType());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_color:
                showColorDialog();
                break;
            case R.id.main_size:
                showSizeDialog();
                break;
            case R.id.main_action:
                showShapeDialog();
                break;
            case R.id.main_reset:
                mDoodleView.reset();
                break;
            case R.id.main_undo:
                mDoodleView.undo();
                break;
            case R.id.main_forward:
                mDoodleView.forward();
                break;

            case R.id.main_save:

                Toast.makeText(this, "还没写", Toast.LENGTH_SHORT).show();

                break;
        }
        return true;
    }

    /**
     * 显示选择画笔颜色的对话框
     */
    private void showColorDialog() {
        if (mColorDialog == null) {
            mColorDialog = new AlertDialog.Builder(this)
                    .setTitle("选择颜色")
                    .setSingleChoiceItems(new String[]{"蓝色", "红色", "黑色"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            mDoodleView.setColor("#0000ff");
                                            break;
                                        case 1:
                                            mDoodleView.setColor("#ff0000");
                                            break;
                                        case 2:
                                            mDoodleView.setColor("#272822");
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mColorDialog.show();
    }

    /**
     * 显示选择画笔粗细的对话框
     */
    private void showSizeDialog() {
        if (mPaintDialog == null) {
            mPaintDialog = new AlertDialog.Builder(this)
                    .setTitle("选择画笔粗细")
                    .setSingleChoiceItems(new String[]{"细", "中", "粗"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            mDoodleView.setSize(dip2px(5));
                                            break;
                                        case 1:
                                            mDoodleView.setSize(dip2px(10));
                                            break;
                                        case 2:
                                            mDoodleView.setSize(dip2px(15));
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mPaintDialog.show();
    }

    /**
     * 显示选择画笔形状的对话框
     */
    private void showShapeDialog() {
        if (mShapeDialog == null) {
            mShapeDialog = new AlertDialog.Builder(this)
                    .setTitle("选择形状")
                    .setSingleChoiceItems(new String[]{"路径", "直线", "矩形", "圆形", "实心矩形", "实心圆"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            mDoodleView.setType(DoodleView.ActionType.Path);
                                            break;
                                        case 1:
                                            mDoodleView.setType(DoodleView.ActionType.Line);
                                            break;
                                        case 2:
                                            mDoodleView.setType(DoodleView.ActionType.Rect);
                                            break;
                                        case 3:
                                            mDoodleView.setType(DoodleView.ActionType.Circle);
                                            break;
                                        case 4:
                                            mDoodleView.setType(DoodleView.ActionType.FillEcRect);
                                            break;
                                        case 5:
                                            mDoodleView.setType(DoodleView.ActionType.FilledCircle);
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mShapeDialog.show();
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private String getHexString(int color) {
        String s = "#";
        int colorStr = (color & 0xff000000) | (color & 0x00ff0000) | (color & 0x0000ff00) | (color & 0x000000ff);
        s = s + Integer.toHexString(colorStr);
        return s;
    }

    public void pen_color(View v) {

        if (chip5.getText().equals("画笔")) {
            Toast.makeText(this, "橡皮擦模式不能修改!", Toast.LENGTH_SHORT).show();
            return;
        }

        ColorPickerDialogBuilder
                .with(this)
                .setTitle("选择画笔颜色")
                .initialColor(Color.parseColor("#ffffff"))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("选择", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {

                        mDoodleView.setColor(getHexString(selectedColor));

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }


    /**
     * 提交与获取
     *
     * @param key
     * @param value
     */
    public void set_sharedString(String key, String value) {
        SharedPreferences sp = this.getSharedPreferences("data", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String get_sharedString(String key, String moren) {
        SharedPreferences sp = this.getSharedPreferences("data", 0);
        return sp.getString(key, moren);
    }


    public void board_forward(View view) {
        mDoodleView.forward();
    }


    public void board_undo(View view) {
        mDoodleView.undo();
    }

    public void board_reset(View view) {

        if (chip5.getText().equals("画笔")) {
            Toast.makeText(this, "橡皮擦模式不能修改!", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialDialog.Builder(this)
                .title("警告")
                .content("这将会重置你的画板，包括画板内容、画笔颜色、画笔形状、画笔大小!")
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        mDoodleView.reset();

                        mDoodleView.setColor("#000000");
                        mDoodleView.setType(DoodleView.ActionType.Path);
                        mDoodleView.setSize(13);

                        f5();
                    }
                })
                .negativeText("取消")
                .show();
    }


    public void pen_size(View view) {
        if (chip5.getText().equals("画笔")) {
            Toast.makeText(this, "橡皮擦模式不能修改!", Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialDialog.Builder(this)
                .title("请输入画笔大小")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("", "" + mDoodleView.getCurrentSize(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        mDoodleView.setSize(Integer.parseInt("" + input));
                        chip2.setText("画笔大小:" + mDoodleView.getCurrentSize());
                    }
                })
                .positiveText("确定")
                .neutralText("取消")
                .show();
    }

    public void pen_xingzhuang(View view) {
        if (chip5.getText().equals("画笔")) {
            Toast.makeText(this, "橡皮擦模式不能修改!", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialDialog.Builder(this)
                .title("选择画笔形状")
                .items(new String[]{"路径", "直线", "矩形", "圆形", "实心矩形", "实心圆"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                mDoodleView.setType(DoodleView.ActionType.Path);
                                break;
                            case 1:
                                mDoodleView.setType(DoodleView.ActionType.Line);
                                break;
                            case 2:
                                mDoodleView.setType(DoodleView.ActionType.Rect);
                                break;
                            case 3:
                                mDoodleView.setType(DoodleView.ActionType.Circle);
                                break;
                            case 4:
                                mDoodleView.setType(DoodleView.ActionType.FillEcRect);
                                break;
                            case 5:
                                mDoodleView.setType(DoodleView.ActionType.FilledCircle);
                                break;
                            default:
                                break;
                        }
                        chip3.setText("画笔形状:" + mDoodleView.getmActionType());
                    }
                })
                .show();
    }





}

