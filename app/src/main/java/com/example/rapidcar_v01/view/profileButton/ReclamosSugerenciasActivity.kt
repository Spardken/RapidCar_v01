package com.example.rapidcar_v01.view.profileButton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.databinding.ActivityReclamosSugerenciasBinding

class ReclamosSugerenciasActivity : AppCompatActivity() {

    lateinit var binding: ActivityReclamosSugerenciasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReclamosSugerenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }


}

