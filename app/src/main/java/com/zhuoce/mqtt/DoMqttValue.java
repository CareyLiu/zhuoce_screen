package com.zhuoce.mqtt;

import android.content.Context;
import android.icu.text.UnicodeSetSpanner;
import android.util.Log;
import android.widget.Toast;


import com.rairmmd.andmqtt.AndMqtt;
import com.rairmmd.andmqtt.MqttSubscribe;
import com.zhuoce.screen.ChuanKouCaoZuoUtils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

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
        AndMqtt.getInstance().subscribe(new MqttSubscribe().setQos(2).setTopic(Addr.ccidAddr), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Toast.makeText(context, "订阅成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            }
        });
    }

    public void doValue(Context context, String topic, String message) {


        if (message.charAt(0) == 'i') {

            message = "i010111_010221.";

            String message1 = message.substring(1, message.length() - 1);

            String[] data = message1.split("_");


        } else if (message.charAt(0) == 'k') {

        } else if (message.charAt(0) == 'M') {

            String leixing = message.substring(0, 3);

            if (leixing.equals("M01")) {

                String zhiLingMa = "M01";
                String xiangZiHao = message.substring(3, 7);
                ChuanKouCaoZuoUtils.kaiGui(Integer.valueOf(xiangZiHao), Integer.valueOf(zhiLingMa));
                String cunOrQu = message.substring(7, 8);
            } else if (leixing.equals("M02")) {

                String zhiLingMa = "M02";

                String messageBean = message.substring(3, message.length() - 1);

                String[] arr = messageBean.split("_");


                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (true) {
                            for (int i = 0; i < arr.length; i++) {

                                String xiangZiHao = arr[i].substring(0, 4);
                                ChuanKouCaoZuoUtils.kaiGui(Integer.valueOf(xiangZiHao), Integer.valueOf(1));
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }).start();


            } else if (leixing.equals("M03")) {
                String messageBean = message.substring(3, message.length() - 1);

                String[] arr = messageBean.split("_");
                String xiangZiNumber = "01";
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (true) {
                            for (int i = 0; i < 12; i++) {
                                String xiangZiHao;
                                if (i < 10) {
                                    xiangZiHao = xiangZiNumber + "0" + i;
                                } else {
                                    xiangZiHao = xiangZiNumber + i;
                                }
                                ChuanKouCaoZuoUtils.kaiGui(Integer.valueOf(xiangZiHao), Integer.valueOf(4));
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }).start();

            }
        }


    }
}
