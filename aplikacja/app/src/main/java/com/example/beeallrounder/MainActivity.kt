package com.example.beeallrounder

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

//    private fun changeLanguage(language : String) {
//        val locale = Locale(language);
//        Locale.setDefault(locale)
//        val configuration = Configuration();
//        configuration.locale = locale;
//        applicationContext.resources.updateConfiguration(configuration,applicationContext.resources.displayMetrics )
//        // here language is changed successfully
//
//        // update views (android handles it by itself, it's here just for prezentation
//    }
}