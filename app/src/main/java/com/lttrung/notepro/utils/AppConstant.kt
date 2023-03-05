package com.lttrung.notepro.utils

class AppConstant {
    companion object {
        const val NOTE = "com.lttrung.notepro.NOTE"
        const val USER_DATABASE_VERSION = 1
        const val USER_DATABASE_NAME = "com.lttrung.notepro.USER_DATABASE"

        const val DEFAULT_PREFERENCES_NAME = "com.lttrung.notepro.DEFAULT_PREFERENCES_NAME"
        const val REFRESH_TOKEN = "com.lttrung.notepro.REFRESH_TOKEN"
        const val ACCESS_TOKEN = "com.lttrung.notepro.ACCESS_TOKEN"

        const val CREATE_NOTE_REQUEST = 1
        const val EDIT_NOTE_REQUEST = 2
        const val SHOW_NOTE_DETAIL_REQUEST = 3

        const val CAMERA_REQUEST = 100
        const val READ_EXTERNAL_STORAGE_REQUEST = 101
        const val PICK_IMAGES_REQUEST = 102

        const val RC_SIGN_IN = 9001

        const val PAGE_LIMIT = 10
    }
}