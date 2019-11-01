package com.play.accompany.bean

/**
 * id       :音频的唯一标示，删除使用
audioUrl : 音频 url地址
imgUrl   : 图片 url地址
title    : 标题
content  : 文字泡,可以为空
likeNum  : 点赞的数量
 */

data class ResponseSpeakBean(val id: String, val audioUrl: String,
                             val imgUrl: String, val title: String, val content: String,
                             val likeNum: Int)
