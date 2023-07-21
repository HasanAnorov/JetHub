package com.hasan.jetfasthub.screens.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hasan.jetfasthub.databinding.ActivityMainBinding

class AppActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}