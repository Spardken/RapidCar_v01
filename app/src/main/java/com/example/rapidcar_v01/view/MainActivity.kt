package com.example.rapidcar_v01.view

import HomeFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.utils.RetrofitInstance
import com.example.rapidcar_v01.view.fragments.DashboardFragment
//import com.example.rapidcar_v01.view.fragments.HomeFragment
import com.example.rapidcar_v01.view.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var navagation: BottomNavigationView

    private val mOnNavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.homeFragment -> {
                supportFragmentManager.commit {
                    replace<HomeFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.dashboardFragment -> {
                supportFragmentManager.commit {
                    replace<DashboardFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.profileFragment -> {
                supportFragmentManager.commit {
                    replace<ProfileFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa RetrofitInstance
        RetrofitInstance.initialize(this)

        // Ahora puedes usar RetrofitInstance.api
        val api = RetrofitInstance.api

        //Busca tu BottomNavigationView por su ID
        navagation = findViewById(R.id.navMenu)

        //Configura el listener para la navegación
        navagation.setOnNavigationItemSelectedListener(mOnNavMenu)

        //Reemplaza el fragmento por el HomeFragment al iniciar
        supportFragmentManager.commit {
            replace<HomeFragment>(R.id.frameContainer)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }
    }
}
