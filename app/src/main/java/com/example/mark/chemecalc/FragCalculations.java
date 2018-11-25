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
        CalcPage calc_page = null;
        String this_title = "Calculation";
        switch(v.getId())
        {
            case R.id.calcButton_reynoldsNumber:
                calc_page = new CalcReynolds();
                this_title = CalcReynolds.title;
                break;
            case R.id.calcButton_pressureDrop:
                calc_page = new CalcPressureDrop();
                this_title = CalcPressureDrop.title;
                break;
        }

        if(calc_page != null)
        {
            // TODO: Set back button to go back to FragCalulations page
            getFragmentManager().beginTransaction().replace(
                    R.id.fragment_container,
                    calc_page).commit();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(this_title);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View thisView = inflater.inflate(R.layout.frag_calculations, container, false);

        ArrayList<View> allButtons = ((ConstraintLayout) thisView.findViewById(R.id.calculations_layout)).getTouchables();

        for(View btnView: allButtons)
        {
            btnView.setOnClickListener(this);
        }
        return thisView;
    }
}
