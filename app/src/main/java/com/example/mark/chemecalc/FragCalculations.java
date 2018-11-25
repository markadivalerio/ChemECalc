package com.example.mark.chemecalc;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class FragCalculations extends Fragment implements OnClickListener{

    public static final String title = "Calculations";

    @Override
    public void onClick(View v) {
        Fragment calc_frag = null;
        String title = "Calculations";
        switch(v.getId())
        {
            case R.id.reynoldsButton:
                calc_frag = new CalcReynolds();
                title = CalcReynolds.title;
                break;
        }

        if(calc_frag != null)
        {
            // TODO: Set back button
            getFragmentManager().beginTransaction().replace(
                    R.id.fragment_container,
                    calc_frag).commit();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View thisView = inflater.inflate(R.layout.frag_calculations, container, false);

        ArrayList<View> allButtons;
        allButtons = ((ConstraintLayout) thisView.findViewById(R.id.calculations_layout)).getTouchables();

        for(View btnView: allButtons)
        {
            btnView.setOnClickListener(this);
        }
        return thisView;
    }
}
