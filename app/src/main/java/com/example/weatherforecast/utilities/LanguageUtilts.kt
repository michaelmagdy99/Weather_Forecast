package com.example.weatherforecast.utilities
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale

object LanguageUtils {

    fun setAppLayoutDirections(locale: String, context: Context) {
        val configuration: Configuration = context.resources.configuration
        configuration.setLayoutDirection(Locale(locale))
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    fun setAppLocale(localeCode: String, context: Context) {
        val resources = context.resources
        val dm = resources.displayMetrics
        val config: Configuration = resources.configuration
        config.setLocale(Locale(localeCode))
        context.resources.updateConfiguration(config, dm)
    }

    fun changeLang(context: Context, lang_code: String): ContextWrapper {
        var myContext = context
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
            } else {
                config.locale = locale
            }
            myContext = context.createConfigurationContext(config)
        }
        return ContextWrapper(myContext)
    }

}