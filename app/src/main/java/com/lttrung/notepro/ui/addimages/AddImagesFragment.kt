package com.lttrung.notepro.ui.addimages

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lttrung.notepro.databinding.FragmentAddImagesBinding
import com.lttrung.notepro.ui.base.activities.AddImagesActivity
import com.lttrung.notepro.utils.AppConstant.Companion.CAMERA_REQUEST
import com.lttrung.notepro.utils.AppConstant.Companion.READ_EXTERNAL_STORAGE_REQUEST
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddImagesFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "com.lttrung.notepro.AddImagesFragment"
    }
    private val binding by lazy {
        FragmentAddImagesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initListeners()
        return binding.root
    }

    private fun initListeners() {
        binding.btnTakePhoto.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST
                )
            } else {
                val parentActivity = (requireActivity() as AddImagesActivity)
                parentActivity.openCamera()
            }
        }
        binding.btnOpenGallery.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST
                )
            } else {
                val parentActivity = (requireActivity() as AddImagesActivity)
                parentActivity.openGallery()
            }
        }
    }
}