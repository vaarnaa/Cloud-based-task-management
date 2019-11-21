package com.example.taskapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_password.*

class PasswordActivity : BaseActivity(), View.OnClickListener {
    // Declare an instance of Firebase Auth.
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        // These call findViewById on the first time, and then cache the values
        // for faster access in subsequent calls. Clicks are handled in `onClick`.
        buttonChangePasswordSubmit.setOnClickListener(this)
        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun changePassword() {
        val user = auth.currentUser
        val newPassword = et_password.text.toString()
        if (!validatePasswords()) {
            return
        }
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                    // Redirect back to the profile settings page if updated successfully.
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
    }

    private fun validatePasswords(): Boolean {
        var valid = true
        val password = et_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            et_password.error = "Required."
            valid = false
        } else {
            et_password.error = null
        }
        val passwordConf = et_password_confirm.text.toString()
        if (TextUtils.isEmpty(passwordConf)) {
            et_password_confirm.error = "Required."
            valid = false
        } else {
            et_password_confirm.error = null
        }
        if (password != passwordConf) {
            et_password.error = "Both passwords must match!"
            et_password_confirm.error = "Both passwords must match!"
            valid = false
        } else {
            et_password.error = null
            et_password_confirm.error = null
        }
        return valid
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            // The user is signed in.
            passwordButtons.visibility = View.VISIBLE
        } else {
            // The user is signed out, so redirect to the login page.
            passwordButtons.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonChangePasswordSubmit -> changePassword()
        }
    }

    companion object {
        // Used for printing debug messages. Usage: Log.d(TAG, "message")
        private const val TAG = "PasswordActivity"
    }
}