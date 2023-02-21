package com.lttrung.notepro.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

/*
* This is the code for a login activity in an Android app.
* It is using the Google Sign-In API to handle the user's login process.
* The activity uses data binding to bind views in the layout to variables in the activity's code.
* When the activity is created, it sets onClickListeners for the login, Google login, and register buttons.
* When the login button is clicked, it switches to the MainActivity.
* When the Google login button is clicked,
* it checks if the user has already signed in with a Google account, and if so, it switches to the MainActivity.
* If the user has not signed in, it starts the Google Sign-In process by creating a GoogleSignInOptions object
* and a GoogleSignInClient object and then starting the sign-in intent.
* When the user finishes signing in, the onActivityResult method is called and it checks the requestCode and resultCode.
* If the requestCode is for Google Sign-In, it gets the task that completed and calls the handleSignInResult method,
* where it tries to get the result from the task and if successful, it shows a toast with the user's email and switches to the MainActivity.
* If there is an exception, it shows a toast with the message "Sign in failed" and logs the error.
* */

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val btnToForgotPasswordListener: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private val btnLoginOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            switchToMain()
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

        binding.btnToForgotPassword.setOnClickListener(btnToForgotPasswordListener)
        binding.btnLogin.setOnClickListener(btnLoginOnClickListener)
        binding.btnGoogleLogin.setOnClickListener(btnGoogleLoginListener)
        binding.btnToRegister.setOnClickListener(btnToRegisterOnClickListener)

        setContentView(binding.root)
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