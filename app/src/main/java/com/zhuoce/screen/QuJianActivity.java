package com.zhuoce.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.gyf.barlibrary.ImmersionBar;
import com.zhuoce.ZxingUtils;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class QuJianActivity extends Activity {


    Thread thread;

    public int baudRate = 9600;//波特率
    public byte baudRate_byte;
    public byte stopBit = 1;//停止位
    public byte dataBit = 8;//数据位
    public byte parity = 0;//比特位
    public byte flowControl = 0;//控制流

    @BindView(R.id.tv_shoujihao)
    TextView tvShoujihao;
    @BindView(R.id.tv_shoujimima)
    TextView tvShoujimima;
    @BindView(R.id.btn_one)
    Button btnOne;
    @BindView(R.id.btn_two)
    Button btnTwo;
    @BindView(R.id.btn_three)
    Button btnThree;
    @BindView(R.id.btn_four)
    Button btnFour;
    @BindView(R.id.btn_five)
    Button btnFive;
    @BindView(R.id.btn_six)
    Button btnSix;
    @BindView(R.id.btn_seven)
    Button btnSeven;
    @BindView(R.id.btn_eight)
    Button btnEight;
    @BindView(R.id.btn_nine)
    Button btnNine;
    @BindView(R.id.btn_zero)
    Button btnZero;

    @BindView(R.id.ll_fanhui)
    LinearLayout llFanhui;
    //    @BindView(R.id.ll_queding)
//    LinearLayout llQueding;
    @BindView(R.id.tv_daojishifanhui)
    TextView tvDaojishifanhui;
    @BindView(R.id.rl_wangjimima)
    RelativeLayout rlWangjimima;
    @BindView(R.id.rl_tuige)
    RelativeLayout rlTuige;

    @BindView(R.id.rl_shoujihao)
    RelativeLayout rlShoujihao;
    @BindView(R.id.rl_shoujimima)
    RelativeLayout rlShoujimima;

    @BindView(R.id.rl_tupian)
    RelativeLayout rlTupian;

    @BindView(R.id.tv_wangji)
    TextView tvWangji;
    @BindView(R.id.btn_qubao)
    Button btnQubao;

    private String cunOrQu;

    private String cunJianHuaShu = "请输入“存”件码";
    private String quJianMaHuaShu = "请输入“取”件码";

    private String saoMaHuoQuCunJianMa = "扫码获取“存”件码";
    private String saoMaHuoQuQuJianMa = "扫码获取“取”件码";
    protected CompositeSubscription _subscriptions = new CompositeSubscription();
    ImmersionBar mImmersionBar;
    public boolean xianChengFlag = true;
    ReadThread mReadThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shoujimima);
        ButterKnife.bind(this);
//        tvShoujihao.setInputType(InputType.TYPE_NULL);
//        tvShoujimima.setInputType(InputType.TYPE_NULL);
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.titleBar(R.id.rl_tupian).init();

        _subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(_subscriptions);


        _subscriptions.add(toObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Notice>() {
            @Override
            public void call(Notice message) {
                if (message.type == ConstanceValue.ERWEIMA) {
                    String str = (String) message.content;
                    Toast.makeText(QuJianActivity.this, str, Toast.LENGTH_LONG).show();


                    Bitmap bitmap = ZxingUtils.createQRImage(str, 200, 200);
                    //  ivErweima.setImageBitmap(bitmap);
                }
            }
        }));


        cunOrQu = getIntent().getStringExtra("cunOrQu");
        if (cunOrQu.equals("1")) {//1取件码取件
            tvShoujihao.setHint("请输入6位取件码取件");
            rlShoujimima.setVisibility(View.GONE);

            tvWangji.setText("忘记取件码？");
        } else if (cunOrQu.equals("2")) { //2取
            tvWangji.setText("忘记手机密码？");
        }



//        if (MyApp.openDevice.equals("1")) {
//            MyApp.driver.SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
//        }


