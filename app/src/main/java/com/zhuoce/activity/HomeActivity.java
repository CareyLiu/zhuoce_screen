package com.zhuoce.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gyf.barlibrary.ImmersionBar;
import com.rairmmd.andmqtt.AndMqtt;
import com.rairmmd.andmqtt.MqttPublish;
import com.zhuoce.activity.utils.MoNiUtils;
import com.zhuoce.mqtt.Addr;
import com.zhuoce.mqtt.MqttZhiLing;
import com.zhuoce.screen.ChuanKouCaoZuoUtils;
import com.zhuoce.screen.ConstanceValue;
import com.zhuoce.screen.HexToText;
import com.zhuoce.screen.KaiGuiOrQuBaoSuccessActivity;
import com.zhuoce.screen.Notice;
import com.zhuoce.screen.R;
import com.zhuoce.screen.RxBus;
import com.zhuoce.screen.RxUtils;

import com.zhuoce.tool.CeShiGongGongFangFa;
import com.zhuoce.tool.Tools;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.zhuoce.mqtt.Addr.ccidAddr;


public class HomeActivity extends Activity {


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

    @BindView(R.id.rl_tuige)
    RelativeLayout rlTuige;

    @BindView(R.id.rl_shoujihao)
    RelativeLayout rlShoujihao;
    @BindView(R.id.rl_shoujimima)
    RelativeLayout rlShoujimima;

    @BindView(R.id.rl_tupian)
    RelativeLayout rlTupian;
    @BindView(R.id.btn_qubao)
    Button btnQubao;
    @BindView(R.id.iv_erweima)
    ImageView ivErweima;
    @BindView(R.id.btn_yijianduokai)
    Button btnYijianduokai;


    private String cunOrQu;

    private String cunJianHuaShu = "请输入“存”件码";
    private String quJianMaHuaShu = "请输入“取”件码";

