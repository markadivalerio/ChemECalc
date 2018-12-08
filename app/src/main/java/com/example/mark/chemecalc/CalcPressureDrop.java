package com.example.mark.chemecalc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.*;

public class CalcPressureDrop extends CalcPage implements AdapterView.OnItemSelectedListener {

    public static final String title = "Pressure Drop";
    public final String description = "Calculate the Pressure Drop of a fluid within a pipe";

    public HashMap<String, HashMap<String, String>> actualDiamRef = new HashMap<String, HashMap<String, String>>();
    public HashMap<String, HashMap<String, String>> fittingsRef = new HashMap<String, HashMap<String, String>>();

    public Spinner sched;
    public Spinner nomSize;
    public EditText actual;

    public TableLayout fittingsTable;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /*if(initializedAdapter !=parent.getAdapter() ) {
            initializedAdapter = parent.getAdapter();
            return;
        }*/
        //Spinner sched = (Spinner) getView().findViewById(R.id.schedSpinner);
        //Spinner nomSize = (Spinner) getView().findViewById(R.id.nomSizeSpinner);
        String schedValue = sched.getSelectedItem().toString();
        String nomValue = nomSize.getSelectedItem().toString();

//        EditText actual = (EditText) getView().findViewById(R.id.actualDiamEditText);
        try {
            if (nomValue == null || nomValue == "Nom Sz" || schedValue == null || schedValue == "Sched")
                return;
            if(actualDiamRef.containsKey(nomValue) && actualDiamRef.get(nomValue).containsKey(schedValue)) {
                String actualValue = actualDiamRef.get(nomValue).get(schedValue);
                if (actualValue == null)
                    actualValue = "";
                actual.setText(actualValue);
            }
        }
        catch(Exception e)
        {
            actual.setText(e.getMessage());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void afterTextChanged(Editable s)
    {
         View thisView = getActivity().getCurrentFocus();
         if(thisView == null)
             return;
         switch(thisView.getId())
         {
             case R.id.actualDiamEditText:
                 sched.setSelection(0, false);
                 nomSize.setSelection(0, false);
                 break;
         }

        performCalculation();
    }

    public String calculate()
    {
        return "No Calculation Yet";
    }

    public void loadAssets() {
        actualDiamRef = loadReferenceCSV("actual_diameter_reference.csv");
        fittingsRef = loadReferenceCSV("fittings.csv");
    }

    public void initFields(View thisView)
    {
        sched = (Spinner) thisView.findViewById((R.id.schedSpinner));
        sched.setOnItemSelectedListener(this);

        nomSize = (Spinner) thisView.findViewById(R.id.nomSizeSpinner);
        nomSize.setOnItemSelectedListener(this);

        actual = (EditText) thisView.findViewById(R.id.actualDiamEditText);
        actual.addTextChangedListener(this);

//        Spinner fittingsSpinner = (Spinner) thisView.findViewById(R.id.fittingsSpinner);
//        List<String> fittingNames = new ArrayList<String>();
//        fittingNames.addAll(fittingsRef.keySet());
//        fittingNames.add("Custom");
//        ArrayAdapter<String> adp= new ArrayAdapter<String>(getActivity(), R.layout.calc_pressure_drop, fittingNames);
//        adp.setDropDownViewResource(R.layout.calc_pressure_drop);
//        fittingsSpinner.setAdapter(adp);

        if(fittingsRef.isEmpty())
        {
            loadAssets();
        }
        List<String> fittingOptions = new ArrayList<String>(fittingsRef.keySet());
        Collections.sort(fittingOptions);
        fittingOptions.add(0,"Custom");
        fittingOptions.add(0,"Fittings");

        ArrayAdapter<String> spinnerArrayAdapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, fittingOptions);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner)thisView.findViewById(R.id.fittingsSpinner);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    public void initTable(View thisView)
    {
        fittingsTable = (TableLayout) thisView.findViewById(R.id.fittingsTableLayout);
        FragmentActivity activity = getActivity();

        TableRow headerRow = new TableRow(activity);

        TextView index = new TextView(activity);
        index.setText("");
        headerRow.addView(index);


        TextView title = new TextView(activity);
        title.setText("Fitting Label");
        headerRow.addView(title);

        TextView qty = new TextView(activity);
        qty.setText("Qty");
        qty.setGravity(Gravity.CENTER);
        headerRow.addView(qty);

        TextView kVal = new TextView(activity);
        kVal.setText("K");
        kVal.setGravity(Gravity.CENTER);
        headerRow.addView(kVal);

        TextView pressureDrop = new TextView(activity);
        pressureDrop.setText("P Drop");
        pressureDrop.setGravity(Gravity.CENTER);
        headerRow.addView(pressureDrop);

        TextView emtpyBtn = new TextView(activity);
        emtpyBtn.setText("");
        headerRow.addView(emtpyBtn);

        fittingsTable.addView(headerRow);

        Button addFittingBtn = thisView.findViewById(R.id.addFittingButton);
        addFittingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFitting();
            }
        });
    }

    public void addFitting()
    {
        FragmentActivity activity = getActivity();
        int rowCount = fittingsTable.getChildCount();

        TableRow row = new TableRow(activity);

        TextView rowNumber = new TextView(activity);
        rowNumber.setText(""+rowCount);
        rowNumber.setGravity(Gravity.CENTER);
        row.addView(rowNumber);

        Spinner fittingSpinner = ((Spinner)getView().findViewById(R.id.fittingsSpinner));
        String fittingName = fittingSpinner.getSelectedItem().toString();


        EditText fName = new EditText(activity);
        if(fittingName.equalsIgnoreCase("Fittings"))
            fittingName = "Custom";
        fName.setText(fittingName);
        fName.setTag("name");
        row.addView(fName);


        EditText fQty = new EditText(activity);
        fQty.setTag("qty");
        fQty.setText("1");
        fQty.setGravity(Gravity.CENTER);
        row.addView(fQty);

        String fKValNumber = "1";
        String fPDropNumber = "1";
        if(fittingsRef.containsKey(fittingName))
        {
            fKValNumber = fittingsRef.get(fittingName).get("kval1");
            fPDropNumber = fittingsRef.get(fittingName).get("kval2");
        }

        EditText fKVal = new EditText(activity);
        fKVal.setText(fKValNumber);
        fKVal.setTag("kval");
        fKVal.setGravity(Gravity.CENTER);
        row.addView(fKVal);

        EditText fPDrop = new EditText(activity);
        fPDrop.setText(fPDropNumber);
        fPDrop.setTag("pdrop");
        fPDrop.setGravity(Gravity.CENTER);
        row.addView(fPDrop);

        Button removeBtn = new Button(activity);
        removeBtn.setText("Remove");
        removeBtn.setTag("remove");
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFitting(v);
            }
        });
        row.addView(removeBtn);

//        row.setTag(""+rowCount);
        fittingsTable.addView(row);
    }

    public double calcFittingKVal(View row)
    {
        return new Double(1.0);
    }

    public double calcFittingPDrop(View row)
    {
        return new Double(1.0);
    }

    public void removeFitting(View v)
    {
        TableRow row = (TableRow)v.getParent();
        int rowIndex = fittingsTable.indexOfChild(row);
        int rowSize =  fittingsTable.getChildCount();

        for(int i = rowIndex+1; i < rowSize; i++)
        {
            TableRow thisRow = (TableRow) fittingsTable.getChildAt(i);
            TextView rowIndexView = ((TextView)thisRow.getChildAt(0));
            rowIndexView.setText(""+(i-1));
        }

        fittingsTable.removeView(row);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View thisView = createView(inflater, R.layout.calc_pressure_drop, container, savedInstanceState);
        TextView descriptionView = (TextView) thisView.findViewById(R.id.description);
        descriptionView.setText(description);


        loadAssets();

        initFields(thisView);

        initTable(thisView);


        return thisView;
    }
}
