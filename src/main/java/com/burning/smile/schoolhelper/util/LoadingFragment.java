package com.burning.smile.schoolhelper.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.burning.smile.schoolhelper.R;


/**
 * Created by Buring on 2016/2/17.
 */
public class LoadingFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_loading, null);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setStyle(STYLE_NO_FRAME, R.style.dialog_transparent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


}
