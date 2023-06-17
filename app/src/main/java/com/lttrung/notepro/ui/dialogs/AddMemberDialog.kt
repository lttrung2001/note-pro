package com.lttrung.notepro.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.DialogAddMemberBinding

class AddMemberDialog(
    context: Context,
    private val addMember: (email: String, role: String) -> Unit
) : Dialog(context) {
    private val binding: DialogAddMemberBinding by lazy {
        DialogAddMemberBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initObservers()
        initListeners()
    }

    private fun initListeners() {
        binding.btnAddMember.setOnClickListener {
            val email = binding.edtAddMemberEmail.text?.trim().toString()
            val role = when (binding.rgRole.checkedRadioButtonId) {
                binding.rdEditor.id -> {
                    binding.rdEditor.text.toString().lowercase()
                }

                binding.rdViewer.id -> {
                    binding.rdViewer.text.toString().lowercase()
                }

                else -> {
                    context.getString(R.string.viewer).lowercase()
                }
            }
            addMember(email, role)
        }
    }

    private fun initObservers() {

    }

    private fun initViews() {
        setContentView(binding.root)
    }
}