package com.example.lesson9.weather.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lesson9.weather.LOG_TAG
import com.example.lesson9.weather.R
import com.example.lesson9.weather.databinding.FragmentCitiesListBinding

const val PROP_CITY_KEY = "city"

class FragmentCities : Fragment(R.layout.fragment_cities_list) {
    // TODO Denis, snackbar - один на фрагмент или на активити? Мне показалось, что удобнее на активити
    //    private var snackbar: Snackbar? = null
    private lateinit var binding: FragmentCitiesListBinding

    private val viewModelFactory: ViewModelProvider.Factory = WeatherViewModelFactory()
    private lateinit var viewModel: CitiesViewModel
    private lateinit var fragmentManager: WeatherFragmentManager

    private val cityListAdapter by lazy {
        CityListItemsAdapter { itemId -> fragmentManager.storeCityId(itemId) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "FragmentCities#onViewCreated")

        fragmentManager = requireActivity() as WeatherFragmentManager

        binding = FragmentCitiesListBinding.bind(view)
                .also {
                    it.recyclerViewCities.apply {
                        adapter = cityListAdapter
                        layoutManager = LinearLayoutManager(context)
                    }

                    it.buttonAdd.setOnClickListener {
                        val dialog = FragmentAddCity(viewModel)
                        dialog.show(requireActivity().supportFragmentManager, "addCityDialog")
                        //  viewModel.addCity("Minsk", "by","23", "23")
                    }

                    it.buttonBack.setOnClickListener {
                        // go to weather data and show weather
                        fragmentManager.showWeatherFragment()
                    }
                }
        viewModel = ViewModelProvider(this, viewModelFactory).get(CitiesViewModel::class.java)
        viewModel.init(requireActivity().applicationContext)
        with(viewModel) {
            citiesListLiveData.observe(viewLifecycleOwner, Observer { data -> showCitiesList(data) })
            errorLiveData.observe(viewLifecycleOwner, Observer { err -> showError(err) })
            fetchCities()
        }
    }

    private fun showCitiesList(data: List<CityItem>) {
        Log.i(LOG_TAG, "Model:$data")
        hideError()
        cityListAdapter.items = data
        cityListAdapter.selectedId = fragmentManager.getStoredCityId()
    }

    fun showError(errorMessage: String) {
        Log.e(LOG_TAG, errorMessage)
        fragmentManager.showError(errorMessage)
    }

    fun hideError() {
        fragmentManager.hideError()
    }

}