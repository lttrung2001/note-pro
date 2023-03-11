package com.lttrung.notepro.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityLoginBinding
import com.lttrung.notepro.ui.forgotpassword.ForgotPasswordActivity
import com.lttrung.notepro.ui.main.MainActivity
import com.lttrung.notepro.ui.register.RegisterActivity
import com.lttrung.notepro.utils.AppConstant.Companion.RC_SIGN_IN
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private val btnToForgotPasswordListener: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private val btnLoginOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                binding.edtPassword.error = getString(R.string.all_input_required)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtPassword.error = getString(R.string.this_text_is_not_email_type)
            } else {
                viewModel.login(email, password)
            }
        }
    }

    private val btnGoogleLoginListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_firebase_client_id))
                .requestEmail()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                switchToMain()
            } else {
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }

    private val btnToRegisterOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setupListener()
        setupObserver()

        setContentView(binding.root)
    }

    private fun setupObserver() {
        viewModel.login.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnLogin.showProgress {
                        buttonTextRes = R.string.loading
                        progressColor = Color.WHITE
                    }
                    binding.btnLogin.isClickable = false
                }
                is Resource.Success -> {
                    binding.btnLogin.hideProgress(R.string.login)
                    binding.btnLogin.isClickable = true
                    switchToMain()
                }
                is Resource.Error -> {
                    binding.btnLogin.hideProgress(R.string.login)
                    binding.btnLogin.isClickable = true
                    binding.edtPassword.error = resource.message
                }
            }
        }
    }

    private fun setupListener() {
        binding.btnToForgotPassword.setOnClickListener(btnToForgotPasswordListener)
        binding.btnLogin.setOnClickListener(btnLoginOnClickListener)
        binding.btnGoogleLogin.setOnClickListener(btnGoogleLoginListener)
        binding.btnToRegister.setOnClickListener(btnToRegisterOnClickListener)

        bindProgressButton(binding.btnLogin)
        binding.btnLogin.attachTextChangeAnimator()
    }

    private fun switchToMain() {
        startActivity(Intent(this, MainActivity::class.java).also { intent ->
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Toast.makeText(this, account.email, Toast.LENGTH_SHORT).show()
            switchToMain()
        } catch (e: ApiException) {
            Log.e("Error", "signInResult:failed code=${e.statusCode}")
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
        }
    }
}