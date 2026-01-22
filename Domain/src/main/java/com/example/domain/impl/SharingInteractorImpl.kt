package com.example.domain.impl

import com.example.domain.api.SharingInteractor
import com.example.domain.api.SharingRepository

class SharingInteractorImpl(
    private val sharingRepository: SharingRepository
): SharingInteractor {

    override fun shareApp() {
        sharingRepository.shareApp()
    }

    override fun openTerms() {
        sharingRepository.openTerms()
    }

    override fun openSupport() {
        sharingRepository.openSupport()
    }

}