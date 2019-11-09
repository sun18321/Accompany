package com.play.accompany.bean

import java.io.Serializable

/**
 * id       : 音频的唯一标示，保存、修改和删除使用
userId   : 个人ID
name     : 昵称
iconUrl  : 个人头像
audioUrl : 音频 url地址
imgUrl   : 图片 url地址
title    : 标题
content  : 文字泡,可以为空
likeNum  : 点赞的数量
 */

data class ResponseSpeakBean(val id: String, val userId: String, val name: String, val iconUrl: String, val audioUrl: String,
                             val imgUrl: String, val title: String, val content: String,
                             var likeNum: Int, val isLike: Int) : Serializable
