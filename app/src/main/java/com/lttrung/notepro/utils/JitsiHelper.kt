package com.lttrung.notepro.utils

import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo

object JitsiHelper {
    private const val URL = "https://meet.jit.si"
    private const val APP_IDENTIFIER = "com.lttrung.notepro.call"
    fun createOptions(roomId: String): JitsiMeetConferenceOptions {
        // Set default JitsiMeetConferenceOptions
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        // Launch a Jitsi Meet activity
        return JitsiMeetConferenceOptions.Builder()
            .setRoom("$URL/$APP_IDENTIFIER.$roomId")
            .setAudioMuted(false)
            .setVideoMuted(true)
            .setFeatureFlag("meeting-password.enabled", true)
            .setFeatureFlag("chat.enabled", false)
            .setFeatureFlag("invite.enabled", false)
            .setUserInfo(JitsiMeetUserInfo())
            .build()
    }
}