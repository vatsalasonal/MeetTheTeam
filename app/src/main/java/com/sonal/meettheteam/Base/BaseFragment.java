package com.sonal.meettheteam.Base;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    public BaseActivity _context;

    public void showProgress(){

        _context.showProgress();
    }

    public void closeProgress(){

        _context.closeProgress();
    }
}
