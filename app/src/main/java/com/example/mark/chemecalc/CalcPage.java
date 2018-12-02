package com.example.mark.chemecalc;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.opencsv.CSVReader;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

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

    public HashMap<String, HashMap<String, String>> loadReferenceCSV(String csvFilename)
    {
        HashMap<String, HashMap<String, String>> dictionary = new HashMap<String, HashMap<String, String>>();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(getActivity().getAssets().open(csvFilename)));
            String[] header = csvReader.readNext();
            String[] row;
            while((row = csvReader.readNext()) != null)
            {
                HashMap<String, String> indexedRow = new HashMap<String, String>();
                for(int c=1;c<header.length;c++)
                {
                    String val = (c < row.length) ? row[c] : null;
                    indexedRow.put(header[c], val);
                }
                dictionary.put(row[0], indexedRow);
            }
        }
        catch(IOException e)
        {
            //woopsy
            e.printStackTrace();
        }

        return dictionary;
    }

    public View createView(LayoutInflater inflater, Integer layoutId, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View this_view = inflater.inflate(layoutId, container, false);


        resultText = (TextView)this_view.findViewById(R.id.resultText);

        ConstraintLayout layout = (ConstraintLayout)this_view.findViewById(R.id.calc_layout_wrapper);
        for(int i=0; i < layout.getChildCount(); i++)
        {
            if(layout.getChildAt(i) instanceof EditText) {
                EditText editTextField = (EditText) layout.getChildAt(i);
                editTextField.addTextChangedListener(this);
            }
        }

        return this_view;
    }
}
