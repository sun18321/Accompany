package com.play.accompany.base;

import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.SPUtils;

public class BaseRequestBean {
    private String token = SPUtils.getInstance().getString(SpConstant.APP_TOKEN);
}
