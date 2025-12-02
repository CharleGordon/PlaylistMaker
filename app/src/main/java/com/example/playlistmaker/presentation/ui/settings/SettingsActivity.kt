package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var arrowBackIcon: MaterialToolbar
    private lateinit var shareApp: MaterialTextView
    private lateinit var supportMessage: MaterialTextView
    private lateinit var userAgreement: MaterialTextView
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        applyWindowInsets()

        initsViews()
        setupVars()

        setCurrentTheme()

        setupListeners()
    }

    private fun initsViews() {
        arrowBackIcon = findViewById(R.id.toolBar)
        shareApp = findViewById(R.id.shareApp)
        supportMessage = findViewById(R.id.supportMessage)
        userAgreement = findViewById(R.id.userAgreement)
        themeSwitcher = findViewById(R.id.nightThemeSwitcher)
    }

    private fun setupListeners() {
        arrowBackIcon.setNavigationOnClickListener {
            finish()
        }

        shareApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            val shareText = getString(R.string.share_course_text)

            intent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(intent, getString(R.string.share_course_text)))
        }

        supportMessage.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)

            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_theme_text))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))

            startActivity(intent)
        }

        userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))

            startActivity(intent)
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            themeInteractor.switchTheme(isChecked)
        }
    }

    private fun setupVars() {
        themeInteractor = Creator.provideThemeInteractor(this)
    }

    private fun setCurrentTheme() {
        themeSwitcher.isChecked = themeInteractor.isDarkTheme()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}