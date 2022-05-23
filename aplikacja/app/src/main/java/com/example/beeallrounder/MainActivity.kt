package com.example.beeallrounder

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import java.util.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp() : Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        return navController.navigateUp() || super.onSupportNavigateUp()
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