package com.example.mark.chemecalc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.opencsv.CSVReader;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.measure.Measure;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

import static javax.measure.unit.NonSI.*;
import static javax.measure.unit.SI.*;


public class CalcPage extends Fragment implements TextWatcher, OnCustomEventListener {

    public static final String title = "Calculations";
    public String description = "";
    private TextView resultText;
    private static UnitFormat unitFormat = UnitFormat.getInstance();
    private HashMap<Integer, String> previousUnits = new HashMap<Integer, String>();

    public void initUnitConverter()
    {
        unitFormat.label(GALLON_LIQUID_US.divide(MINUTE),"Gpm");
        unitFormat.label(LITER.divide(MINUTE),"Lpm");
        unitFormat.label(CENTI(POISE), "cP");
        unitFormat.label(PASCAL.times(SECOND),"Pas");
    }

    public double getInputValue(Integer viewId, Integer unitsViewId, String desiredUnitStr)
    {
        return getInputValue(viewId, unitsViewId, desiredUnitStr, null);
    }

    public double getInputValue(Integer viewId, Integer unitsViewId, String desiredUnitStr, String fromUnitsStr)
    {
        double rawValue = getDoubleValue(viewId);
        if(unitsViewId == null || desiredUnitStr == null)
        {
            return rawValue;
        }

        Spinner unitsSpinner = getView().findViewById(unitsViewId);
        String rawUnitStr = unitsSpinner.getSelectedItem().toString();
        if(fromUnitsStr != null) {
            rawUnitStr = fromUnitsStr;
        }


        if(rawUnitStr == null || rawUnitStr.length() == 0
            || desiredUnitStr == null || desiredUnitStr.length() == 0
            || rawUnitStr == desiredUnitStr
            || !unitFormat.isValidIdentifier(rawUnitStr)
            || !unitFormat.isValidIdentifier(desiredUnitStr))
        {
            return rawValue;
        }

        Unit rawUnit = Unit.valueOf(rawUnitStr);
        Unit desiredUnit = Unit.valueOf(desiredUnitStr);

        UnitConverter converter = rawUnit.getConverterTo(desiredUnit);
        double convertedValue = converter.convert(Measure.valueOf(rawValue, rawUnit).doubleValue(rawUnit));

        return convertedValue;
    }

    public double getDoubleValue(int rid)
    {
        return getDoubleValue(getView().findViewById(rid));
    }

    public double getDoubleValue(View view)
    {
        if(view instanceof InputView)
        {
            InputView iView = (InputView) view;
            return iView.getValue();
        }

        try {
            String textValue = ((TextView) view).getText().toString();
            return Double.parseDouble(textValue);
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }

    public String sciNotation(double value)
    {
        return new DecimalFormat("#.###E0").format(value);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
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

        initUnitConverter();

        resultText = (TextView)this_view.findViewById(R.id.resultText);

        ConstraintLayout layout = (ConstraintLayout)this_view.findViewById(R.id.calcConstraintLayoutWrapper);
        for(int i=0; i < layout.getChildCount(); i++)
        {
            View thisView = layout.getChildAt(i);
            if(thisView instanceof InputView) {
                InputView iView = (InputView) thisView;
                iView.setCustomEventListener(this);
                iView.resetListeners();
            }
            if(thisView instanceof EditText) {
                EditText editTextField = (EditText) thisView;
                editTextField.addTextChangedListener(this);
            }
            else if(thisView instanceof Spinner) {
                Spinner spin = (Spinner) thisView;
                if(String.valueOf(spin.getTag()).contains("unit_selector"))
                {

                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view,
                                                   int position, long id) {

                            String newUnits = adapterView.getItemAtPosition(position).toString();
                            int unitsId = adapterView.getId();
                            int textId = -1;
                            String previousUnitsStr = null;
                            if(previousUnits.containsKey(unitsId))
                            {
                                previousUnitsStr = previousUnits.get(unitsId);
                            }
                            previousUnits.put(unitsId, newUnits);
                            switch(unitsId)
                            {
                                case R.id.units_diam:
                                    textId = R.id.actualDiamEditText;
                                    break;
                                case R.id.units_pipe_length:
                                    textId = R.id.pipeLenEditText;
                                    break;
                                case R.id.units_roughness_factor:
                                    textId = R.id.roughFacEditText;
                                    break;
                                default:
                                    textId = -1;
                                    break;
                            }
                            if(textId != -1)
                            {
                                TextView valueView = getView().findViewById(textId);
                                String newValue = String.valueOf(getInputValue(textId, unitsId, newUnits, previousUnitsStr));
                                if(newValue != String.valueOf(0.0)) {
                                    valueView.setText(newValue);
                                }

                            }
//                            adapterView.getItemAtPosition(position).toString();
                            performCalculation();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }
        }

        return this_view;
    }

    @Override
    public void onChangeEvent() {
        Log.w("test", "here100");
        performCalculation();
    }
}
