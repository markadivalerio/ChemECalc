package com.example.mark.chemecalc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.jetbrains.annotations.Nullable;

public class CalcReynolds extends Fragment implements View.OnClickListener {
    Button calcButton;
    TextView resultText;

    public void onClick(View v)
    {
        if(v == calcButton) {
            EditText input1 = (EditText)getView().findViewById(R.id.editText1); // density
            EditText input2 = (EditText)getView().findViewById(R.id.editText2); // diameter
            EditText input3 = (EditText)getView().findViewById(R.id.editText3); // velocity
            EditText input4 = (EditText)getView().findViewById(R.id.editText4); // viscosity
            double density = Double.parseDouble(input1.getText().toString());
            double diameter = Double.parseDouble(input2.getText().toString());
            double velocity = Double.parseDouble(input3.getText().toString());
            double viscosity = Double.parseDouble(input4.getText().toString());

            double resultDouble = (density * diameter * velocity) / viscosity;
            resultText.setText(Double.toString(resultDouble)); // Reynolds Number
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View this_view = inflater.inflate(R.layout.calc_reynolds, container, false);

        calcButton = (Button)this_view.findViewById(R.id.calcButton);
        resultText = (TextView)this_view.findViewById(R.id.resultText);

        calcButton.setOnClickListener(this);

        return this_view;
    }
}
