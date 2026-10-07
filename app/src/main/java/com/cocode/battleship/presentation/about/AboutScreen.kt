package com.cocode.battleship.presentation.about

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.presentation.SYM_SECTION
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextPrimary
import com.cocode.battleship.ui.theme.TextSecondary

/** Opens the About page's links in the browser and works out the version from the installed package. */
@Composable
fun AboutRoute(onBack: () -> Unit) {
    val context = LocalContext.current
    val versionName = remember { appVersionName(context) }
    val privacyAvailable = aboutUrl(AboutLink.Privacy, context.packageName) != null
    AboutScreen(
        versionName = versionName,
        privacyAvailable = privacyAvailable,
        openLink = { link ->
            val url = aboutUrl(link, context.packageName)
            url != null && openUrl(context, url)
        },
        onBack = onBack,
    )
}

/**
 * The About page, sections in the order of the cocode-apps standard: name and version, what the
 * app does, privacy, links, credits and licenses, made by Cocode. [openLink] returns false when no
 * app can open the link, and the page then says so under the links.
 */
@Composable
fun AboutScreen(
    versionName: String,
    privacyAvailable: Boolean,
    openLink: (AboutLink) -> Boolean,
    onBack: () -> Unit,
) {
    var noBrowser by rememberSaveable { mutableStateOf(false) }
    val open = { link: AboutLink -> noBrowser = !openLink(link) }

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
                AboutButton(R.string.about_check_updates) { open(AboutLink.Update) }
                Hint(stringResource(R.string.about_check_updates_hint))
            }
            AboutSection(R.string.about_what_title) {
                Body(stringResource(R.string.about_what))
            }
            AboutSection(R.string.about_privacy_title) {
                Body(stringResource(R.string.about_privacy_no_data))
                Body(stringResource(R.string.about_privacy_no_internet))
                Body(stringResource(R.string.about_privacy_on_device))
                if (privacyAvailable) AboutButton(R.string.about_privacy_link) { open(AboutLink.Privacy) }
            }
            AboutSection(R.string.about_links_title) {
                AboutButton(R.string.about_website) { open(AboutLink.Website) }
                AboutButton(R.string.about_source) { open(AboutLink.Source) }
                AboutButton(R.string.about_report) { open(AboutLink.Issues) }
                Hint(stringResource(R.string.about_report_hint))
                if (noBrowser) Body(stringResource(R.string.about_no_browser))
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

@Composable
private fun AboutSection(@StringRes title: Int, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyCard, RoundedCornerShape(4.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // The diamond is decoration; TalkBack should read the title alone, as a heading.
            Text(
                text = SYM_SECTION,
                style = MaterialTheme.typography.titleSmall,
                color = SonarCyan,
                modifier = Modifier.clearAndSetSemantics {},
            )
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleSmall,
                color = SonarCyan,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.semantics { heading() },
            )
        }
        content()
    }
}

@Composable
private fun Body(text: String) = Text(
    text = text,
    style = MaterialTheme.typography.bodyMedium,
    color = TextPrimary,
)

@Composable
private fun Hint(text: String) = Text(
    text = text,
    style = MaterialTheme.typography.bodySmall,
    color = TextSecondary,
)

@Composable
private fun AboutButton(@StringRes label: Int, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan),
    ) {
        Text(
            text = stringResource(label),
            letterSpacing = 1.sp,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
