package android.freelessons.org.sampleandroidappusingfirebase.ui

import android.app.DialogFragment
import android.freelessons.org.sampleandroidappusingfirebase.R
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class SignUpUI : DialogFragment() {
    private var signUpButton: Button? = null
    private var passwordEditText: EditText? = null
    private var emailAutoCompleteTextView: AutoCompleteTextView? = null
    var firebaseAuth: FirebaseAuth? = null
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val v = inflater.inflate(R.layout.signup, null, false)
        findViews(v)
        firebaseAuth = FirebaseAuth.getInstance()
        return v
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun findViews(rootView: View) {
        signUpButton = rootView.findViewById(R.id.email_sign_up)
        signUpButton?.setOnClickListener { signUp() }
        emailAutoCompleteTextView = rootView.findViewById(R.id.email)
        passwordEditText = rootView.findViewById(R.id.password)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun signUp() {
        if (inputValid()) {
            firebaseAuth!!.createUserWithEmailAndPassword(emailAutoCompleteTextView!!.text.toString(), passwordEditText!!.text.toString()).addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Toast.makeText(activity.applicationContext, "Sign Up successful", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    val exception = task.exception
                    Toast.makeText(activity.applicationContext, "Error: " + if (exception != null) exception.message else "Unknow error", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            if (!isEmailValid) {
                Toast.makeText(context, "Entered email is invalid", Toast.LENGTH_SHORT).show()
            } else if (!isPasswordValid) {
                Toast.makeText(context, "Entered password is invalid. Should be at least 3 characters.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inputValid(): Boolean {
        return emailAutoCompleteTextView!!.text.toString().isNotEmpty() && passwordEditText!!.text.toString().isNotEmpty()
    }

    private val isEmailValid: Boolean
        get() = emailAutoCompleteTextView!!.text.toString().isNotEmpty()
    private val isPasswordValid: Boolean
        get() = passwordEditText!!.text.toString().isNotEmpty() && passwordEditText!!.text.toString().length > 6

    companion object {
        @JvmStatic
        fun newInstance(): SignUpUI {
            return SignUpUI()
        }
    }
}