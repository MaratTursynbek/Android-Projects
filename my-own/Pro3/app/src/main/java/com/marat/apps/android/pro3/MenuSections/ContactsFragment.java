package com.marat.apps.android.pro3.MenuSections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marat.apps.android.pro3.Interfaces.ToolbarTitleChangeListener;
import com.marat.apps.android.pro3.R;

public class ContactsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        ToolbarTitleChangeListener listener = (ToolbarTitleChangeListener) getActivity();
        listener.onTitleChanged(getString(R.string.title_main_fragment_contacts));

        return v;
    }

}
