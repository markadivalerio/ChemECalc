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

import java.text.DecimalFormat;
import java.util.*;

public class CalcPressureDrop extends CalcPage implements AdapterView.OnItemSelectedListener {

    public static final String title = "Pressure Drop";
    public final String description = "Calculate the Pressure Drop of a fluid within a pipe";

    public HashMap<String, HashMap<String, String>> actualDiamRef = new HashMap<String, HashMap<String, String>>();
    public HashMap<String, HashMap<String, String>> fittingsRef = new HashMap<String, HashMap<String, String>>();

    public TableLayout fittingsTable;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        EditText actual = (EditText) getView().findViewById(R.id.actualDiamEditText);
        Spinner sched = (Spinner) getView().findViewById(R.id.schedSpinner);
        Spinner nomSize = (Spinner) getView().findViewById(R.id.nomSizeSpinner);
        String schedValue = sched.getSelectedItem().toString();
        String nomValue = nomSize.getSelectedItem().toString();

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

         Spinner sched = (Spinner) getView().findViewById(R.id.schedSpinner);
         Spinner nomSize = (Spinner) getView().findViewById(R.id.nomSizeSpinner);

         switch(thisView.getId())
         {
             case R.id.actualDiamEditText:
                 sched.setSelection(0, false);
                 nomSize.setSelection(0, false);
                 break;
         }

