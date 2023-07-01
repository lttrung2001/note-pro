package com.lttrung.notepro.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.StorageReference
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.FragmentBottomSheetGalleryBinding
import com.lttrung.notepro.domain.data.locals.models.ImageSelectionLocalsModel
import com.lttrung.notepro.ui.activities.chat.ChatViewModel
import com.lttrung.notepro.ui.adapters.ImageSelectionAdapter
import com.lttrung.notepro.ui.dialogs.UploadDialog
import com.lttrung.notepro.ui.dialogs.builders.DialogBuilder
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetGallery : BottomSheetDialogFragment() {
    @Inject
    lateinit var storageRef: StorageReference
    private val binding by lazy {
        FragmentBottomSheetGalleryBinding.inflate(layoutInflater)
    }
    private val viewModel: ChatViewModel by activityViewModels()
    private val imageAdapter by lazy {
        ImageSelectionAdapter()
            .setIsSelectSingleImage(true)
            .setItemListener(object : ImageSelectionAdapter.ItemListener {
                override fun onClick(image: ImageSelectionLocalsModel) {
                    DialogBuilder(requireContext())
                        .setNotice(R.string.ask_send_image)
                        .addButtonLeft(R.string.back)
                        .addButtonRight(R.string.send) {
                            dismiss()
                            // Send image
                            UploadDialog(requireContext(), image.url) { task ->
                                viewModel.saveUploadResult(task)
                            }.show()
                        }.build().show()
                }
            })
    }
    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            // Load more images
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViews()
        initObservers()
        initListeners()
        viewModel.getImages(requireContext(), viewModel.imagePage, PAGE_LIMIT)
        return binding.root
    }

    private fun initViews() {
        binding.rvImages.adapter = imageAdapter
    }

    private fun initObservers() {
        viewModel.imagesLiveData.observe(viewLifecycleOwner) { paging ->
            if (paging.hasNextPage) {
                binding.rvImages.addOnScrollListener(onScrollListener)
            } else {
                binding.rvImages.removeOnScrollListener(onScrollListener)
            }
            imageAdapter.submitList(paging.data)
        }
    }

    private fun initListeners() {

    }
}