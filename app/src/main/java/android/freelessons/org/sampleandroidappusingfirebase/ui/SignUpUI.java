package android.freelessons.org.sampleandroidappusingfirebase.ui;

import android.app.DialogFragment;
import android.freelessons.org.sampleandroidappusingfirebase.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SignUpUI extends DialogFragment {

    public static SignUpUI newInstance(){
        return new SignUpUI();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signup, null, false);
        return v;
    }
}
