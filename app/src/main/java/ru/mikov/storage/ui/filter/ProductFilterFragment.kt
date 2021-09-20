package ru.mikov.storage.ui.filter

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.mikov.storage.R

class ProductFilterFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_product_filter, rootKey)
    }
}