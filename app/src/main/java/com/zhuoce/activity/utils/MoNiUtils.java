package com.zhuoce.activity.utils;

import android.util.Log;

import com.zhuoce.mqtt.Addr;
import com.zhuoce.mqtt.MqttZhiLing;

public class MoNiUtils {
    public static void moNiYiJianDuoKai() {
        Log.i("LIUCHENG", "mqtt 模拟发送:" + "M0201012_01022$");
        MqttZhiLing.publish(Addr.ccidAddr, "M0201012_01022$");
    }
}
