package com.lttrung.notepro.utils

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.lttrung.notepro.model.Image
import com.lttrung.notepro.model.Paging
import io.reactivex.rxjava3.core.Single

class GalleryUtils {
    companion object {
        fun findImages(context: Context, page: Int, limit: Int): Single<Paging<Image>> {
            val imageList: ArrayList<Image> = ArrayList()
            val columns = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
            )
            val sort = MediaStore.Images.ImageColumns.DATE_TAKEN
            val imageCursor = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                    null, "$sort DESC LIMIT $limit OFFSET ${page * limit}"
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
                        Image(
                            cs.getInt(idColumnIndex).toString(),
                            cs.getString(nameColumnIndex),
                            cs.getString(dataColumnIndex)
                        )
                    )
                }
            }
            val hasNextPage = (nextPageCursor?.count ?: 0) > 0
            imageCursor?.close()
            nextPageCursor?.close()
            return Single.just(Paging(hasPreviousPage = false, hasNextPage = hasNextPage, imageList))
        }
    }
}