        performCalculation();
    }

    public double getDoubleValue(int rid)
    {
        try {
            TextView view = (TextView) getView().findViewById(rid);
            String textValue = view.getText().toString();
            return Double.parseDouble(textValue);
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }


    public String calculate()
    {
        double flow = getDoubleValue(R.id.flowEditText);
        double specGrav = getDoubleValue(R.id.specGravEditText);
        double visc = getDoubleValue(R.id.viscEditText);
        double diam = getDoubleValue(R.id.actualDiamEditText);
        double pipeLen = getDoubleValue(R.id.pipeLenEditText);
        double roughnessF = getDoubleValue(R.id.roughFacEditText);

        TextView calcSummary = (TextView) getView().findViewById(R.id.calcSummaryTextView);

        String inputText = "F: " + flow +
                " SG: " + specGrav +
                " u: " + visc +
                " D: " + diam +
                " L: " + pipeLen +
                " RF: " + roughnessF;

        double velocity = calcVelocity(flow, diam);
        double density = calcDensity(specGrav);
        double reynolds = calcReynolds(diam, velocity, density, visc);
        double frictionF = calcFrictionFactor(reynolds, roughnessF, diam);
        double pipePDrop = calcPipePDrop();


        String calcSumText = "" +
//                " Vel: " + velocity +
//                " Den: " + density +
                " Re: " + (new DecimalFormat("##.#####E00").format(reynolds)) +
                " \nFF: " + (new DecimalFormat("##.#####E00").format(frictionF)) +
                " \nPPD: " + (new DecimalFormat("##.#####E00").format(pipePDrop));

        String other = " Vel: " + (new DecimalFormat("##.#####E00").format(velocity)) +
                "\nDen: " + (new DecimalFormat("##.#####E00").format(density)) +
                "\n FF: " + (new DecimalFormat("##.#####E00").format(frictionF));
        calcSummary.setText(calcSumText);

        return other;
    }

    public double calcVelocity(double flow, double diam)
    {
        try
        {
            return (0.408496 * flow) / (Math.pow(diam, 2.0));
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }


    public double calcDensity(double specGrav)
    {
        return (specGrav * 62.43);
    }


    public double calcReynolds(double diameter, double velocity, double density, double viscocity)
    {
        try
        {
            return (124 * diameter * velocity * density) / viscocity;
        }
        catch(Exception e)
        {
            return 0.0;
        }

    }


    public double calcFrictionFactor(double reynolds, double roughnessF, double diam)
    {
        double frictionFactor = 0.0;
        diam = diam / 39.37; // converts inches to meters
        roughnessF = roughnessF / 1000.0; // mm to m
        try
        {
            double left = Math.pow((7.0 / reynolds), 0.9);
            double right = (0.27 * roughnessF / diam);

            double a_val = Math.pow(
                (-2.457 * Math.log(
                        (left + right)
                )),
                16.0);
            double b_val = Math.pow((37530.0 / reynolds), 16.0);

            double churchillFF = 8.0 * Math.pow(
                    Math.pow((8.0 / reynolds), 12.0)
                    + 1 / Math.pow((a_val + b_val), 1.5), (1.0 / 12.0));
            TextView fitSum = (TextView) getView().findViewById(R.id.fittingSummaryTextView);
            String sum = "a=" + (new DecimalFormat("##.#####E00").format(a_val))
                    + "\nb="+(new DecimalFormat("##.#####E00").format(b_val));
            fitSum.setText(sum);
            return churchillFF;
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }


    public double calcPipePDrop()
    {
        double pipePDrop = 0.0;

        return pipePDrop;
    }

    public double calFittingKValue(double reynolds, double diam,
            double k_1, double k_inf, double k_d)
    {
        double kVal = 1.0;

        return kVal;
    }

    public double calcFittingPDrop(double reynolds, double diam, double kval)
    {
        double kVal = 1.0;

        double grav_accel_const = 32.8;

        return kVal;
    }


    public double sumFittings()
    {
        double pDropTotal = 0.0;
        for(int i = 1; i < fittingsTable.getChildCount(); i++)
        {
            TableRow row = (TableRow) fittingsTable.getChildAt(i);
            EditText pDropEditText = (EditText)row.getChildAt(row.getChildCount() - 2);
            try {
                pDropTotal += Double.parseDouble(pDropEditText.getText().toString());
            }
            catch(NullPointerException | NumberFormatException e) { /* Do Nothing */
            }
        }
        return pDropTotal;
    }

//    public double setRowPDrop(TableRow row)
//    {
//        return 0.0;
//    }
//
//    public double calcFittingKVal(View row)
//    {
//        return 1.0;
//    }
//
//    public double calcFittingPDrop(View row)
//    {
//        return 1.0;
//    }

    public void loadAssets() {
        actualDiamRef = loadReferenceCSV("actual_diameter_reference.csv");
        fittingsRef = loadReferenceCSV("fittings.csv");
    }

    public void initFields(View thisView)
    {

        Spinner sched = (Spinner) thisView.findViewById((R.id.schedSpinner));
        sched.setOnItemSelectedListener(this);

        Spinner nomSize = (Spinner) thisView.findViewById(R.id.nomSizeSpinner);
        nomSize.setOnItemSelectedListener(this);

        EditText actual = (EditText) thisView.findViewById(R.id.actualDiamEditText);
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
        title.setMaxWidth(256);
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
        fName.setMaxWidth(256);
        row.addView(fName);


        EditText fQty = new EditText(activity);
        fQty.setTag("qty");
        fQty.setText("1");
        fQty.setGravity(Gravity.CENTER);
        row.addView(fQty);


        if(fittingsRef.containsKey(fittingName))
        {
            String fKValNumber = fittingsRef.get(fittingName).get("k_1");
            String fPDropNumber = fittingsRef.get(fittingName).get("k_inf");

            TextView fKVal = new TextView(activity);
            fKVal.setText(fKValNumber);
            fKVal.setTag("kval");
            fKVal.setGravity(Gravity.CENTER);
            row.addView(fKVal);

            TextView fPDrop = new TextView(activity);
            fPDrop.setText(fPDropNumber);
            fPDrop.setTag("pdrop");
            fPDrop.setGravity(Gravity.CENTER);
            row.addView(fPDrop);
        }
        else
        {
            String fKValNumber = "1";
            String fPDropNumber = "1";

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
        }


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
        // TextView descriptionView = (TextView) thisView.findViewById(R.id.description);
        // descriptionView.setText(description);


        loadAssets();

        initFields(thisView);

        initTable(thisView);


        return thisView;
    }
}
