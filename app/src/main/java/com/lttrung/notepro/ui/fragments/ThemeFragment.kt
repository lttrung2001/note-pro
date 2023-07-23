package com.lttrung.notepro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lttrung.notepro.databinding.FragmentThemeBinding
import com.lttrung.notepro.domain.data.networks.models.Theme
import com.lttrung.notepro.ui.activities.chat.ChatInfoActivity
import com.lttrung.notepro.ui.activities.chat.ChatInfoViewModel
import com.lttrung.notepro.ui.adapters.ThemeAdapter

class ThemeFragment(private val themeList: List<Theme>) : BottomSheetDialogFragment() {
    private val binding by lazy {
        FragmentThemeBinding.inflate(layoutInflater)
    }
    private val viewModel: ChatInfoViewModel by activityViewModels()
    private val themeAdapter by lazy {
        ThemeAdapter {
            viewModel.currentTheme = it
            // Call api update note theme (add theme field ref to selected theme)
            val parentActivity = (requireActivity() as ChatInfoActivity)
            parentActivity.socketService.changeTheme(
                roomId = parentActivity.note.id,
                theme = it
            )
            parentActivity.handleChangeTheme(it)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.rvThemes.adapter = themeAdapter
        themeAdapter.submitList(themeList)
        return binding.root
    }
}