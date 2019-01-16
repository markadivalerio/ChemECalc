package com.example.mark.chemecalc;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public class CalcPressureDrop extends CalcPage implements AdapterView.OnItemSelectedListener {

    public static final String title = "Pressure Drop";
    public final String description = "Calculate the Pressure Drop of a fluid within a pipe";

    private final String MiscFLabel = "Misc. P. Drop (psi)";

    public HashMap<String, HashMap<String, String>> actualDiamRef = new HashMap<String, HashMap<String, String>>();
    public HashMap<String, HashMap<String, String>> fittingsRef = new HashMap<String, HashMap<String, String>>();

    public TableLayout fittingsTable;

    private double velocity = 0.0;
    private double reynolds = 0.0;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        EditText actual = getView().findViewById(R.id.actualDiamEditText);
        Spinner sched = getView().findViewById(R.id.schedSpinner);
        Spinner nomSize = getView().findViewById(R.id.nomSizeSpinner);
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

         Spinner sched = getView().findViewById(R.id.schedSpinner);
         Spinner nomSize = getView().findViewById(R.id.nomSizeSpinner);

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
//        double flow = getDoubleValue(R.id.flowEditText);
//        double specGrav = getDoubleValue(R.id.specGravEditText);
//        double visc = getDoubleValue(R.id.viscEditText);
//        double diam = getDoubleValue(R.id.actualDiamEditText);
//        double roughnessF = getDoubleValue(R.id.roughFacEditText);
//        double flow = getInputValue(R.id.flowEditText,R.id.units_flow,"Gpm");
        double flow = ((InputView) getView().findViewById(R.id.flow)).getValueIn("Gpm");
        double specGrav = ((InputView) getView().findViewById(R.id.specific_gravity)).getValue();
        double visc = ((InputView) getView().findViewById(R.id.viscosity)).getValueIn("cP");
        double diam = getInputValue(R.id.actualDiamEditText, R.id.units_diam, "in");
        double pipeLen = ((InputView) getView().findViewById(R.id.pipe_length)).getValueIn("ft");
        double roughnessF = ((InputView) getView().findViewById(R.id.roughness_factor)).getValueIn("mm");

//        double pipeLenKM = getInputValue(R.id.pipeLenEditText, R.id.units_pipe_length, "km");
//        String whatevah = "km = " + pipeLenKM;
//        TextView fitSum = getView().findViewById(R.id.fittingSummaryTextView);
//        fitSum.setText(whatevah);
//        return whatevah;

        double density = calcDensity(specGrav);
        velocity = calcVelocity(flow, diam);
        reynolds = calcReynolds(diam, density, visc);
        double frictionF = calcFrictionFactor(roughnessF, diam);
        double pipePDropPer100 = calcPipePDropPer100(frictionF, density, diam);

        double pPDrop = calcPipePDrop(pipePDropPer100);
        double fPDrop = sumFittings(true);
        TableRow miscRow = (TableRow) fittingsTable.getChildAt(1);
        double mPDrop = getDoubleValue(miscRow.getChildAt(3));

        double totalPDrop = pPDrop + fPDrop + mPDrop;

//                String calcSumText = "Re: " + sciNotation(reynolds);
        String calcSumText = "Re: " + sciNotation(reynolds) +
                "\nFF: " +  sciNotation(frictionF);

       TextView fitSum = getView().findViewById(R.id.fittingSummaryTextView);
       String sum = "Vel: " + sciNotation(velocity) +
                "\nDen: " + sciNotation(density);
       fitSum.setText(sum);

       DecimalFormat dcmlFmt = new DecimalFormat("#.###");
       String other = "Pipe + Fittings + Misc = Total\n"
               + dcmlFmt.format(pPDrop) + "+"
               + dcmlFmt.format(fPDrop) + "+"
               + dcmlFmt.format(mPDrop) + "="
               + dcmlFmt.format(totalPDrop);

        TextView calcSummary = getView().findViewById(R.id.calcSummaryTextView);
        calcSummary.setText(calcSumText);

        return other;

    }

    public double calcDensity(double specGrav)
    {
        return (specGrav * 62.43);
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



    public double calcReynolds(double diameter, double density, double viscosity)
    {
        try
        {
            return (124 * diameter * velocity * density) / viscosity;
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }


    public double calcFrictionFactor(double roughnessF, double diam)
    {
        /*
        churchill's FF equation:
        FF = 8((8/Re)12 + 1/(A+B)1.5)1/12
        A = (-2.457*ln((7/Re)^0.9+0.27*ε/D))^16
        B = (37530/Re)^16
         */
//        diam = diam / 39.37; // converts inches to meters
        diam = diam / 12.0; // inches to feet
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

//            TextView fitSum = getView().findViewById(R.id.fittingSummaryTextView);
//            String sum = "a=" + sciNotation(a_val)
//                    + "\nb=" + sciNotation(b_val);
//            fitSum.setText(sum);

            return churchillFF;
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }


    public double calcPipePDropPer100(double frictionF, double density, double diam)
    {
        try
        {
            return ((0.001294 * 100.0 * frictionF * density * Math.pow(velocity, 2.0) ) / diam);
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }


    public double calcPipePDrop(double pipePDropPer100)
    {
        double pipeLen = ((InputView) getView().findViewById(R.id.pipe_length)).getValueIn("ft");
        return (pipeLen * pipePDropPer100 / 100.0);
    }

    public double calcFittingKValue(double k_1, double k_inf, double k_d)
    {
        double diam = getDoubleValue(R.id.actualDiamEditText);
        try
        {
            return (k_1 / reynolds + (k_inf * (1.0 + (k_d / Math.pow(diam, 0.3)))));
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }

    public double calcFittingPDrop(double kval, int qty)
    {
        double specGrav = ((InputView) getView().findViewById(R.id.specific_gravity)).getValue();
        double GRAV_ACCEL_CONST = 32.174; // ft/s^2

        try{
            return qty * kval * (Math.pow(velocity, 2.0) * specGrav) / (2 * 2.32 * GRAV_ACCEL_CONST);
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }

    public double calcFittingPDrop(TableRow row)
    {
        //row childAt index reference:
        // 0 = row index
        // 1 = name
        // 2 = quantity
        // 3 = k value - REMOVED
        // 3 = p drop
        // 4 = remove button

        double fPdrop = 0.0;
        Boolean isCustom = (row.getChildAt(1) instanceof EditText);
        String fittingName = ((TextView)row.getChildAt(1)).getText().toString();

        TextView pDropView = (TextView)row.getChildAt(row.getChildCount() - 2);
        int qty = (int) getDoubleValue(row.getChildAt(2));

        if(isCustom)
        {
            fPdrop = qty * getDoubleValue(pDropView);
        }
        else
        {
            double k_1 = Double.valueOf(fittingsRef.get(fittingName).get("k_1"));
            double k_inf = Double.valueOf(fittingsRef.get(fittingName).get("k_inf"));
            double k_d = Double.valueOf(fittingsRef.get(fittingName).get("k_d"));
            double fKValNumber = calcFittingKValue(k_1, k_inf, k_d);
            fPdrop = calcFittingPDrop(fKValNumber, qty);
        }

        return fPdrop;
    }


    public double sumFittings(Boolean recalcPDrop)
    {
        double pDropTotal = 0.0;

        // i=0 is table headers
        // i=1 is miscellaneous
        for(int i = 2; i < fittingsTable.getChildCount(); i++)
        {
            TableRow row = (TableRow) fittingsTable.getChildAt(i);
            Boolean isCustom = (row.getChildAt(1) instanceof EditText);
            String fittingName = ((TextView)row.getChildAt(1)).getText().toString();

            double fPdrop = calcFittingPDrop(row);

            if(!isCustom && recalcPDrop)
            {
                TextView pDropView = (TextView)row.getChildAt(row.getChildCount() - 2);
                pDropView.setText(sciNotation(fPdrop));
            }

            pDropTotal += fPdrop;
        }

        return pDropTotal;
    }




    public void loadAssets() {
        actualDiamRef = loadReferenceCSV("actual_diameter_reference.csv");
        fittingsRef = loadReferenceCSV("fittings.csv");
    }


    public void initFields(View thisView)
    {

        Spinner sched = thisView.findViewById((R.id.schedSpinner));
        sched.setOnItemSelectedListener(this);

        Spinner nomSize = thisView.findViewById(R.id.nomSizeSpinner);
        nomSize.setOnItemSelectedListener(this);

        EditText actual = thisView.findViewById(R.id.actualDiamEditText);
        actual.addTextChangedListener(this);

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

        Spinner spinner = thisView.findViewById(R.id.fittingsSpinner);
        spinner.setAdapter(spinnerArrayAdapter);


    }

    public void initTable(View thisView)
    {
        fittingsTable = thisView.findViewById(R.id.fittingsTableLayout);
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

        /*
        TextView kVal = new TextView(activity);
        kVal.setText("K");
        kVal.setGravity(Gravity.CENTER);
        headerRow.addView(kVal);*/

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

        addFitting(MiscFLabel);
    }

    public void addFitting()
    {
        Spinner fittingSpinner = getView().findViewById(R.id.fittingsSpinner);
        String fittingName = fittingSpinner.getSelectedItem().toString();
        if(fittingName.equalsIgnoreCase("Fittings"))
            fittingName = "Custom";
        addFitting(fittingName);
        //performCalculation();
    }

    public void addFitting(String fittingName)
    {
        Boolean isMisc = fittingName.equalsIgnoreCase(MiscFLabel);
        Boolean isCustom = !isMisc && !fittingsRef.containsKey(fittingName);

        FragmentActivity activity = getActivity();
        int rowCount = fittingsTable.getChildCount();

        TableRow row = new TableRow(activity);

        TextView rowNumber = new TextView(activity);
        rowNumber.setText(""+rowCount);
        rowNumber.setGravity(Gravity.CENTER);
        row.addView(rowNumber);

        if(isCustom)
        {
            EditText fName = new EditText(activity);
            fName.setText(fittingName);
            fName.setTag("name");
            fName.setMaxWidth(256);
            row.addView(fName);
        }
        else {
            TextView fName = new TextView(activity);
            fName.setText(fittingName);
            fName.setTag("name");
            fName.setMaxWidth(256);
            row.addView(fName);
        }

        TextView fQty = new TextView(activity);
        if(!isMisc)
        {
            fQty = new EditText(activity);
            fQty.addTextChangedListener(this);
        }
        fQty.setTag("qty");
        fQty.setText("1");
        fQty.setGravity(Gravity.CENTER);
        row.addView(fQty);


        if(isCustom || isMisc) {
            double fKValNumber = 0.0;
            double fPDropNumber = 0.0;

            /*
            EditText fKVal = new EditText(activity);
            fKVal.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            fKVal.setText(String.valueOf(fKValNumber));
            fKVal.setTag("kval");
            fKVal.setGravity(Gravity.CENTER);
            fKVal.setVisibility(View.INVISIBLE);
            row.addView(fKVal);*/

            EditText fPDrop = new EditText(activity);
//            android:inputType="numberDecimal"
            fPDrop.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            fPDrop.setText(String.valueOf(fPDropNumber));
            fPDrop.setTag("pdrop");
            fPDrop.setGravity(Gravity.CENTER);
            fPDrop.addTextChangedListener(this);
            row.addView(fPDrop);
        }
        else
        {
//            double fkval = calFittingKValue()
            // (new DecimalFormat("#.#####E00").format(b_val))
            double k_1 = Double.valueOf(fittingsRef.get(fittingName).get("k_1"));
            double k_inf = Double.valueOf(fittingsRef.get(fittingName).get("k_inf"));
            double k_d = Double.valueOf(fittingsRef.get(fittingName).get("k_d"));

            double fKValNumber = calcFittingKValue(k_1, k_inf, k_d);
            double fPDropNumber = calcFittingPDrop(fKValNumber, 1);

            /*
            TextView fKVal = new TextView(activity);
            fKVal.setText(sciNotation(fKValNumber));
            fKVal.setTag("kval");
            fKVal.setGravity(Gravity.CENTER);
            row.addView(fKVal);*/

            TextView fPDrop = new TextView(activity);
            fPDrop.setText(sciNotation(fPDropNumber));
            fPDrop.setTag("pdrop");
            fPDrop.setGravity(Gravity.CENTER);
//            fPDrop.addTextChangedListener(this); // user can't change this value directly
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
        if(isMisc) {
            removeBtn.setVisibility(View.INVISIBLE);
        }
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
        performCalculation();
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
