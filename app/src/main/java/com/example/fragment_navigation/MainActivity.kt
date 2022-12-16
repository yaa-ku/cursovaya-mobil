package com.example.fragment_navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fragment_navigation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, HomeFragment())
        transaction.commit()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId){
                    R.id.home -> {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frame_layout, HomeFragment())
                        transaction.commit()
                    }
                    R.id.weather -> {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frame_layout, WeatherFragment())
                        transaction.commit()
                    }
                    R.id.charts -> {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frame_layout, ChartsFragment())
                        transaction.commit()
                    }
                    R.id.profile -> {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frame_layout, ProfileFragment())
                        transaction.commit()
                    }
             }
            true
        }
    }
}
