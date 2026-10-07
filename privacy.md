# Privacy Policy — Battleship

**App:** Battleship (`com.cocode.battleship`)
**Developer:** CoCode.dk — Babak Bandpey
**Last updated:** 7 October 2026

> The canonical, always-current version of this policy is published at
> **https://battleship.cocode.dk/privacy/**

**Battleship does not collect, transmit, or share any personal data.**
It is a single-player naval combat game: you play against a computer opponent that runs on your device, and the game works offline.

## Your game data

The game keeps a record of your play: career statistics (games played, victories, win streaks, best score),
the highest rank you have reached, and the medals and badges you have earned. The app stores this record privately
on your device (Android `SharedPreferences`, in private mode) and does not transmit it. We never receive it.
It is removed from your phone if you delete the app or clear its data. Android may copy it through your configured backup
or device-transfer service; see "Device backup" below.

## No internet, no tracking

- The app requests **no internet permission**, so the app itself cannot connect to the internet. When you tap a link, it hands the address to your browser (see "External links" below).
- We use **no analytics, no crash reporting, and no advertising**.
- The app includes **no analytics or advertising SDKs** (software kits from other companies) and uses **no cookies and no advertising identifiers**. The libraries it uses come from Google (AndroidX) and JetBrains (Kotlin).
- The app asks Android for one permission, **VIBRATE**. It is a normal, install-time permission that shows no prompt and
  gives the app no access to any of your data. The app uses it only for short vibrations during play, for example when a
  super weapon fires. Android's AndroidX library also adds a permission that exists only inside this app
  (`com.cocode.battleship.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`). It keeps other apps from sending messages to the
  app's internal receivers, and it gives the app no access to your data.
- The app requests **no access to your location, contacts, camera, microphone, or storage**.

## Device backup

If you have enabled Android Auto Backup or Google account backup on your device, the operating system
may include this app's local data in your own personal Google backup, and Android may copy it to a new phone when you
transfer your data. You and Google control this. We have no access to it. A backup copy stays in your Google backup until
you remove it there. See [Google's Privacy Policy](https://policies.google.com/privacy) for details.

## External links

Some buttons open a web page in your browser. The main menu has two: **[ cocode.dk ]** (the developer's website,
[cocode.dk](https://cocode.dk)) and **[ OPEN DOWNLOAD PAGE ↗ ]** (the app's
[releases page on GitHub](https://github.com/cocodedk/Battleship/releases/latest)). The About screen (the **ABOUT**
button in the main menu) has five: **See the latest version** (the app's page on F-Droid), **Read the privacy policy**,
**Open the website**, **See the source code on GitHub** and **Report a problem on GitHub**.

The app opens a page only when you tap one of these buttons. It hands the address to your browser and adds no information
about you to it. The app itself makes no connection. Your browser then connects to that site, which can see your visit as
any website can, for example your IP address. Each site is governed by its own privacy policy. If your phone has no
browser, the app shows a message and nothing opens.

## Children

The app does not knowingly collect data from anyone, including children.

## Changes

If this policy changes, the updated version will be posted here and on the website with a new
"last updated" date.

## Contact

Questions about this policy can be sent to **bb@cocode.dk** (CoCode.dk, developer: Babak Bandpey).
