package com.zhuoce.screen;

import java.util.ArrayList;

public class zcBoard {
    static  public ArrayList<String> getBoardName(){        //这是主板名称。点击后进入相应的页面
        ArrayList<String> str=new ArrayList<String>();
        str.add("ZC-20A");
        str.add("ZC-40A");
        str.add("ZC-64A");
        str.add("ZC-83A");
        str.add("ZC-83E");
        str.add("ZC-328");
        str.add("ZC-328E");
        str.add("ZC-328S");
        str.add("ZC-339");
        return str;
    }
    static public ArrayList<String> getTTYName(){
    	ArrayList<String> str=new ArrayList<String>();
    	str.add("/dev/ttyS2");//20a  0
        str.add("/dev/ttyS2");//40a
        str.add("/dev/ttyS2");       //64a
        str.add("/dev/ttyS2");//83a
        str.add("/dev/ttyS2");//83e
        str.add("/dev/ttyS3");//328  //5
        str.add("/dev/ttyS3");//328e
        str.add("/dev/ttyS3");//328s
        str.add("/dev/ttyS4");//339  //8
    	return str;
    }
}
