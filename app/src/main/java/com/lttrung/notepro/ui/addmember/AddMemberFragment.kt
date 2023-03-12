package com.lttrung.notepro.ui.addmember

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.FragmentAddMemberBinding
import com.lttrung.notepro.ui.showmembers.ShowMembersViewModel
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMemberFragment : BottomSheetDialogFragment() {
    private var binding: FragmentAddMemberBinding? = null
    private val addMemberViewModel: ShowMembersViewModel by activityViewModels()
    private lateinit var roleAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("INFO", "onCreateView")
        binding = FragmentAddMemberBinding.inflate(layoutInflater)

        initViews()
        initAdapters()
        initListeners()
        initObservers()

        return binding!!.root
    }

    private fun initAdapters() {
        val roles = arrayListOf("editor", "viewer")
        roleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, roles)
        binding!!.roleSpinner.adapter = roleAdapter
    }

    private fun initViews() {
        bindProgressButton(binding!!.addButton)
        binding!!.addButton.attachTextChangeAnimator()
    }

    private fun initObservers() {
        addMemberViewModel.member.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding!!.addButton.isClickable = false
                    binding!!.addButton.showProgress {
                        buttonTextRes = R.string.loading
                        progressColor = Color.WHITE
                    }
                }

                is Resource.Success -> {
                    binding!!.addButton.isClickable = true
                    binding!!.addButton.hideProgress(R.string.add)
                }

                is Resource.Error -> {
                    binding!!.addButton.isClickable = true
                    binding!!.addButton.hideProgress(R.string.add)
                    Log.e("ERROR", resource.message)
                }
            }
        }
    }

    private fun initListeners() {
        binding?.apply {
            addButton.setOnClickListener(addListener)
        }
    }

    private val addListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val note = requireActivity().intent.getSerializableExtra(NOTE) as Note
            val email = binding!!.email.text?.trim().toString()
            val role = binding!!.roleSpinner.selectedItem.toString()
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                addMemberViewModel.addMember(note.id, email, role)
            } else {
                binding!!.email.error = getString(R.string.this_text_is_not_email_type)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("INFO", "onDetach")
    }
}