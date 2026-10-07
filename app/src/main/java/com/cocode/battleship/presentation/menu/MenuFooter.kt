package com.cocode.battleship.presentation.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.presentation.about.openUrl
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextDim

private const val URL_WEBSITE = "https://cocode.dk"
private const val URL_APK = "https://github.com/cocodedk/Battleship/releases/latest"

@Composable
internal fun MenuFooter() {
    val context = LocalContext.current
    // No browser on the phone: say so here instead of crashing (as the About screen does).
    var linkFailed by remember { mutableStateOf(false) }

    Spacer(Modifier.height(12.dp))
    HorizontalDivider(
        color = SonarCyan.copy(alpha = 0.10f),
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.menu_developed_by),
        style = MaterialTheme.typography.labelSmall,
        color = TextDim,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(4.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.menu_website_link),
            style = MaterialTheme.typography.labelSmall,
            color = SonarCyan.copy(alpha = 0.55f),
            modifier = Modifier.clickable { linkFailed = !openUrl(context, URL_WEBSITE) }
        )
        Text(
            text = "·",
            style = MaterialTheme.typography.labelSmall,
            color = TextDim,
        )
        Text(
            text = stringResource(R.string.menu_apk_link),
            style = MaterialTheme.typography.labelSmall,
            color = SonarCyan.copy(alpha = 0.55f),
            modifier = Modifier.clickable { linkFailed = !openUrl(context, URL_APK) }
        )
    }
    if (linkFailed) {
        Text(
            text = stringResource(R.string.about_no_browser),
            style = MaterialTheme.typography.bodySmall,
            color = AmberWarning,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
    Spacer(Modifier.height(16.dp))
}
