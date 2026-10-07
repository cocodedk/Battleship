package com.cocode.battleship.presentation.about

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.presentation.SYM_SECTION
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextPrimary
import com.cocode.battleship.ui.theme.TextSecondary

/** One card of the About page. [title] is a TalkBack heading. */
@Composable
internal fun AboutSection(@StringRes title: Int, content: @Composable ColumnScope.() -> Unit) {
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
internal fun Body(text: String) = Text(
    text = text,
    style = MaterialTheme.typography.bodyMedium,
    color = TextPrimary,
)

@Composable
internal fun Hint(text: String) = Text(
    text = text,
    style = MaterialTheme.typography.bodySmall,
    color = TextSecondary,
)

/** A button that opens [link]; if no app could open it, the note appears right under the button. */
@Composable
internal fun LinkButton(
    @StringRes label: Int,
    link: AboutLink,
    failed: AboutLink?,
    open: (AboutLink) -> Unit,
) {
    AboutButton(label) { open(link) }
    if (failed == link) {
        // A polite live region makes TalkBack read the note when it appears.
        Text(
            text = stringResource(R.string.about_no_browser),
            style = MaterialTheme.typography.bodySmall,
            color = AmberWarning,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

@Composable
internal fun AboutButton(@StringRes label: Int, onClick: () -> Unit) {
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
