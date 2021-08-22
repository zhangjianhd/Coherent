package com.zhangjian.coherent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.zhangjian.coherent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataBinding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        dataBinding.btnRealValue.setOnClickListener {
            startActivity(Intent(this, RealValueActivity::class.java))
        }

        dataBinding.btnSimulate.setOnClickListener {
            startActivity(Intent(this, SimulateProgressActivity::class.java))
        }
    }
}