//        ivClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        btnFanhui.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //getNet();
//                // int leijiahe = 0x99 + 0xb1 + 1 + 1 + 00;
//                kaiGui(0x99, 0xb1, 1, 1, 0, 0);
//
//            }
//        });


        // 找出showCounter组件
        // TextView showCounter = (TextView) this.findViewById(R.id.t);

        // handler类
        final handler handle = new handler(tvDaojishifanhui);
        // 线程
        thread = new Thread() {
            public void run() {
                int i = 60;
                while (xianChengFlag) {
                    // 获得消息，并写好message中内容，这里采用了回收机制
                    Message msg = handle.obtainMessage();// 1、新建空消息


                    if (i == 0) {
                        msg.obj = "over";
                    } else {
                        msg.obj = (i--) + "s";// 2、带上消息内容
                    }
                    handle.sendMessage(msg);// 3、发送消息
                    try {
                        if (thread != null || thread.isAlive()) {
                            thread.sleep(1000);
                        }

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
            }

        };
        thread.start();

        tvShoujihao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouJiHaoOrShouJiMima = "0";
                if (tvShoujihao.getHint().toString().equals("请输入手机号码")) {
                    tvShoujihao.setHint("");
                    tvShoujihao.setText("|");
                } else {
                    tvShoujihao.setText(tvShoujihao.getText().toString() + "|");
                }

            }
        });
        tvShoujimima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouJiHaoOrShouJiMima = "1";
                if (tvShoujimima.getHint().toString().equals("请输入手机密码")) {
                    tvShoujimima.setHint("");
                    tvShoujimima.setText("|");
                } else {
                    tvShoujihao.setText(tvShoujihao.getText().toString() + "|");
                }
            }
        });

        llFanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xianChengFlag = false;
                thread.interrupt();
                thread = null;
                finish();
            }
        });
