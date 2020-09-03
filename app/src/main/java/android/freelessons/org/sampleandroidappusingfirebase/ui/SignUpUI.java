package android.freelessons.org.sampleandroidappusingfirebase.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.freelessons.org.sampleandroidappusingfirebase.R;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;


public class SignUpUI extends DialogFragment {

    Button signUpButton;
    EditText passwordEditText;
    AutoCompleteTextView emailAutoCompleteTextView;
    FirebaseAuth firebaseAuth;
    public static SignUpUI newInstance(){
        return new SignUpUI();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signup, null, false);
        findViews(v);
        firebaseAuth = FirebaseAuth.getInstance();

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window!=null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            }
        }
    }
    private void findViews(View rootView){
        signUpButton= rootView.findViewById(R.id.email_sign_up);
        signUpButton.setOnClickListener(view -> signUp());
        emailAutoCompleteTextView= rootView.findViewById(R.id.email);
        passwordEditText= rootView.findViewById(R.id.password);
    }
    private void signUp(){
        if(inputValid()) {
            firebaseAuth.createUserWithEmailAndPassword(emailAutoCompleteTextView.getText().toString(), passwordEditText.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Sign Up successful", Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    Exception exception = task.getException();
                    Toast.makeText(getActivity().getApplicationContext(), "Error: "+(exception != null ? exception.getMessage() : "Unknow error"), Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            if(!isEmailValid()){
                Toast.makeText(getContext(), "Entered email is invalid", Toast.LENGTH_SHORT).show();
            }else if(!isPasswordValid()){
                Toast.makeText(getContext(), "Entered password is invalid. Should be at least 3 characters.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean inputValid(){
        return !emailAutoCompleteTextView.getText().toString().isEmpty()&&!passwordEditText.getText().toString().isEmpty();
    }
    private boolean isEmailValid(){
        return !emailAutoCompleteTextView.getText().toString().isEmpty();
    }
    private boolean  isPasswordValid(){
        return !passwordEditText.getText().toString().isEmpty() && passwordEditText.getText().toString().length()>6;
    }
}
