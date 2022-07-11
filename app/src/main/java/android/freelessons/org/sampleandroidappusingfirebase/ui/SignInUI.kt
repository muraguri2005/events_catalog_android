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

class SignInUI : DialogFragment() {
    var signInButton: Button? = null
    var passwordEditText: EditText? = null
    var emailAutoCompleteTextView: AutoCompleteTextView? = null
    var firebaseAuth: FirebaseAuth? = null
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val v = inflater.inflate(R.layout.signin, null, false)
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
        signInButton = rootView.findViewById<View>(R.id.email_sign_in) as Button
        signInButton!!.setOnClickListener { view: View? -> signIn() }
        emailAutoCompleteTextView = rootView.findViewById(R.id.email)
        passwordEditText = rootView.findViewById(R.id.password)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun signIn() {
        if (inputValid()) {
            firebaseAuth!!.signInWithEmailAndPassword(emailAutoCompleteTextView!!.text.toString(), passwordEditText!!.text.toString()).addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Toast.makeText(activity.applicationContext, "Sign in successful", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(activity.applicationContext, "Error: ", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            if (!isEmailValid) {
                Toast.makeText(context, "Entered email is invalid", Toast.LENGTH_SHORT).show()
            } else if (isEmailValid) {
                Toast.makeText(context, "Entered password is invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inputValid(): Boolean {
        return isEmailValid && isPasswordValid
    }

    //&& emailAutoCompleteTextView.getText().toString();
    private val isEmailValid: Boolean
        get() = !emailAutoCompleteTextView!!.text.toString().isEmpty()

    //&& emailAutoCompleteTextView.getText().toString();
    private val isPasswordValid: Boolean
        get() = !passwordEditText!!.text.toString().isEmpty() && passwordEditText!!.text.toString().length > 6

    companion object {
        @JvmStatic
        fun newInstance(): SignInUI {
            return SignInUI()
        }
    }
}