//        tvShoujihao.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        tvShoujimima.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


        try {
            ChuanKouCaoZuoUtils.getSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChuanKouCaoZuoUtils.lianJieChuanKou();


    }


    /**
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭当前Activity.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //   Toast.makeText(this, "按下了back键   onBackPressed()", Toast.LENGTH_SHORT).show();
//        xianChengFlag = false;
//        this.thread.interrupt();
//        this.thread = null;

    }

    /**
     * 注册事件通知
     */
    public Observable<Notice> toObservable() {
        return RxBus.getDefault().toObservable(Notice.class);
    }

    private String shouJiHaoOrShouJiMima = "0";//0手机号 1 手机密码

    private void insertShouJiHao(String str) {

        if (tvShoujihao.getText().toString().equals("|")) {

            tvShoujihao.setText("");
        }
        if (shouJiHaoOrShouJiMima.equals("1")) {
            return;
        }
        if (tvShoujihao.getText().toString().length() == 11) {
            return;
        }


        str = tvShoujihao.getText().toString() + str + "|";
        tvShoujihao.setText(str);


        if (str.length() == 6) {
            // getNet();
        }
    }

    private void insertShouJiMiMa(String str) {
        if (shouJiHaoOrShouJiMima.equals("0")) {
            return;
        }
        if (tvShoujimima.getText().toString().equals("|")) {
            tvShoujimima.setText("");
        }

        if (tvShoujimima.getText().toString().length() == 6) {
            return;
        }


        str = tvShoujimima.getText().toString() + str + "|";
        tvShoujimima.setText(str);


        if (str.length() == 6) {
            // getNet();
        }
    }


    private void houtuishoujimima() {

        if (shouJiHaoOrShouJiMima.equals("0")) {
            return;
        }
        String str = tvShoujimima.getText().toString();

        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }


        if (str.length() == 0) {
            tvShoujimima.setText("");
            if (cunOrQu.equals("1")) {
                tvShoujimima.setHint("请输入手机密码");

            } else {

                tvShoujimima.setHint("请输入手机密码");
            }
        } else {
            tvShoujimima.setText(str);
        }
    }


    private void houtuiShouJiHao() {
        if (shouJiHaoOrShouJiMima.equals("1")) {
            return;
        }
        String str = tvShoujihao.getText().toString();
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }

        if (str.length() == 0) {
            tvShoujihao.setText("");
            if (cunOrQu.equals("1")) {
                tvShoujihao.setHint("请输入取件码");

            } else {

                tvShoujihao.setHint("请输入手机号码");
            }
        } else {
            tvShoujihao.setText(str);
        }
    }


    @OnClick({R.id.btn_one, R.id.btn_two, R.id.btn_three, R.id.btn_four, R.id.btn_five, R.id.btn_six,
            R.id.btn_seven, R.id.btn_eight, R.id.btn_nine, R.id.btn_zero, R.id.rl_tuige, R.id.btn_qubao,R.id.rl_wangjimima})
    public void onViewClicked(View view) {
        if (tvShoujihao.getText().toString().contains("|")) {
            String str = tvShoujihao.getText().toString().replace("|", "");
            tvShoujihao.setText(str);
        }

        if (tvShoujimima.getText().toString().contains("|")) {
            String str = tvShoujimima.getText().toString().replace("|", "");
            tvShoujimima.setText(str);
        }

        switch (view.getId()) {

            case R.id.btn_one:
                insertShouJiHao("1");
                insertShouJiMiMa("1");
                break;
            case R.id.btn_two:
                insertShouJiHao("2");
                insertShouJiMiMa("2");
                break;
            case R.id.btn_three:
                insertShouJiHao("3");
                insertShouJiMiMa("3");
                break;
            case R.id.btn_four:
                insertShouJiHao("4");
                insertShouJiMiMa("4");
                break;
            case R.id.btn_five:
                insertShouJiHao("5");
                insertShouJiMiMa("5");
                break;
            case R.id.btn_six:
                insertShouJiHao("6");
                insertShouJiMiMa("6");
                break;
            case R.id.btn_seven:
                insertShouJiHao("7");
                insertShouJiMiMa("7");
                break;
            case R.id.btn_eight:
                insertShouJiHao("8");
                insertShouJiMiMa("8");
                break;
            case R.id.btn_nine:
                insertShouJiHao("9");
                insertShouJiMiMa("9");
                break;
            case R.id.btn_zero:
                insertShouJiHao("0");
                insertShouJiMiMa("0");
                break;
            case R.id.rl_tuige:
                houtuiShouJiHao();
                houtuishoujimima();
                break;
            case R.id.btn_qubao:


                if (cunOrQu.equals("1")) {
                    //取件码
                    quJianMaGetNet();
                } else {
                    //手机和密码

//                    if (tvShoujihao.getText().toString().trim().length() == 11) {
//                        Toast.makeText(QuJianActivity.this, "请输入正确手机号", Toast.LENGTH_LONG).show();
//                        return;
//                    } else if (tvShoujimima.getText().toString().trim().length() == 6) {
//                        Toast.makeText(QuJianActivity.this, "请输入6位密码", Toast.LENGTH_LONG).show();
//                        return;
//                    }
                    shouJiMiMaGetNet();
                }
                break;

            case R.id.rl_wangjimima:
//                ChuanKouCaoZuoUtils.kaiGui(1, 3);
//                if (mReadThread == null) {
//                    mReadThread = new ReadThread();
//                    mReadThread.start();
//                }

            WangJiQuOrCunActivity.actionStart(QuJianActivity.this, cunOrQu);
                break;
        }
    }

    public void shouJiMiMaGetNet() {
        String shouJiHao = tvShoujihao.getText().toString();
        String shouJiMiMa = tvShoujimima.getText().toString();
        if (shouJiHao.equals("1111") && shouJiMiMa.equals("1111")) {
            ChuanKouCaoZuoUtils.kaiGui(1, 1);
            KaiGuiOrQuBaoSuccessActivity.actionStart(QuJianActivity.this,"qubao");
        }

    }

    public void quJianMaGetNet() {
        String quJianMa = tvShoujihao.getText().toString();
        if (quJianMa.equals("111111")) {
            ChuanKouCaoZuoUtils.kaiGui(1, 1);
            KaiGuiOrQuBaoSuccessActivity.actionStart(QuJianActivity.this,"qubao");
        }
    }

    /**
     * @param context 上下文
     * @param cunOrQu 2 手机号 密码 1 取件码
     */
    public static void actionStart(Context context, String cunOrQu) {
        Intent intent = new Intent(context, QuJianActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("cunOrQu", cunOrQu);
        context.startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!_subscriptions.isUnsubscribed()) {
            _subscriptions.unsubscribe();
        }

        mFlag = false;

        xianChengFlag = false;
        thread.interrupt();
    }

    public class handler extends Handler {

        TextView showCounter;

        public handler(TextView showCounter) {
            super();
            this.showCounter = showCounter;
        }

        // 处理消息
        public void handleMessage(Message msg) {
            String text = (String) msg.obj;
            Log.i("QuJianActivity", text);
            if (text.equals("over")) {
                thread.interrupt();
                finish();
            } else {
                showCounter.setText(text);
            }


        }

    }


    public boolean mFlag = true;

    //读取串口返回数据
    public class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (mFlag) {
                int size;
                try {

                    byte[] buffer = new byte[16];
                    if (ChuanKouCaoZuoUtils.mInputStream == null) {

                        break;
                    }

                    size = ChuanKouCaoZuoUtils.mInputStream.read(buffer);

                    if (size == 16) {

                        for (int i = 0; i < size; i++) {

                            Log.i("QuJianActivity", HexToText.byteArrToHex(buffer) + "");


//                            Message msg = new Message();
//                            msg.obj = buffer[i];
//                            msg.arg1 = 2;
//                            handler.sendMessage(msg);

                            // flush_buffer
                            Arrays.fill(buffer, (byte) 0);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


}
