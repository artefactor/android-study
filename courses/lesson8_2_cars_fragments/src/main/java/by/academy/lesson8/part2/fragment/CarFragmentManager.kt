package by.academy.lesson8.part2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

interface CarFragmentManager {
    fun openFragment(fragment1: Fragment, fragment2Class: Class<out Fragment>, bundle: Bundle)
    fun openFragment(fragment1: Fragment, fragment2: Fragment)
    fun closeFragment(fragment2: Fragment)
    fun simpleBack()
    fun onBackPressed()
}
