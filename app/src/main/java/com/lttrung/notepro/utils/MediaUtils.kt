package com.lttrung.notepro.utils

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.lttrung.notepro.domain.data.locals.models.MediaSelectionLocalsModel
import com.lttrung.notepro.domain.data.networks.models.Paging

object MediaUtils {
    fun findImages(
        context: Context, page: Int, limit: Int
    ): Paging<MediaSelectionLocalsModel> {
        val imageList: ArrayList<MediaSelectionLocalsModel> = ArrayList()
        val columns = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID
        )
        val sort = MediaStore.Images.ImageColumns.DATE_TAKEN
        val imageCursor = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                "$sort DESC LIMIT $limit OFFSET ${page * limit}"
            )
        } else {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, page * limit)
                }, null
            )
        }
        val nextPageCursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, (page + 1) * limit)
                }, null
            )
        } else {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, "$sort DESC LIMIT $limit OFFSET ${(page + 1) * limit}"
            )
        }
        imageCursor?.let { cs ->
            for (i in 0 until cs.count) {
                cs.moveToPosition(i)
                val idColumnIndex = cs.getColumnIndex(MediaStore.Images.Media._ID)
                val nameColumnIndex = cs.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                val dataColumnIndex =
                    cs.getColumnIndex(MediaStore.Images.Media.DATA)
                imageList.add(
                    MediaSelectionLocalsModel(
                        cs.getInt(idColumnIndex).toString(),
                        cs.getString(nameColumnIndex),
                        cs.getString(dataColumnIndex),
                        System.currentTimeMillis(),
                        "",
                        AppConstant.MESSAGE_CONTENT_TYPE_IMAGE,
                        false
                    )
                )
            }
        }
        val hasNextPage = (nextPageCursor?.count ?: 0) > 0
        imageCursor?.close()
        nextPageCursor?.close()
        return Paging(hasPreviousPage = false, hasNextPage = hasNextPage, imageList)
    }

    fun findVideos(
        context: Context, page: Int, limit: Int
    ): Paging<MediaSelectionLocalsModel> {
        val videoList: ArrayList<MediaSelectionLocalsModel> = ArrayList()
        val columns = arrayOf(
            MediaStore.Video.VideoColumns.DISPLAY_NAME,
            MediaStore.Video.VideoColumns.DATA,
            MediaStore.Video.VideoColumns._ID
        )
        val sort = MediaStore.Video.VideoColumns.DATE_TAKEN
        val videoCursor = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                "$sort DESC LIMIT $limit OFFSET ${page * limit}"
            )
        } else {
            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, page * limit)
                }, null
            )
        }
        val nextPageCursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, (page + 1) * limit)
                }, null
            )
        } else {
            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, "$sort DESC LIMIT $limit OFFSET ${(page + 1) * limit}"
            )
        }
        videoCursor?.let { cs ->
            for (i in 0 until cs.count) {
                cs.moveToPosition(i)
                val idColumnIndex = cs.getColumnIndex(MediaStore.Video.VideoColumns._ID)
                val nameColumnIndex = cs.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
                val dataColumnIndex =
                    cs.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
                videoList.add(
                    MediaSelectionLocalsModel(
                        cs.getInt(idColumnIndex).toString(),
                        cs.getString(nameColumnIndex),
                        cs.getString(dataColumnIndex),
                        System.currentTimeMillis(),
                        "",
                        AppConstant.MESSAGE_CONTENT_TYPE_VIDEO,
                        false
                    )
                )
            }
        }
        val hasNextPage = (nextPageCursor?.count ?: 0) > 0
        videoCursor?.close()
        nextPageCursor?.close()
        return Paging(hasPreviousPage = false, hasNextPage = hasNextPage, videoList)
    }
}