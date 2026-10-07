package com.cocode.battleship.presentation.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cocode.battleship.R
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavySurface

/** Opens the About page's links in the browser and works out the version from the installed package. */
@Composable
fun AboutRoute(onBack: () -> Unit) {
    val context = LocalContext.current
    val language = LocalConfiguration.current.locales[0].language
    val versionName = remember { appVersionName(context) }
    val privacyAvailable = aboutUrl(AboutLink.Privacy, context.packageName, language) != null
    AboutScreen(
        versionName = versionName,
        privacyAvailable = privacyAvailable,
        openLink = { link ->
            val url = aboutUrl(link, context.packageName, language)
            url != null && openUrl(context, url)
        },
        onBack = onBack,
    )
}

/**
 * The About page, sections in the order of the cocode-apps standard: name and version, what the
 * app does, privacy, links, credits and licenses, made by Cocode. [openLink] returns false when no
 * app can open the link, and the page then says so right under the button that was tapped.
 */
@Composable
fun AboutScreen(
    versionName: String,
    privacyAvailable: Boolean,
    openLink: (AboutLink) -> Boolean,
    onBack: () -> Unit,
) {
    var failedLink by rememberSaveable { mutableStateOf<AboutLink?>(null) }
    val open = { link: AboutLink -> failedLink = if (openLink(link)) null else link }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepNavy, NavySurface, DeepNavy)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AboutSection(R.string.app_name) {
                Body(stringResource(R.string.about_version, versionName))
                LinkButton(R.string.about_check_updates, AboutLink.Update, failedLink, open)
                Hint(stringResource(R.string.about_check_updates_hint))
            }
            AboutSection(R.string.about_what_title) {
                Body(stringResource(R.string.about_what))
            }
            AboutSection(R.string.about_privacy_title) {
                Body(stringResource(R.string.about_privacy_no_data))
                Body(stringResource(R.string.about_privacy_no_internet))
                Body(stringResource(R.string.about_privacy_on_device))
                if (privacyAvailable) {
                    LinkButton(R.string.about_privacy_link, AboutLink.Privacy, failedLink, open)
                }
            }
            AboutSection(R.string.about_links_title) {
                LinkButton(R.string.about_website, AboutLink.Website, failedLink, open)
                LinkButton(R.string.about_source, AboutLink.Source, failedLink, open)
                LinkButton(R.string.about_report, AboutLink.Issues, failedLink, open)
                Hint(stringResource(R.string.about_report_hint))
            }
            AboutSection(R.string.about_credits) {
                Body(stringResource(R.string.about_credits_license))
            }
            AboutSection(R.string.about_made_by) {
                Body(stringResource(R.string.about_made_by_body))
            }
            // Support slot: reserved for the Support phase of the cocode-apps standard. Nothing is
            // shown until then.
            AboutButton(R.string.about_back, onBack)
        }
    }
}
