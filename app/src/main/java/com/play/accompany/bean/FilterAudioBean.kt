package com.play.accompany.bean

import java.io.Serializable

data class FilterAudioBean(val name: String, val path: String, val sizeLong: Long, val sizeString: String,
                           var audioDuration: Int, var audioDurationString: String, var selected: Boolean = false):Serializable




