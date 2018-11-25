package com.example.mark.chemecalc;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CalcPage extends Fragment implements TextWatcher {

    public static final String title = "Calculations";
    public String description = "";
    private TextView resultText;

    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    public void afterTextChanged(Editable s)
    {
        performCalculation();
    }

    public void performCalculation()
    {
        resultText.setText(calculate());
    }

    public String calculate()
    {
        return "No Calculation Found";
    }

    public View createView(LayoutInflater inflater, Integer layoutId, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View this_view = inflater.inflate(layoutId, container, false);


        resultText = (TextView)this_view.findViewById(R.id.resultText);

//        ArrayList<EditText> editTextFields = new ArrayList<EditText>();
        //getAllChildren(this_view);
        ConstraintLayout layout = (ConstraintLayout)this_view.findViewById(R.id.calc_layout_wrapper);
        for(int i=0; i < layout.getChildCount(); i++)
        {
            if(layout.getChildAt(i) instanceof EditText) {
                EditText editTextField = (EditText) layout.getChildAt(i);
                editTextField.addTextChangedListener(this);
//                editTextFields.add((EditText))
            }
        }


        return this_view;
    }
}
