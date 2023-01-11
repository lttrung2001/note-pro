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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuBottomSheetDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var binding: FragmentMenuBottomSheetDialogBinding? = null

    private val takePhotoListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
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
        View.OnClickListener { view ->
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
        bottomSheet.show(parentFragmentManager, MenuBottomSheetDialogFragment.toString())
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuBottomSheetDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuBottomSheetDialogFragment().apply {

            }
    }
}