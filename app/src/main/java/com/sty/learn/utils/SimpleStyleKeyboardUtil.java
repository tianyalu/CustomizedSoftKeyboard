package com.sty.learn.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sty.learn.R;
import com.sty.learn.view.SoftkeyboardSimpleStyle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

public class SimpleStyleKeyboardUtil {
    /**
     * 默认简洁键盘
     */
    public static final int TYPE_DEFAULT = 0;
    /**
     * 彩色键盘
     */
    public static final int TYPE_PAXSTYLE = 1;
    private int mTypeOfKeyboard = TYPE_DEFAULT;
    private Context mContext;
    private Activity mActivity;
    private SoftkeyboardSimpleStyle keyboardView;
    public static Keyboard mSimpleNumKeyboard;// 数字键盘
    public static Keyboard mPaxStyleNumKeyboard;// 百富风格彩色键盘
    public static Keyboard keyboard;// 提供给keyboardView 进行画

    public boolean isShow = false;
    InputFinishListener inputOver;
    KeyBoardStateChangeListener keyBoardStateChangeListener;
    private int containerId = R.id.fl_container, keyboardId = R.id.keyboard_view;// container和keyboard的默认id
    // 开始输入的键盘状态设置
    public static int inputType = 1;// 默认

    public static final int KEYBOARD_SHOW = 1;
    public static final int KEYBOARD_HIDE = 2;

    private EditText ed;
    private Handler showHandler;
    private View inflaterView;
    // private View layoutView;
    private FrameLayout mContainerView;

    /**
     * 最新构造方法，现在都用这个
     *
     * @param ctx
     *            也用于findId，键盘id写死
     */
    public SimpleStyleKeyboardUtil(Context ctx) {
        this.mContext = ctx;
        this.mActivity = (Activity) mContext;
        initKeyBoardView();
    }

    /**
     *
     * @param ctx
     *            也用于findId
     * @param idOfContainer
     *            包含者id
     * @param idOfKeyboard
     *            键盘id
     */
    public SimpleStyleKeyboardUtil(Context ctx, int idOfContainer, int idOfKeyboard) {
        this.mContext = ctx;
        this.mActivity = (Activity) mContext;
        this.containerId = idOfContainer;
        this.keyboardId = idOfKeyboard;
        initKeyBoardView();
    }

    /**
     * 弹框类，用这个
     *
     * @param view
     *            是弹框的inflaterView
     */
    public SimpleStyleKeyboardUtil(Context ctx, View view) {
        this.mContext = ctx;
        this.mActivity = (Activity) mContext;
        this.inflaterView = view;
        initKeyBoardView();
    }

    // 设置监听事件
    public void setInputOverListener(InputFinishListener listener) {
        this.inputOver = listener;
    }

    public static Keyboard getKeyBoardType() {
        return keyboard;
    }

    private void initKeyBoardView() {
        if (inflaterView != null) {
            mContainerView = (FrameLayout) inflaterView.findViewById(containerId);
        } else if (mContainerView == null) {
            mContainerView = (FrameLayout) mActivity.findViewById(containerId);
        }
        //Log.d("Test",mContainerView.toString()+"-------------------------");
        mContainerView.setVisibility(View.GONE);
        //Log.d("Test","-------------------------");
    }