    private String saoMaHuoQuCunJianMa = "扫码获取“存”件码";
    private String saoMaHuoQuQuJianMa = "扫码获取“取”件码";
    protected CompositeSubscription _subscriptions = new CompositeSubscription();
    ImmersionBar mImmersionBar;
    public boolean xianChengFlag = true;
    ReadThread mReadThread;
    GThread gThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home_xilan);
        ButterKnife.bind(this);


        gThread = new GThread();
        gThread.start();

        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.titleBar(R.id.rl_tupian).init();

        _subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(_subscriptions);
        _subscriptions.add(toObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Notice>() {
            @Override
            public void call(Notice message) {
                if (message.type == ConstanceValue.ERWEIMA) {
                    String str = (String) message.content;
                    Log.i("LIUCHENG", "接收大O发来的信息：" + str);
                    String str1 = "";


                    Pattern p = Pattern.compile("\\{([^}]*)\\}");
                    Matcher m = p.matcher(str);
                    while (m.find()) {

                        System.out.println(m.group(1));//获取该次匹配中组(),正则表达式中只有一个(),即只分了一个组
                        str1 = m.group(1);
                    }

                    //Toast.makeText(HomeActivity.this, str1, Toast.LENGTH_LONG).show();
                    //  Bitmap bitmap = ZxingUtils.createQRImage(str, 200, 200);
                    Bitmap bitmap = Tools.createQRImage(HomeActivity.this, str1, null);
                    ivErweima.setImageBitmap(bitmap);
                } else if (message.type == ConstanceValue.SHULIANG) {
                    String data = String.valueOf(message.content);


                    String str = String.valueOf(message.content).substring(3, data.length() - 1);
                    Log.i("LIUCHENG", "接收大R发送来的信息:" + str);

                    String xiangZiShuLiang = data.substring(1, 3);
                    String[] shuZu = str.split("_");


                    Log.i("LIUCHENG", "箱子数量：" + xiangZiShuLiang);
                    for (int i = 0; i < shuZu.length; i++) {
                        Log.i("LIUCHENG", "第" + i + "个数据：" + shuZu[i]);
                    }

                }
            }
        }));

        cunOrQu = "2";
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

        btnYijianduokai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoNiUtils.moNiYiJianDuoKai();
            }
        });

        try {
            ChuanKouCaoZuoUtils.getSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChuanKouCaoZuoUtils.lianJieChuanKou();


        //  CeShiGongGongFangFa.kaiQiFuWu(HomeActivity.this);
    }


    /**
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭当前Activity.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();


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
            R.id.btn_seven, R.id.btn_eight, R.id.btn_nine, R.id.btn_zero, R.id.rl_tuige, R.id.btn_qubao})
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

                    shouJiMiMaGetNet();
                }
                break;

            case R.id.rl_wangjimima:
                ChuanKouCaoZuoUtils.kaiGui(1, 3, 3);
                if (mReadThread == null) {
                    mReadThread = new ReadThread();
                    mReadThread.start();
                }

                break;
        }
    }

    public void shouJiMiMaGetNet() {
        String shouJiHao = tvShoujihao.getText().toString();
        String shouJiMiMa = tvShoujimima.getText().toString();
        if (shouJiHao.equals("1111") && shouJiMiMa.equals("1111")) {
            ChuanKouCaoZuoUtils.kaiGui(1, 2, 3);
            KaiGuiOrQuBaoSuccessActivity.actionStart(HomeActivity.this, "qubao");

        }

        Log.i("LIUCHENG", "后台mqtt");
        Log.i("LIUCHENG", "指令码" + "m01");
        Log.i("LIUCHENG", "手机号：" + shouJiHao);
        Log.i("LIUCHENG", "密码：" + shouJiMiMa);
        Log.i("LIUCHENG", "模拟后台给我数据");
        Log.i("LIUCHENG", "M0101011");
        Log.i("LIUCHENG", "指令码：" + "M01");
        Log.i("LIUCHENG", "箱子锁号：" + "0101");
        Log.i("LIUCHENG", "类型：" + "存");

        MqttZhiLing.publish(ccidAddr, "m01" + shouJiHao + shouJiMiMa);
        MqttZhiLing.publish(ccidAddr, "M01" + "01011");
    }

    public void quJianMaGetNet() {
        // TODO: 2022-03-21 校验serverid ccid
        String quJianMa = tvShoujihao.getText().toString();
        if (quJianMa.equals("111111")) {
            ChuanKouCaoZuoUtils.kaiGui(1, 1, 3);
            KaiGuiOrQuBaoSuccessActivity.actionStart(HomeActivity.this, "qubao");
            String str = "m" + tvShoujihao.getText().toString() + tvShoujimima.getText().toString();

            AndMqtt.getInstance().publish(new MqttPublish()
                    .setMsg(str)
                    .setQos(2).setRetained(false)
                    .setTopic(ccidAddr), new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        }
    }


    /**
     * @param context 上下文
     * @param cunOrQu 2 手机号 密码 1 取件码
     */
    public static void actionStart(Context context, String cunOrQu) {
        Intent intent = new Intent(context, HomeActivity.class);
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

        if (thread != null) {
            thread.interrupt();
        }
        if (gThread != null) {
            gThread.interrupt();
        }
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
    HashMap<Integer, String> hashMap;

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

                        Log.i("QuJianActivity", HexToText.byteArrToHex(buffer) + "");

                        String strHex = HexToText.byteArrToHex(buffer);

                        if (strHex.charAt(0) == '8' && strHex.charAt(1) == '8') {

                            hashMap = new HashMap<>();
                            for (int i = 0; i < 24; i = i + 2) {
                                hashMap.put(i / 2, strHex.charAt(i + 4) + "" + strHex.charAt(i + 5));

                            }


                        }

                        for (int i = 0; i < 12; i++) {
                            Log.i("QuJianActivity", "第" + String.valueOf(i + 1) + "个箱子对应的号是：" + hashMap.get(i) + "");
                        }


                        // flush_buffer
                        Arrays.fill(buffer, (byte) 0);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private boolean flag = true;//中断线程

    public class GThread extends Thread {
        int i = 0;

        @Override
        public void run() {
            super.run();
            while (flag) {
                try {
                    i = i + 1;
                    if (i % 60 == 0) {

                        MqttZhiLing.publish(ccidAddr, "g.");
                        Log.i("线程G", "g$");

                    }
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
