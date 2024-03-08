package com.example.weatherforecast.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale

object
LanguageUtilts {
    fun get_En_To_Ar_Numbers(number: String): String {
        val arabicNumber = mutableListOf<String>()
        for (element in number) {
            when (element) {
                '1' -> arabicNumber.add("١")
                '2' -> arabicNumber.add("٢")
                '3' -> arabicNumber.add("٣")
                '4' -> arabicNumber.add("٤")
                '5' -> arabicNumber.add("٥")
                '6' -> arabicNumber.add("٦")
                '7' -> arabicNumber.add("٧")
                '8' -> arabicNumber.add("٨")
                '9' -> arabicNumber.add("٩")
                '0' ->arabicNumber.add("٠")
                else -> arabicNumber.add("")
            }
        }
        return arabicNumber.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")
            .replace(" ", "")
    }


    fun setAppLayoutDirections(locale:String,context: Context){
        val configuration: Configuration = context.resources.configuration
        configuration.setLayoutDirection(Locale(locale))
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun setAppLocale(localeCode: String, context: Context) {
        val resources = context.resources
        val dm = resources.displayMetrics
        val config: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            config.setLocale(Locale(localeCode))
        }
        else
        {
            config.locale = Locale(localeCode)
        }
        resources.updateConfiguration(config, dm)
    }
    @SuppressLint("ObsoleteSdkInt")
    fun changeLang(context: Context, lang_code: String): ContextWrapper {
        var myContext=context
        val sysLocale: Locale
        val rs: Resources = context.resources
        val config: Configuration = rs.configuration
        sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales.get(0)
        } else {
            config.locale
        }
        if (lang_code != "" && !sysLocale.language.equals(lang_code)) {
            val locale = Locale(lang_code)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            { config.setLocale(locale) }
            else { config.locale = locale }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                myContext = context.createConfigurationContext(config)
            } else {
                myContext.resources.updateConfiguration(config, context.resources.displayMetrics)
            }
        }
        return ContextWrapper(myContext)
    }
}