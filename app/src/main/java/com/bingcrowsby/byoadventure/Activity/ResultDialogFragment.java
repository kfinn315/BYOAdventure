package com.bingcrowsby.byoadventure.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bingcrowsby.byoadventure.R;

/**
 * Created by kevinfinn on 3/8/15.
 */
public class ResultDialogFragment extends DialogFragment {

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.resultfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle("Daily Report");

        TextView tv4 = (TextView) view.findViewById(R.id.textView4);
        tv4.setText("HEY THERE");

    }
}
