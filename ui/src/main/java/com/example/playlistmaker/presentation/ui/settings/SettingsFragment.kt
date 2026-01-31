package com.example.playlistmaker.presentation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.presentation.viewmodel.settings.SettingsFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.themeSettings.observe(viewLifecycleOwner) { themeSettings ->
            binding.nightThemeSwitcher.isChecked = themeSettings.isDarkTheme
        }
    }

    private fun setupListeners() {
        binding.shareApp.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        binding.supportMessage.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.onTermsClicked()
        }

        binding.nightThemeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitch(isChecked)
        }
    }
}