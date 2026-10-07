package com.cocode.battleship.presentation.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

enum class AboutLink { Update, Website, Privacy, Source, Issues }

/** True while the app is listed on F-Droid (apps.yml in cocode-apps says `fdroid: live`). */
const val LIVE_ON_FDROID = true

private const val WEBSITE_URL = "https://battleship.cocode.dk"
private const val PRIVACY_URL = "https://battleship.cocode.dk/privacy/"
private const val REPO_URL = "https://github.com/cocodedk/Battleship"

/**
 * Languages the site has both a home page and a privacy page for, at `<site>/<code>/` and
 * `<site>/<code>/privacy/`. Persian has a home page but no privacy page of its own (its policy is a
 * section of the English one), so Persian stays on the English pages.
 */
private val SITE_LANGUAGES = setOf("da")

private fun localizedWebsite(language: String) =
    if (language in SITE_LANGUAGES) "$WEBSITE_URL/$language/" else WEBSITE_URL

private fun localizedPrivacy(language: String) =
    if (language in SITE_LANGUAGES) "$WEBSITE_URL/$language/privacy/" else PRIVACY_URL

/**
 * Where each About link goes. The website and privacy links follow [language] (a code such as "da"
 * from the app's current locale) and open the English pages when the site has none in that language.
 * "Check for updates" opens the F-Droid page once the app is live there, the GitHub release until
 * then; the app never asks the network whether a newer version exists. [privacyUrl] is null when the
 * app has no published policy, and the About page then leaves the link out.
 */
fun aboutUrl(
    link: AboutLink,
    applicationId: String,
    language: String,
    liveOnFdroid: Boolean = LIVE_ON_FDROID,
    privacyUrl: String? = localizedPrivacy(language),
): String? = when (link) {
    AboutLink.Update ->
        if (liveOnFdroid) "https://f-droid.org/packages/$applicationId/" else "$REPO_URL/releases/latest"
    AboutLink.Website -> localizedWebsite(language)
    AboutLink.Privacy -> privacyUrl
    AboutLink.Source -> REPO_URL
    AboutLink.Issues -> "$REPO_URL/issues"
}

/** Opens [url] in whatever app handles it; false when none does. */
internal fun openUrl(context: Context, url: String): Boolean = try {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    true
} catch (_: ActivityNotFoundException) {
    false
}

@Suppress("DEPRECATION")
internal fun appVersionName(context: Context): String =
    context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
