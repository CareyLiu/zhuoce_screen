package com.zhuoce.screen;

public class YingJianZhiLing {


    public static byte[] sendZhiLing(int addr, int num, int zlm) {
        return GuiCaoZuo(0x99, addr, num, zlm, 0, 0);
    }

    /**
     * @param touxinxi 头信息 默认0x99
     * @param ADDR     箱体信息 b1开始
     * @param NUM      箱子号1-12
     * @param ZLM      1=开,2=关,3=查询，4=群开指令5=回复出厂
     * @param DATA1    备用字段
     * @param DATA2    备用字段
     */
    private static byte[] GuiCaoZuo(int touxinxi, int ADDR, int NUM, int ZLM, int DATA1, int DATA2) {


        // int leijiahe = 0x99 + 0xb1 + 1 + 1 + 00;
        int leijiahe = touxinxi + ADDR + NUM + ZLM + DATA1 + DATA2;
        int leijiahe_a = leijiahe / 256;
        int leijiahe_b = leijiahe % 256;


        byte[] to_send = openDevice((byte) touxinxi, (byte) ADDR, (byte) NUM, (byte) ZLM, (byte) DATA1, (byte) DATA2, (byte) leijiahe_a, (byte) leijiahe_b);


        //写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
        //int retval = MyApp.driver.WriteData(to_send, to_send.length);
        return to_send;

    }

    public static byte[] openDevice(byte touxinxi, byte ADDR, byte NUM, byte ZLM, byte DATA1, byte DATA2, byte LRC2_a, byte LRC2_b) {

        byte[] bytes = new byte[8];

        bytes[0] = touxinxi;
        bytes[1] = ADDR;
        bytes[2] = NUM;
        bytes[3] = ZLM;
        bytes[4] = DATA1;
        bytes[5] = DATA2;
        bytes[6] = LRC2_a;
        bytes[7] = LRC2_b;
        return bytes;
    }


}
