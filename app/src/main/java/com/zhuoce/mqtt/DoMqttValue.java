package com.zhuoce.mqtt;

import android.content.Context;
import android.icu.text.UnicodeSetSpanner;
import android.util.Log;
import android.widget.Toast;


import com.rairmmd.andmqtt.AndMqtt;
import com.rairmmd.andmqtt.MqttSubscribe;
import com.zhuoce.screen.ChuanKouCaoZuoUtils;
import com.zhuoce.screen.ConstanceValue;
import com.zhuoce.screen.Notice;
import com.zhuoce.screen.RxBus;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class DoMqttValue {

    public Context context;//上下文
    public String message;//消息


    List<String> stringList;

    public DoMqttValue(Context context) {
        this.context = context;
        if (AndMqtt.getInstance() == null) {
            return;
        }
        if (!AndMqtt.getInstance().isConnect()) {
            return;
        }

    }

    public void doValue(Context context, String topic, String message) {


        if (message.charAt(0) == 'i') {

            message = "i010111_010221.";

            String message1 = message.substring(1, message.length() - 1);

            String[] data = message1.split("_");


        } else if (message.charAt(0) == 'k') {
            String zhiLingMa = message.substring(0, 1);
            String xiangZiSuoHao = message.substring(1, 5);//箱子锁号

            Log.i("LIUCHENG", "执行硬件命令后的应答");
            Log.i("LIUCHENG", "请求码：" + zhiLingMa);
            Log.i("LIUCHENG", "箱子锁号" + xiangZiSuoHao);
            Log.i("LIUCHENG", "执行结果:" + "成功");

        } else if (message.charAt(0) == 'M') {

            String leixing = message.substring(0, 3);

            if (leixing.equals("M01")) {

                //        MqttZhiLing.publish(ccidAddr,"M01"+"01011");

                String zhiLingMa = "M01";
                String guiHao = message.substring(3, 5);
                String suoHao = message.substring(5, 7);

                Log.i("LIUCHENG", "---------接收到后台传递的开箱命令----------");
                Log.i("LIUCHENG", "指令码：" + "M01");
                Log.i("LIUCHENG", "箱子号：" + guiHao);
                Log.i("LIUCHENG", "锁号 ：" + suoHao);

                ChuanKouCaoZuoUtils.kaiGui(Integer.valueOf(guiHao), Integer.valueOf(suoHao), Integer.valueOf("1"));


            } else if (leixing.equals("M02")) {


                String messageBean = message.substring(3, message.length() - 1);

                String[] arr = messageBean.split("_");


                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        int i = 0;
                        while (i < arr.length) {


                            String guiHao = arr[i].substring(0, 2);
                            String suoHao = arr[i].substring(2, 4);
                            ChuanKouCaoZuoUtils.kaiGui(Integer.valueOf(guiHao), Integer.valueOf(suoHao), Integer.valueOf(1));
                            Log.i("LIUCHENG", "柜号：" + guiHao + "锁号：" + suoHao);
                            Log.i("LIUCHENG", "开");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            i = i + 1;

                        }

                    }
                }).start();


            } else if (leixing.equals("M03")) {

                ChuanKouCaoZuoUtils.kaiGui(0, 0, Integer.valueOf(4));

            }
        } else if (message.charAt(0) == 'O') {


            String str = message.substring(1, message.length());

            Notice notice = new Notice();
            notice.content = str;
            notice.type = ConstanceValue.ERWEIMA;
            RxBus.getDefault().sendRx(notice);

            String strContent = String.valueOf(notice.content);

            Log.i("LIUCHENG", "发送大O" + strContent);
        } else if (message.charAt(0) == 'o') {

//            String str = "O{https://shop.hljsdkj.com?ccid=124n125x99D120J124J99J107J106x102J104x113n111J112J122x123E114D112n122g}.";
//            Log.i("LIUCHENG", "接收小o发送的：" + str);
//            MqttZhiLing.publish(Addr.ccidAddr, str);

        } else if (message.charAt(0) == 'r') {
//            String str = "R1112_08_09_10_11_11_11_23_12_10_11.";
//            Log.i("LIUCHENG", "接收到了小r的数据");
//            MqttZhiLing.publish(Addr.ccidAddr, str);
        } else if (message.charAt(0) == 'R') {

            Notice notice = new Notice();
            notice.content = message;
            notice.type = ConstanceValue.SHULIANG;
            RxBus.getDefault().sendRx(notice);
            Log.i("LIUCHENG", "发送大R数据：" + notice.content);
        }
    }
}
