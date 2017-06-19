package com.sty.learn.view;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import com.sty.learn.R;
import com.sty.learn.utils.SimpleStyleKeyboardUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by shity on 2017/6/16/0016.
 */

public class CustomEditText extends EditText {
    Instrumentation in;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        in = new Instrumentation();
        this.setLongClickable(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyEditText);
        a.recycle();
        setLongClickable(false);
        setTextIsSelectable(false);
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    public void setIMEEnabled(boolean enable, boolean showCursor) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }
        if (methodName == null) {
            this.setInputType(InputType.TYPE_NULL);
        }
        if (!enable) {
            if (showCursor) {
                ((Activity) getContext()).getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                try {
                    Class<CustomEditText> cls = CustomEditText.class;
                    Method setSoftInputShownOnFocus;
                    setSoftInputShownOnFocus = cls.getMethod(methodName, boolean.class);
                    setSoftInputShownOnFocus.setAccessible(true);
                    setSoftInputShownOnFocus.invoke(this, false);
                } catch (NoSuchMethodException e) {
                    this.setInputType(InputType.TYPE_NULL);
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                this.setInputType(InputType.TYPE_NULL);
            }
        }
    }

    /**
     * requestfocus+相应的et被点击
     * @param keyboardUtil
     *            默认简洁键盘
     */
    public void requestFocusAndTouch(SimpleStyleKeyboardUtil keyboardUtil) {
        keyboardUtil.showKeyBoardLayout(this);
        this.requestFocus();
    }
    /**
     *
     * @param keyboardUtil
     * @param keyboardType
     *      选择键盘类型{@linkplain com.sty.learn.utils.SimpleStyleKeyboardUtil#TYPE_DEFAULT}
     *                  {@linkplain com.sty.learn.utils.SimpleStyleKeyboardUtil#TYPE_PAXSTYLE}
     *      (注：也可以给customEditText设置一个选择不同键盘类型的属性以简洁代码)
     */
    public void requestFocusAndTouch(SimpleStyleKeyboardUtil keyboardUtil, int keyboardType) {
        keyboardUtil.showKeyBoardLayout(this, keyboardType);
        this.requestFocus();
    }
}