    /***
     *
     *
     * @param edit
     * @return 判断输入法是否打开
     */
    public boolean setKeyBoardCursorNew(EditText edit) {
        this.ed = edit;
        boolean flag = false;

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
        if (isOpen) {
            // ((InputMethodManager)
            // mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            if (imm.hideSoftInputFromWindow(edit.getWindowToken(), 0))
                flag = true;
        }

        // act.getWindow().setSoftInputMode(
        // WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
            edit.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(edit, false);
            } catch (NoSuchMethodException e) {
                edit.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public void hideSystemKeyBoard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mContainerView.getWindowToken(), 0);
    }

    public void hideAllKeyBoard() {
        hideSystemKeyBoard();
        hideKeyboardLayout();
    }

    public int getInputType() {
        return inputType;
    }

    /**
     *
     * @return keyboardview是否在屏幕上可以占有位置
     */
    public boolean keyboardViewIsExisted() {
        if (keyboardView.getHeight() > 0 && keyboardView.getWidth() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getKeyboardState() {
        return this.isShow;
    }

    public EditText getEd() {
        return ed;
    }

    // 设置一些不需要使用这个键盘的edittext,解决切换问题
    public void setOtherEdittext(EditText... edittexts) {
        for (EditText editText : edittexts) {
            editText.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // 防止没有隐藏键盘的情况出现
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideKeyboardLayout();
                            }
                        }, 300);
                        ed = (EditText) v;
                        hideKeyboardLayout();
                    }
                    return false;
                }
            });
        }
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
            if (ed == null)
                return;
            Editable editable = ed.getText();
            // if (editable.length()>=20)
            // return;
            int start = ed.getSelectionStart();
            int end = ed.getSelectionEnd();
            String temp = editable.subSequence(0, start) + text.toString()
                    + editable.subSequence(start, editable.length());
            ed.setText(temp);
            Editable etext = ed.getText();
            Selection.setSelection(etext, start + 1);
        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onPress(int primaryCode) {

            // setPreviewEnabled 点击键盘 然后显示按键内容的白板
            // 键盘默认不弹出白板，如需要可以在此设置
            keyboardView.setPreviewEnabled(false);
            return;
        }

        @Override
        public void onKey(final int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 收起
                hideKeyboardLayout();
                if (inputOver != null)
                    inputOver.inputHasOver(primaryCode, ed);
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == 57419) {// 000
                editable.insert(start, "000");
            } else if (primaryCode == 57420 || primaryCode == 57418) {// confirm&cancel
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        new Instrumentation().sendKeyDownUpSync(primaryCode == 57418 ? KeyEvent.KEYCODE_BACK
                                : KeyEvent.KEYCODE_ENTER);
                    }
                }).start();
            } else if (primaryCode == 57421) {// 00
                editable.insert(start, "00");
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    public void showKeyboard() {
        if (keyboardView != null) {
            keyboardView.setVisibility(View.GONE);
        }
        initInputType();
        isShow = true;
        keyboardView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initKeyBoard(int keyBoardViewID) {
        mActivity = (Activity) mContext;
        if (inflaterView != null) {
            keyboardView = (SoftkeyboardSimpleStyle) inflaterView.findViewById(keyBoardViewID);
        } else {
            keyboardView = (SoftkeyboardSimpleStyle) mActivity.findViewById(keyBoardViewID);
        }
        keyboardView.setEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);
        keyboardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        });
    }

    private void initInputType() {
        // 可以在此给不同的键盘设置不同的背景颜色
        if (mTypeOfKeyboard == TYPE_DEFAULT) {
            initKeyBoard(keyboardId);
            keyboardView.setPreviewEnabled(false);
            mSimpleNumKeyboard = new Keyboard(mContext, R.xml.symbols_keyboard_num);
            setMyKeyBoard(mSimpleNumKeyboard);
        } else if (mTypeOfKeyboard == TYPE_PAXSTYLE) {
            initKeyBoard(keyboardId);
            keyboardView.setPreviewEnabled(false);
            mPaxStyleNumKeyboard = new Keyboard(mContext, R.xml.other_keyboard_num);
            setMyKeyBoard(mPaxStyleNumKeyboard);
        }

    }

    private void setMyKeyBoard(Keyboard newkeyboard) {
        keyboard = newkeyboard;
        keyboardView.setKeyboard(newkeyboard);
    }

    // 新的隐藏方法
    public void hideKeyboardLayout() {
        if (getKeyboardState() == true) {
            if (mContainerView != null)
                mContainerView.setVisibility(View.GONE);
            if (keyBoardStateChangeListener != null)
                keyBoardStateChangeListener.KeyBoardStateChange(KEYBOARD_HIDE, ed);
            isShow = false;
            hideKeyboard();
            ed = null;
        }
    }

    /**
     * @param editText
     * @param keyBoardType
     *            类型
     */
    // 新的show方法
    public void showKeyBoardLayout(final EditText editText, int keyBoardType) {
        if (editText.equals(ed) && getKeyboardState() == true && mTypeOfKeyboard == keyBoardType)
            return;
        mTypeOfKeyboard = keyBoardType;
        if (setKeyBoardCursorNew(editText)) {
            showHandler = new Handler();
            showHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show(editText);
                }
            }, 400);
        } else {
            // 直接显示
            show(editText);
        }
    }

    /**
     * @param editText
     *            默认使用简洁小键盘
     */
    // 新的show方法
    public void showKeyBoardLayout(final EditText editText) {
        if (editText.equals(ed) && getKeyboardState() == true)
            return;
        if (setKeyBoardCursorNew(editText)) {
            showHandler = new Handler();
            showHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show(editText);
                }
            }, 400);
        } else {
            // 直接显示
            show(editText);
        }
    }

    private void show(EditText editText) {
        this.ed = editText;
        if (mContainerView != null) {
            mContainerView.setVisibility(View.VISIBLE);
        }
        showKeyboard();
        if (keyBoardStateChangeListener != null)
            keyBoardStateChangeListener.KeyBoardStateChange(KEYBOARD_SHOW, editText);
    }

    public void hideKeyBoardForBack() {
        if (isShow) {
            hideAllKeyBoard();
        }
    }

    private void hideKeyboard() {
        isShow = false;
        if (keyboardView != null) {
            int visibility = keyboardView.getVisibility();
            if (visibility == View.VISIBLE) {
                keyboardView.setVisibility(View.INVISIBLE);
            }
        }
        if (mContainerView != null) {
            mContainerView.setVisibility(View.GONE);
        }
    }

    /**
     * 输入监听
     */
    public interface InputFinishListener {
        public void inputHasOver(int onclickType, EditText editText);
    }

    /**
     * 监听键盘变化
     */
    public interface KeyBoardStateChangeListener {
        public void KeyBoardStateChange(int state, EditText editText);
    }

    public void setKeyBoardStateChangeListener(KeyBoardStateChangeListener listener) {
        this.keyBoardStateChangeListener = listener;
    }
}