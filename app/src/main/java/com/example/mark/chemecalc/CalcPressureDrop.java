package com.example.mark.chemecalc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import org.jetbrains.annotations.Nullable;

public class CalcPressureDrop extends CalcPage {

    public static final String title = "Pressure Drop";
    public final String description = "Calculate the Pressure Drop of a fluid within a pipe";

    public String calculate()
    {
        try
        {
            EditText input1 = (EditText)getView().findViewById(R.id.editText1); // density
            EditText input2 = (EditText)getView().findViewById(R.id.editText2); // diameter
            EditText input3 = (EditText)getView().findViewById(R.id.editText3); // velocity
            EditText input4 = (EditText)getView().findViewById(R.id.editText4); // viscosity
            double density = Double.parseDouble(input1.getText().toString());
            double diameter = Double.parseDouble(input2.getText().toString());
            double velocity = Double.parseDouble(input3.getText().toString());
            double viscosity = Double.parseDouble(input4.getText().toString());

            double resultDouble = (density * diameter * velocity) / viscosity;
            return Double.toString(resultDouble);
        }
        catch(Exception e)
        {
            return "Error: "+e.getMessage();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View this_view = createView(inflater, R.layout.calc_pressure_drop, container, savedInstanceState);
        TextView descriptionView = (TextView) this_view.findViewById(R.id.description);
        descriptionView.setText(description);
        return this_view;
    }
}
