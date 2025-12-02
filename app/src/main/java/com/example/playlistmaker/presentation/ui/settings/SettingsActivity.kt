package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.presentation.ui.settings.dark_theme.App
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val arrowBackIcon = findViewById<MaterialToolbar>(R.id.toolBar)
        val shareApp = findViewById<MaterialTextView>(R.id.shareApp)
        val supportMessage = findViewById<MaterialTextView>(R.id.supportMessage)
        val userAgreement = findViewById<MaterialTextView>(R.id.userAgreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.nightThemeSwitcher)
        val currentTheme = (applicationContext as App).getDarkTheme()
        themeSwitcher.isChecked = currentTheme

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

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}