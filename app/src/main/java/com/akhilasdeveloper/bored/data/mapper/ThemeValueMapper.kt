package com.akhilasdeveloper.bored.data.mapper

object ThemeValueMapper {

    const val SYSTEM_THEME = 4
    const val LIGHT_THEME = 8
    const val DARK_THEME = 3

    fun getSystemInDarkValueFromConst(source: Int): Boolean? = when(source){
        SYSTEM_THEME -> {null}
        LIGHT_THEME -> {false}
        DARK_THEME -> {true}
        else -> {null}
    }

    fun getSystemInDarkValueFromBoolean(source: Boolean?):Int = when(source){
        null -> {SYSTEM_THEME}
        true -> {DARK_THEME}
        false -> {LIGHT_THEME}
    }
}