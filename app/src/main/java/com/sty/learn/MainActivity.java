package com.sty.learn;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.sty.learn.utils.EnterAmountTextWatcher;
import com.sty.learn.utils.PosStyleKeyboardUtil;
import com.sty.learn.view.CustomEditText;
import com.sty.learn.view.SoftKeyboardPosStyle;

public class MainActivity extends AppCompatActivity {

    private CustomEditText edtAmount; //输入框金额
    private FrameLayout flkeyBoardContainer;
    private SoftKeyboardPosStyle softKeyboard; //软键盘

    private static final int KEY_BOARD_CANCEL = 1;
    private static final int KEY_BOARD_OK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setListeners();
    }

    protected void initViews(){
        edtAmount = (CustomEditText) findViewById(R.id.amount_edtext);
        edtAmount.setHint("0.00");
        edtAmount.setInputType(InputType.TYPE_NULL);
        edtAmount.setIMEEnabled(false, true);

        flkeyBoardContainer = (FrameLayout) findViewById(R.id.fl_trans_softkeyboard);
        softKeyboard = (SoftKeyboardPosStyle) findViewById(R.id.soft_keyboard_view);
    }

    protected void setListeners(){
        edtAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtAmount.setFocusable(true);
                PosStyleKeyboardUtil.show(MainActivity.this, flkeyBoardContainer);
                return false;
            }
        });

        edtAmount.addTextChangedListener(new EnterAmountTextWatcher());

        edtAmount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() ==KeyEvent.ACTION_UP){
                    if(keyCode == KeyEvent.KEYCODE_BACK){
                        handler.sendEmptyMessage(KEY_BOARD_CANCEL);
                    }else if(keyCode == KeyEvent.KEYCODE_ENTER){
                        handler.sendEmptyMessage(KEY_BOARD_OK);
                    }
                }
                return false;
            }
        });

    }

    protected Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            handleMsg(msg);
        }
    };

    protected void handleMsg(Message msg){
        switch(msg.what){
            case KEY_BOARD_OK:
                String amount = edtAmount.getText().toString().trim();
                if(amount != null){
                    // TODO: 2017/6/19/0019
                }else{
                    PosStyleKeyboardUtil.hide(MainActivity.this, flkeyBoardContainer);
                }
                edtAmount.setFocusable(true);
                edtAmount.setFocusableInTouchMode(true);
                edtAmount.requestFocus();
                break;
            case KEY_BOARD_CANCEL:
                edtAmount.setText("");
                PosStyleKeyboardUtil.hide(MainActivity.this, flkeyBoardContainer);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(flkeyBoardContainer.getVisibility() == View.VISIBLE){
                PosStyleKeyboardUtil.hide(MainActivity.this, flkeyBoardContainer);
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
