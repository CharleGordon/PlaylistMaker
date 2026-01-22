package com.example.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.R
import com.example.domain.api.SharingRepository

class ExternalNavigator(
    private val context: Context
): SharingRepository {

    override fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_course_text))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun openTerms() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.user_agreement_url))).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun openSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_theme_text))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_text))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}