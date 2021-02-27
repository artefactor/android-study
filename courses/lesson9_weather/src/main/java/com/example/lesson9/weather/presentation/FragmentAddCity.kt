package com.example.lesson9.weather.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.lesson9.weather.LOG_TAG
import com.example.lesson9.weather.R
import com.example.lesson9.weather.databinding.FragmentAddCityBinding
import com.google.android.material.snackbar.Snackbar

class FragmentAddCity(private val viewModel: CitiesViewModel) : DialogFragment(R.layout.fragment_add_city) {
    private lateinit var binding: FragmentAddCityBinding
    private lateinit var fragmentManager: WeatherFragmentManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "AddCity#onViewCreated")
        fragmentManager = requireActivity() as WeatherFragmentManager
        binding = FragmentAddCityBinding.bind(view)
                .apply {
                    buttonCancel.setOnClickListener { dismiss() }
                    buttonOk.setOnClickListener {
                        val cityName = editTextCityName.text.toString()
                        if (cityName.isNotEmpty()) {
                            //loadData(cityName)
                            viewModel.addCity(cityName, "by", "23", "23")
                            dismiss()
                        } else {
                            Snackbar.make(it, "Write city name!", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
    }

    private fun logData(data: List<CityItem>?) {
        Log.i("usecase", data.toString())
    }


    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showError(errorMessage: String) {
        Log.e(LOG_TAG, errorMessage)
        fragmentManager.showError(errorMessage)
    }
}