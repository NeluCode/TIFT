package com.nelu.tift.data.repo

import com.nelu.tift.data.model.URLTypes
import com.nelu.tift.data.repo.base.BaseTIFT
import com.nelu.tift.data.repo.base.BaseTiktok
import com.nelu.tift.db.dao.DaoDownloads
import com.nelu.tift.di.Initializations
import com.nelu.tift.di.Initializations.apiService
import com.nelu.tift.di.Initializations.daoDownloads
import java.io.File
import java.util.regex.Pattern

class RepoTIFT : BaseTIFT {

    override val tiktok: BaseTiktok get() = RepoTiktok()

    override suspend fun identifyURL(url: String) = when {
        isTikTokUrl(url) -> URLTypes.TIKTOK
        isFacebookUrl(url) -> URLTypes.FACEBOOK
        isInstagramUrl(url) -> URLTypes.INSTAGRAM
        isTwitterUrl(url) -> URLTypes.TWITTER
        else -> URLTypes.UNKNOWN
    }

    override suspend fun getDownloads() = daoDownloads.getAllDownloads()

    private fun isTikTokUrl(url: String): Boolean {
        val tiktokPattern = Pattern.compile("^(https?://)?(www\\.)?(tiktok\\.com)")
        return tiktokPattern.matcher(url).find()
    }

    private fun isFacebookUrl(url: String): Boolean {
        val facebookPattern = Pattern.compile("^(https?://)?(www\\.)?(facebook\\.com)")
        return facebookPattern.matcher(url).find()
    }

    private fun isInstagramUrl(url: String): Boolean {
        val instagramPattern = Pattern.compile("^(https?://)?(www\\.)?(instagram\\.com)")
        return instagramPattern.matcher(url).find()
    }

    private fun isTwitterUrl(url: String): Boolean {
        val twitterPattern = Pattern.compile("^(https?://)?(www\\.)?(twitter\\.com)")
        return twitterPattern.matcher(url).find()
    }
}