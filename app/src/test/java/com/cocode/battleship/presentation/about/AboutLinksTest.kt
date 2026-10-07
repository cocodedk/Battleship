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
            aboutUrl(AboutLink.Update, id, "en", liveOnFdroid = true)
        )
    }

    @Test
    fun `update opens the latest GitHub release when the app is not on F-Droid`() {
        assertEquals(
            "https://github.com/cocodedk/Battleship/releases/latest",
            aboutUrl(AboutLink.Update, id, "en", liveOnFdroid = false)
        )
    }

    @Test
    fun `update defaults to the F-Droid page, since the app is live there`() {
        assertEquals("https://f-droid.org/packages/com.cocode.battleship/", aboutUrl(AboutLink.Update, id, "en"))
    }

    @Test
    fun `privacy opens the English policy in English`() {
        assertEquals("https://battleship.cocode.dk/privacy/", aboutUrl(AboutLink.Privacy, id, "en"))
    }

    @Test
    fun `privacy opens the Danish policy in Danish`() {
        assertEquals("https://battleship.cocode.dk/da/privacy/", aboutUrl(AboutLink.Privacy, id, "da"))
    }

    @Test
    fun `privacy falls back to the English policy in a language the site lacks`() {
        assertEquals("https://battleship.cocode.dk/privacy/", aboutUrl(AboutLink.Privacy, id, "de"))
    }

    @Test
    fun `privacy stays English in Persian, which has no privacy page of its own`() {
        assertEquals("https://battleship.cocode.dk/privacy/", aboutUrl(AboutLink.Privacy, id, "fa"))
    }

    @Test
    fun `privacy link is absent when the app has no policy`() {
        assertNull(aboutUrl(AboutLink.Privacy, id, "da", privacyUrl = null))
    }

    @Test
    fun `website opens the English site in English`() {
        assertEquals("https://battleship.cocode.dk", aboutUrl(AboutLink.Website, id, "en"))
    }

    @Test
    fun `website opens the Danish site in Danish`() {
        assertEquals("https://battleship.cocode.dk/da/", aboutUrl(AboutLink.Website, id, "da"))
    }

    @Test
    fun `website falls back to the English site in a language the site lacks`() {
        assertEquals("https://battleship.cocode.dk", aboutUrl(AboutLink.Website, id, "de"))
        assertEquals("https://battleship.cocode.dk", aboutUrl(AboutLink.Website, id, "fa"))
    }

    @Test
    fun `source and issues links are the same in every language`() {
        for (language in listOf("en", "da", "de")) {
            assertEquals("https://github.com/cocodedk/Battleship", aboutUrl(AboutLink.Source, id, language))
            assertEquals("https://github.com/cocodedk/Battleship/issues", aboutUrl(AboutLink.Issues, id, language))
        }
    }
}
