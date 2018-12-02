package com.example.mark.chemecalc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class CalcPressureDrop extends CalcPage implements AdapterView.OnItemSelectedListener {

    public static final String title = "Pressure Drop";
    public final String description = "Calculate the Pressure Drop of a fluid within a pipe";

    public HashMap<String, HashMap<String, String>> actualDiamRef = new HashMap<String, HashMap<String, String>>();
    protected Adapter initializedAdapter=null;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(initializedAdapter !=parent.getAdapter() ) {
            initializedAdapter = parent.getAdapter();
            return;
        }
        switch(parent.getId())
        {
            case R.id.spinner1:
                //sched
                break;
            case R.id.spinner2:
                // nomSz
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String calculate()
    {
        return "No Calculation Yet";
    }

    public void loadAssets() {
        actualDiamRef = loadReferenceCSV("actual_diameter_reference.csv");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View this_view = createView(inflater, R.layout.calc_pressure_drop, container, savedInstanceState);
        TextView descriptionView = (TextView) this_view.findViewById(R.id.description);
        descriptionView.setText(description);
        loadAssets();

        Spinner sched = (Spinner) this_view.findViewById((R.id.spinner1));
        sched.setOnItemSelectedListener(this);

        Spinner nomSz = (Spinner) this_view.findViewById(R.id.spinner2);
        nomSz.setOnItemSelectedListener(this);


        return this_view;
    }
}
