package com.example.weather.Utility

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weather.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val fragment = HomeFragment()
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.add(R.id.fragmentContainerView, fragment)
//        fragmentTransaction.commit()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home_main)?.findNavController()
        navController?.let { setupWithNavController(bottomNavigationView, it) }
    }



    }

