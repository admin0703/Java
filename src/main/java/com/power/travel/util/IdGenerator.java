package com.power.travel.util;

import java.util.UUID;

//生成用户唯一id，便于根据id存贮酒店、攻略、路线等信息
public class IdGenerator {

    public static String id() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
