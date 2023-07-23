package com.lttrung.notepro.ui.activities.chat

import android.content.Intent
import android.view.WindowManager
import androidx.activity.viewModels
import com.lttrung.notepro.databinding.ActivityChatInfoBinding
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.Theme
import com.lttrung.notepro.ui.activities.viewmembers.ViewMembersActivity
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.dialogs.AddMemberDialog
import com.lttrung.notepro.ui.fragments.ThemeFragment
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatInfoActivity : BaseActivity() {
    override val binding by lazy {
        ActivityChatInfoBinding.inflate(layoutInflater)
    }
    override val viewModel: ChatInfoViewModel by viewModels()
    val note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }
    private lateinit var addMemberDialog: AddMemberDialog

    override fun initListeners() {
        super.initListeners()
        binding.apply {
            btnChatInfoViewMembers.setOnClickListener {
                startActivity(Intent(this@ChatInfoActivity, ViewMembersActivity::class.java).apply {
                    putExtra(NOTE, note)
                })
            }
            ivChatInfoAddMember.setOnClickListener {
                addMemberDialog = AddMemberDialog(this@ChatInfoActivity) { email, role ->
                    viewModel.addMember(note.id, email, role)
                }
                addMemberDialog.show()
                addMemberDialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
                )
            }
            btnChatInfoChangeTheme.setOnClickListener {
                if (viewModel.themeList.isEmpty()) {
                    viewModel.getThemeList()
                } else {
                    ThemeFragment(viewModel.themeList).also { f ->
                        f.show(supportFragmentManager, f.tag)
                    }
                }
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.addMemberLiveData.observe(this) {
            handleAddResult(it)
        }
        viewModel.themeListLiveData.observe(this) {
            ThemeFragment(it).also { f ->
                f.show(supportFragmentManager, f.tag)
            }
        }
    }

    fun handleChangeTheme(theme: Theme) {
        // Handle change theme
        setResult(RESULT_OK, intent.apply {
            putExtra(AppConstant.THEME, theme)
        })
        finish()
    }

    private fun handleAddResult(newMember: Member) {
        addMemberDialog.dismiss()

        socketService.sendAddMemberMessage(note.id, newMember.email)
    }
}