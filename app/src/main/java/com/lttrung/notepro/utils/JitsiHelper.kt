package com.lttrung.notepro.utils

import android.os.Bundle
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo

object JitsiHelper {
    private const val URL = "https://meet.jit.si"
    private const val APP_IDENTIFIER = "com.lttrung.notepro.call"
    fun createOptions(roomId: String, currentUser: CurrentUser, audioOnly: Boolean = false): JitsiMeetConferenceOptions {
        // Set default JitsiMeetConferenceOptions
        val defaultOptions = JitsiMeetConferenceOptions.Builder().setRoom(null)
            .setAudioMuted(false)
            .setAudioOnly(audioOnly)
            .setFeatureFlag("meeting-password.enabled", true)
            .setFeatureFlag("chat.enabled", false)
            .setFeatureFlag("invite.enabled", false)
            .setFeatureFlag("add-people.enabled", false)
            .setFeatureFlag("raise-hand.enabled", false)
            // This line auto join to the meeting
            .setFeatureFlag("prejoinpage.enabled", false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        // Launch a Jitsi Meet activity
        return JitsiMeetConferenceOptions.Builder().setRoom("$URL/$APP_IDENTIFIER.$roomId")
            .setUserInfo(JitsiMeetUserInfo(Bundle().apply {
                putString("displayName", currentUser.fullName)
                putString("email", currentUser.email)
            })).build()
    }
}