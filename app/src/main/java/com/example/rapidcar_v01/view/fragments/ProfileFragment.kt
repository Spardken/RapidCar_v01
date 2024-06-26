/*
package com.example.rapidcar_v01.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rapidcar_v01.databinding.FragmentProfileBinding
import com.example.rapidcar_v01.view.profileButton.LoginActivity
import com.example.rapidcar_v01.tokenmanager.SharedPreferencesManager
import android.content.SharedPreferences

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

        binding.btnEditarProfile.setOnClickListener{
            val intent = Intent(activity,UserPerfileFragment::class.java)
            startActivity(intent)
        }

    val sharedPreferencesManager = SharedPreferencesManager(requireContext())
    val sharedPreferences = sharedPreferencesManager.getSharedPreferences()

    // Actualizar la visibilidad de los TextViews basado en si el token es nulo o no
    fun updateTextViews() {
        if (sharedPreferencesManager.fetchAuthToken() == null) {
            binding.tvUsuarioActivo.visibility = View.INVISIBLE
            binding.tvUsuarioInactivo.visibility = View.VISIBLE
        } else {
            binding.tvUsuarioActivo.visibility = View.VISIBLE
            binding.tvUsuarioInactivo.visibility = View.INVISIBLE
        }
    }

    // Actualizar los TextViews inicialmente
    updateTextViews()

    // Crear un observador para las preferencias compartidas
    val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        updateTextViews()
    }

    // Registrar el observador
    sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

    // Configurar el OnClickListener para el botón de logout
    binding.btnLogout.setOnClickListener {
        sharedPreferencesManager.saveAuthToken(null)
    }

    binding.btnLogin.setOnClickListener {
        val LoginActivity = Intent(activity, LoginActivity::class.java)
        startActivity(LoginActivity)
    }
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

*/

package com.example.rapidcar_v01.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rapidcar_v01.databinding.FragmentProfileBinding
import com.example.rapidcar_v01.view.profileButton.LoginActivity
import com.example.rapidcar_v01.tokenmanager.SharedPreferencesManager
import android.content.SharedPreferences

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val sharedPreferences = sharedPreferencesManager.getSharedPreferences()


        binding.btnEditarProfile.setOnClickListener{
            val intent = Intent(activity,UserPerfileFragment::class.java)
            startActivity(intent)
        }
        // Actualizar la visibilidad de los TextViews basado en si el token es nulo o no
        fun updateTextViews() {
            _binding?.let {
                if (sharedPreferencesManager.fetchAuthToken() == null) {
                    it.tvUsuarioActivo.visibility = View.INVISIBLE
                    it.tvUsuarioInactivo.visibility = View.VISIBLE
                } else {
                    it.tvUsuarioActivo.visibility = View.VISIBLE
                    it.tvUsuarioInactivo.visibility = View.INVISIBLE
                }
            }
        }

        // Actualizar los TextViews inicialmente
        updateTextViews()

        // Crear un observador para las preferencias compartidas
        val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            updateTextViews()
        }

        // Registrar el observador
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        // Configurar el OnClickListener para el botón de logout
        binding.btnLogout.setOnClickListener {
            sharedPreferencesManager.saveAuthToken(null)
        }

        binding.btnLogin.setOnClickListener {
            val LoginActivity = Intent(activity, LoginActivity::class.java)
            startActivity(LoginActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
