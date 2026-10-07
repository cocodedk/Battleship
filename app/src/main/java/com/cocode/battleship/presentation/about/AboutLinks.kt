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
 * Where each About link goes. "Check for updates" opens the F-Droid page once the app is live there,
 * the GitHub release until then; the app never asks the network whether a newer version exists.
 * [privacyUrl] is null when the app has no published policy, and the About page then leaves the
 * link out.
 */
fun aboutUrl(
    link: AboutLink,
    applicationId: String,
    liveOnFdroid: Boolean = LIVE_ON_FDROID,
    privacyUrl: String? = PRIVACY_URL,
): String? = when (link) {
    AboutLink.Update ->
        if (liveOnFdroid) "https://f-droid.org/packages/$applicationId/" else "$REPO_URL/releases/latest"
    AboutLink.Website -> WEBSITE_URL
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
