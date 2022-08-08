package com.test;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTest {

    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void zhengZeCeShi() {

        String s  = "{one}";
        Pattern p = Pattern.compile("\\{([^}]*)\\}");
        Matcher m = p.matcher(s);
        while (m.find()) {
            System.out.println(m.group(1) );//获取该次匹配中组(),正则表达式中只有一个(),即只分了一个组
        }
    }
}
