package com.lttrung.notepro.ui.fragments

import android.graphics.BitmapFactory
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
import com.lttrung.notepro.domain.data.locals.models.MediaSelectionLocalsModel
import com.lttrung.notepro.ui.activities.chat.ChatViewModel
import com.lttrung.notepro.ui.activities.editimage.EditImageFragment
import com.lttrung.notepro.ui.adapters.MediaSelectionAdapter
import com.lttrung.notepro.ui.dialogs.builders.DialogBuilder
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.MediaType
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetGallery(private val type: MediaType) : BottomSheetDialogFragment() {
    @Inject
    lateinit var storageRef: StorageReference
    private val binding by lazy {
        FragmentBottomSheetGalleryBinding.inflate(layoutInflater)
    }
    private val viewModel: ChatViewModel by activityViewModels()
    private val mediaAdapter by lazy {
        MediaSelectionAdapter()
            .setIsSelectSingle(true)
            .setItemListener(object : MediaSelectionAdapter.ItemListener {
                override fun onClick(image: MediaSelectionLocalsModel) {
                    if (type == MediaType.VIDEO) {
                        DialogBuilder(requireContext())
                        .setNotice(R.string.ask_send_video)
                        .addButtonLeft(R.string.back)
                        .addButtonRight(R.string.send) {
                            dismiss()
                            sendMediaViaCloudStorage(image.url)
                        }.build().show()
                    } else if (type == MediaType.IMAGE) {
                        try {
                            val bitmap = BitmapFactory.decodeFile(image.url)
                            val f = EditImageFragment(bitmap)
                            f.show(requireActivity().supportFragmentManager, f.tag)
                            dismiss()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
//                    val noticeId = when (type) {
//                        MediaType.IMAGE -> R.string.ask_send_image
//                        MediaType.VIDEO -> R.string.ask_send_video
//                    }
//                    DialogBuilder(requireContext())
//                        .setNotice(noticeId)
//                        .addButtonLeft(R.string.back)
//                        .addButtonRight(R.string.send) {
//                            dismiss()
//                            sendMediaViaCloudStorage(image.url)
//                        }.build().show()
                }
            })
    }

    private fun sendMediaViaCloudStorage(url: String) {
        val byteArray = File(url).readBytes()
        val path = when (type) {
            MediaType.IMAGE -> "images/messages/${System.currentTimeMillis()}.jpg"
            MediaType.VIDEO -> "videos/messages/${System.currentTimeMillis()}.mp4"
        }
        storageRef.child(path)
            .putBytes(byteArray)
            .addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    viewModel.saveUploadResult(
                        hashMapOf<String, String>().apply {
                            val typeValue = when (type) {
                                MediaType.IMAGE -> AppConstant.MESSAGE_CONTENT_TYPE_IMAGE
                                MediaType.VIDEO -> AppConstant.MESSAGE_CONTENT_TYPE_VIDEO
                            }
                            put("TYPE", typeValue)
                            put("URL", uri.toString())
                        }
                    )
                    dismiss()
                }

            }
    }

    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    loadMedia()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViews()
        initObservers()
        initListeners()
        loadMedia()
        return binding.root
    }

    private fun loadMedia() {
        when (type) {
            MediaType.IMAGE -> {
                viewModel.getImages(requireContext(), viewModel.imagePage, PAGE_LIMIT)
            }
            MediaType.VIDEO -> {
                viewModel.getVideos(requireContext(), viewModel.videoPage, PAGE_LIMIT)
            }
        }
    }

    private fun initViews() {
        binding.rvImages.adapter = mediaAdapter
    }

    private fun initObservers() {
        when (type) {
            MediaType.IMAGE -> {
                viewModel.imagesLiveData.observe(viewLifecycleOwner) { paging ->
                    if (paging.hasNextPage) {
                        binding.rvImages.addOnScrollListener(onScrollListener)
                    } else {
                        binding.rvImages.removeOnScrollListener(onScrollListener)
                    }
                    mediaAdapter.submitList(paging.data)
                }
            }
            MediaType.VIDEO -> {
                viewModel.videosLiveData.observe(viewLifecycleOwner) { paging ->
                    if (paging.hasNextPage) {
                        binding.rvImages.addOnScrollListener(onScrollListener)
                    } else {
                        binding.rvImages.removeOnScrollListener(onScrollListener)
                    }
                    mediaAdapter.submitList(paging.data)
                }
            }
        }
    }

    private fun initListeners() {

    }
}