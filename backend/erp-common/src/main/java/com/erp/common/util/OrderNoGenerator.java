package com.erp.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Date;

public class OrderNoGenerator {

    public static String generate(String prefix) {
        String datePart = DateUtil.format(new Date(), "yyyyMMdd");
        return StrUtil.format("{}-{}-", prefix, datePart);
    }
}
