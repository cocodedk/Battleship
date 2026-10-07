package com.cocode.battleship.presentation.about

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AboutLinksTest {
    private val id = "com.cocode.battleship"

    @Test
    fun `update opens the F-Droid page when the app is live there`() {
        assertEquals(
            "https://f-droid.org/packages/com.cocode.battleship/",
            aboutUrl(AboutLink.Update, id, liveOnFdroid = true)
        )
    }

    @Test
    fun `update opens the latest GitHub release when the app is not on F-Droid`() {
        assertEquals(
            "https://github.com/cocodedk/Battleship/releases/latest",
            aboutUrl(AboutLink.Update, id, liveOnFdroid = false)
        )
    }

    @Test
    fun `update defaults to the F-Droid page, since the app is live there`() {
        assertEquals("https://f-droid.org/packages/com.cocode.battleship/", aboutUrl(AboutLink.Update, id))
    }

    @Test
    fun `privacy link points at the privacy page of the site`() {
        assertEquals("https://battleship.cocode.dk/privacy/", aboutUrl(AboutLink.Privacy, id))
    }

    @Test
    fun `privacy link is absent when the app has no policy`() {
        assertNull(aboutUrl(AboutLink.Privacy, id, privacyUrl = null))
    }

    @Test
    fun `website source and issues links`() {
        assertEquals("https://battleship.cocode.dk", aboutUrl(AboutLink.Website, id))
        assertEquals("https://github.com/cocodedk/Battleship", aboutUrl(AboutLink.Source, id))
        assertEquals("https://github.com/cocodedk/Battleship/issues", aboutUrl(AboutLink.Issues, id))
    }
}
