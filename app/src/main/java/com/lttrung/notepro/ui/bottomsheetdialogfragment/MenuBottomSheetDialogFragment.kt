package com.lttrung.notepro.ui.bottomsheetdialogfragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lttrung.notepro.databinding.FragmentMenuBottomSheetDialogBinding
import com.lttrung.notepro.utils.AppConstant

class MenuBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var binding: FragmentMenuBottomSheetDialogBinding? = null

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
                    AppConstant.CAMERA_REQUEST
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
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    AppConstant.READ_EXTERNAL_STORAGE_REQUEST
                )
            } else {
                openBottomSheetMenu()
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 1)
    }

    private fun openBottomSheetMenu() {
        val bottomSheet = MenuBottomSheetDialogFragment()
        bottomSheet.show(parentFragmentManager, "MenuBottomSheetDialogFragment")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMenuBottomSheetDialogBinding.inflate(layoutInflater)
        binding!!.btnTakePhoto.setOnClickListener(takePhotoListener)
        binding!!.btnOpenGallery.setOnClickListener(openGalleryListener)
        return binding!!.root
    }
}