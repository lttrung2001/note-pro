package com.lttrung.notepro.ui.addimages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lttrung.notepro.databinding.FragmentAddImagesBinding
import com.lttrung.notepro.ui.viewgallery.ViewGalleryActivity
import com.lttrung.notepro.utils.AppConstant.Companion.CAMERA_REQUEST
import com.lttrung.notepro.utils.AppConstant.Companion.READ_EXTERNAL_STORAGE_REQUEST
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddImagesFragment : BottomSheetDialogFragment() {
    private var binding: FragmentAddImagesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initViews()
        initListeners()
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initListeners() {
        binding?.apply {
            btnTakePhoto.setOnClickListener(takePhotoListener)
            btnOpenGallery.setOnClickListener(openGalleryListener)
        }
    }

    private fun initViews() {
        binding = FragmentAddImagesBinding.inflate(layoutInflater)
    }

    private val takePhotoListener: View.OnClickListener by lazy {
        View.OnClickListener {
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
                openCamera()
            }
        }
    }

    private val openGalleryListener: View.OnClickListener by lazy {
        View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST
                )
            } else {
                openGallery()
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        requireActivity().startActivityIfNeeded(cameraIntent, CAMERA_REQUEST)
    }

    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        Log.i("INFO", "openGallery")
        val intent = Intent(requireContext(), ViewGalleryActivity::class.java)
        requireActivity().startActivityIfNeeded(intent, 1)
    }
}