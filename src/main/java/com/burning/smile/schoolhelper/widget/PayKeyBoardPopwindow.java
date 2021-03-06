package com.burning.smile.schoolhelper.widget;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.mine.ModifyPayPassActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by smile on 2017/5/15.
 */
public class PayKeyBoardPopwindow extends Dialog implements
        OnClickListener {

    private Context mContext;

    private Stack<Integer> mNumberStack;//保存输入的数字
    private List<TextView> mNumberViewList;//保存密码显示的几个text
    private final static int NUMBER_COUNT = 6;

    private TextView mNumber1TextView;
    private TextView mNumber2TextView;
    private TextView mNumber3TextView;
    private TextView mNumber4TextView;
    private TextView mNumber5TextView;
    private TextView mNumber6TextView;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;
    private TextView tv7;
    private TextView tv8;
    private TextView tv9;
    private TextView tv0;
    private TextView clear;
    private ImageView delete;
    private boolean mIsPassword = true;
    private final static String PASSWORD_NUMBER_SYMBOL = "●";

    private ImageView ivLeft;

    private OnInputNumberCodeCallback mCallback; // 返回结果的回调

    private View mMenuView;
    private View view;
    private StringBuilder codeBuilder = new StringBuilder();
    private TextView forgetPass;


    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    public PayKeyBoardPopwindow(Context mContext, int theme, OnInputNumberCodeCallback mCallback) {
        super(mContext,theme);
        this.mContext = mContext;
        this.mCallback = mCallback;

        mNumberStack = new Stack<>();
        mNumberViewList = new ArrayList<TextView>();

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.view_pay_keyboard, null);


        mNumber1TextView = (TextView) mMenuView.findViewById(R.id.num1);
        mNumber2TextView = (TextView) mMenuView.findViewById(R.id.num2);
        mNumber3TextView = (TextView) mMenuView.findViewById(R.id.num3);
        mNumber4TextView = (TextView) mMenuView.findViewById(R.id.num4);
        mNumber5TextView = (TextView) mMenuView.findViewById(R.id.num5);
        mNumber6TextView = (TextView) mMenuView.findViewById(R.id.num6);
        ivLeft = (ImageView) mMenuView.findViewById(R.id.backIV);


        mNumberViewList.add(mNumber1TextView);
        mNumberViewList.add(mNumber2TextView);
        mNumberViewList.add(mNumber3TextView);
        mNumberViewList.add(mNumber4TextView);
        mNumberViewList.add(mNumber5TextView);
        mNumberViewList.add(mNumber6TextView);

        tv1 = (TextView) mMenuView.findViewById(R.id.tv_1);
        tv2 = (TextView) mMenuView.findViewById(R.id.tv_2);
        tv3 = (TextView) mMenuView.findViewById(R.id.tv_3);
        tv4 = (TextView) mMenuView.findViewById(R.id.tv_4);
        tv5 = (TextView) mMenuView.findViewById(R.id.tv_5);
        tv6 = (TextView) mMenuView.findViewById(R.id.tv_6);
        tv7 = (TextView) mMenuView.findViewById(R.id.tv_7);
        tv8 = (TextView) mMenuView.findViewById(R.id.tv_8);
        tv9 = (TextView) mMenuView.findViewById(R.id.tv_9);
        clear = (TextView) mMenuView.findViewById(R.id.clear);
        tv0 = (TextView) mMenuView.findViewById(R.id.tv_0);
        delete = (ImageView) mMenuView.findViewById(R.id.deleteIv);

        forgetPass = (TextView)mMenuView.findViewById(R.id.forgetPassword);

        ivLeft.setOnClickListener(this);
        tv1.setOnClickListener(this);

        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
        tv9.setOnClickListener(this);
        clear.setOnClickListener(this);
        tv0.setOnClickListener(this);
        delete.setOnClickListener(this);
        forgetPass.setOnClickListener(this);


        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);

        Window window = getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.BottomToTop);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.popLayout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        this.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backIV:
                dismiss();
                break;
            case R.id.tv_1:
                mNumberStack.push(1);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();
                }
                break;
            case R.id.tv_2:
                mNumberStack.push(2);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();
                }
                break;
            case R.id.tv_3:
                mNumberStack.push(3);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();
                }
                break;
            case R.id.tv_4:
                mNumberStack.push(4);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();
                }
                break;
            case R.id.tv_5:
                mNumberStack.push(5);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();

                }
                break;
            case R.id.tv_6:
                mNumberStack.push(6);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();

                }
                break;
            case R.id.tv_7:
                mNumberStack.push(7);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();

                }
                break;
            case R.id.tv_8:
                mNumberStack.push(8);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();

                }
                break;
            case R.id.tv_9:
                mNumberStack.push(9);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();
                }
                break;
            case R.id.clear:
                clearnNumber();
                refreshNumberViews(mNumberStack);
                break;
            case R.id.tv_0:
                mNumberStack.push(0);
                refreshNumberViews(mNumberStack);
                if (mNumberStack.size() == NUMBER_COUNT) {
                    for (int number : mNumberStack) {
                        codeBuilder.append(number);
                    }
                    mCallback.onInputFinish(codeBuilder.toString());
                    dismiss();
                }
                break;
            case R.id.deleteIv:
                deleteNumber();
                refreshNumberViews(mNumberStack);
                break;
            case R.id.forgetPassword:
                  mContext.startActivity(new Intent(mContext, ModifyPayPassActivity.class));
                break;
            default:
                break;
        }
    }


    /**
     * 返回输出的结果
     */
    public interface OnInputNumberCodeCallback {
        void onInputFinish(String input);

    }

    /**
     * 清空mNumberStack的内容并刷新密码格
     */
    public void clearnNumber() {
        mNumberStack.clear();
        refreshNumberViews(mNumberStack);
    }

    /**
     * 删除密码位数
     */
    public void deleteNumber() {
        if (mNumberStack.empty() || mNumberStack.size() > NUMBER_COUNT) {
            return;
        }
        mNumberStack.pop();
    }


    /**
     * 刷新输入框显示
     *
     * @param mNumberStack
     */
    public void refreshNumberViews(Stack<Integer> mNumberStack) {

        for (int i = 0, size = mNumberViewList.size(); i < size; i++) {
            int numSize = mNumberStack.size();
            if (i < numSize) {
                if (mIsPassword) {
                    mNumberViewList.get(i).setText(PASSWORD_NUMBER_SYMBOL);
                } else {
                    mNumberViewList.get(i).setText(String.valueOf(mNumberStack.get(i)));
                }
            } else {
                mNumberViewList.get(i).setText("");
            }
        }

    }


}