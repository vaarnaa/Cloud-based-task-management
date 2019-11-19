package com.example.taskapplication

import android.app.ProgressDialog
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity

// Should 'open' be changed to 'abstract'?
open class BaseActivity : AppCompatActivity() {
    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }
}