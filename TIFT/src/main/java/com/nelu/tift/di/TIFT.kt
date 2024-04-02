package com.nelu.tift.di

import com.nelu.tift.data.repo.base.BaseTIFT
import com.nelu.tift.data.repo.RepoTIFT

class TIFT {

    companion object {
        private lateinit var baseTIFT: BaseTIFT
    }

    private fun inject() {
        baseTIFT = RepoTIFT()
    }

    fun getTIFT() = baseTIFT

    init { inject() }
}