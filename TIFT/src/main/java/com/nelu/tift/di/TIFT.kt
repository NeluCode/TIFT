package com.nelu.tift.di

import com.nelu.tift.data.repo.base.BaseTIFT
import com.nelu.tift.data.repo.base.BaseTiktok
import com.nelu.tift.data.repo.RepoTIFT
import com.nelu.tift.data.repo.RepoTiktok
import com.nelu.tift.di.Initializations.apiService

class TIFT {

    companion object {
        private lateinit var baseTIFT: BaseTIFT
    }

    private fun inject() {
        baseTIFT = RepoTIFT(
            RepoTiktok(apiService)
        )
    }

    fun getTIFT() = baseTIFT

    init { inject() }
}