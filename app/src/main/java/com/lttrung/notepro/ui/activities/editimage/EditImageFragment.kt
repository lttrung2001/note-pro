package com.lttrung.notepro.ui.activities.editimage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.StorageReference
import com.lttrung.notepro.databinding.FragmentEditImageBinding
import com.lttrung.notepro.ui.activities.chat.ChatViewModel
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.MediaType
import com.lttrung.notepro.utils.toByteArray
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditImageFragment(
    val bitmap: Bitmap
) : BottomSheetDialogFragment() {
    @Inject
    lateinit var storageRef: StorageReference

    private val binding by lazy {
        FragmentEditImageBinding.inflate(layoutInflater)
    }
    private val viewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews() {
        binding.ivEditingImage.setImageBitmap(bitmap)
        val value = getBitmapBrightness(bitmap) / 255
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.sbBrightnessBar.min = value
        }
        binding.sbBrightnessBar.progress = value
    }

    private fun initListeners() {
        binding.sbBrightnessBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                updateImageBrightness(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        binding.sbContrastBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val contrastValue: Float =
                    p1 / 100.0f // Chuyển đổi giá trị SeekBar từ [0, 200] thành [0, 2]

                val modifiedBitmap: Bitmap = applyContrast(bitmap, contrastValue)
                binding.ivEditingImage.setImageBitmap(modifiedBitmap)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        binding.btnSend.setOnClickListener {
            sendMediaViaCloudStorage(
                binding.ivEditingImage.drawable
                    .toBitmap()
                    .toByteArray()
            )
        }
    }

    private fun applyContrast(bitmap: Bitmap, contrastValue: Float): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(
            floatArrayOf(
                contrastValue, 0f, 0f, 0f, 0f,
                0f, contrastValue, 0f, 0f, 0f,
                0f, 0f, contrastValue, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )

        val filter = ColorMatrixColorFilter(colorMatrix)
        val drawable = binding.ivEditingImage.drawable as BitmapDrawable
        val modifiedBitmap = Bitmap.createBitmap(
            drawable.bitmap.width,
            drawable.bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(modifiedBitmap)
        val paint = Paint()
        paint.colorFilter = filter

        // Vẽ hình ảnh gốc lên canvas với hiệu ứng tương phản
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return modifiedBitmap
    }

    private fun updateImageBrightness(brightness: Int) {
        val brightnessFactor = brightness / 255.0F
        val colorMatrix = ColorMatrix()
        colorMatrix.set(
            floatArrayOf(
                brightnessFactor, 0f, 0f, 0f, 0f,
                0f, brightnessFactor, 0f, 0f, 0f,
                0f, 0f, brightnessFactor, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val filter = ColorMatrixColorFilter(colorMatrix)
        binding.ivEditingImage.colorFilter = filter
    }

    private fun getBitmapBrightness(bitmap: Bitmap): Int {
        val width = bitmap.width
        val height = bitmap.height
        val totalPixels = width * height
        val pixels = IntArray(totalPixels)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var totalRed: Long = 0
        var totalGreen: Long = 0
        var totalBlue: Long = 0
        for (pixel in pixels) {
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
            totalRed += red.toLong()
            totalGreen += green.toLong()
            totalBlue += blue.toLong()
        }
        val averageRed = (totalRed / totalPixels).toInt()
        val averageGreen = (totalGreen / totalPixels).toInt()
        val averageBlue = (totalBlue / totalPixels).toInt()

        // Tính toán giá trị độ sáng trung bình
        val brightness = 0.299f * averageRed + 0.587f * averageGreen + 0.114f * averageBlue / 255.0f

        // Chuyển đổi giá trị độ sáng từ [0, 1] sang [0, 255]
        return (brightness * 255).toInt()
    }

    private fun sendMediaViaCloudStorage(byteArray: ByteArray) {
        viewModel.isLoading.value = true
        val path = "images/messages/${System.currentTimeMillis()}.jpg"
        storageRef.child(path)
            .putBytes(byteArray)
            .addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    viewModel.saveUploadResult(
                        hashMapOf<String, String>().apply {
                            put("TYPE", AppConstant.MESSAGE_CONTENT_TYPE_IMAGE)
                            put("URL", uri.toString())
                        }
                    )
                    dismiss()
                    viewModel.isLoading.value = false
                }
            }
    }
}