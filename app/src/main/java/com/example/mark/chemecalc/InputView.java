package com.example.mark.chemecalc;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import org.w3c.dom.Text;

import javax.measure.Measure;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;
import java.util.ArrayList;
import java.util.Arrays;

public class InputView extends ConstraintLayout{

    public OnCustomEventListener customEventListener = null;
    public static Dictionary unitsDictionary = null;
    public TextView labelView;
    public EditText valueView;
    public TextView unitView;
    private ArrayList<String> unitsList = null;
    private String previousUnit = "";

    public InputView(Context context) {
        super(context, null);
        init(context, null);
    }

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        Log.w("test", "here2");
        init(context, attrs);
    }

    public InputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.w("test", "here3");
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.input_view, this);
        customEventListener = null;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InputView);

        //Get a reference to the layout where you want children to be placed
        labelView = (TextView)findViewById(R.id.input_label);
        valueView = (EditText)findViewById(R.id.input_value);
        unitView = (TextView)findViewById(R.id.input_units);

        try
        {
            String unitReference = ta.getString(R.styleable.InputView_iv_units_reference);;
            unitsList = loadUnits(context, unitReference);
            ta.recycle();

            Log.w("test", String.valueOf(unitsList));
            if(unitsList != null)
            {
                previousUnit = unitsList.get(0);
                setUnit(previousUnit, false);
            }
        }
        finally
        {
        }

        ta = context.obtainStyledAttributes(attrs, R.styleable.InputView);
        String initLabel = ta.getString(R.styleable.InputView_iv_label);
        setLabel(initLabel);
        ta.recycle();

        ta = context.obtainStyledAttributes(attrs, R.styleable.InputView);
        String initValue = ta.getString(R.styleable.InputView_iv_value);
        setValue(initValue);
        ta.recycle();

        valueView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                customEventListener.onChangeEvent();
            }
        });

        unitView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("test","Clicked");
                int currentIx = unitsList.indexOf(getUnit()) + 1;
                if(currentIx >= unitsList.size())
                {
                    currentIx = 0;
                }
                Log.w("test", unitsList.get(currentIx));
                setUnit(unitsList.get(currentIx));
                customEventListener.onChangeEvent();
            }
        });


    }

    public ArrayList<String> loadUnits(Context context, String unitReference)
    {
        if(unitsDictionary == null)
        {
            unitsDictionary = new Dictionary(((Activity)context), "units_reference.csv");
        }
        String unitStr = unitsDictionary.get(unitReference, "DEFAULT");
        ArrayList<String> units = new ArrayList<String>(Arrays.asList(unitStr.split(",")));

        return units;
    }

    public String getLabel()
    {
        return labelView.getText().toString();
    }

    public void setLabel(String newLabel)
    {
        if(newLabel == null)
        {
            newLabel = "";
        }
        labelView.setText(newLabel);
    }

    public double getValue()
    {
        try {
            return Double.parseDouble(valueView.getText().toString());
        }
        catch(Exception e)
        {
            return 0.0;
        }
    }

    public double getValueIn(String desiredUnitStr)
    {
        double currentValue = getValue();
        Unit currentUnit = Unit.valueOf(getUnit());
        Unit desiredUnit = Unit.valueOf(desiredUnitStr);

        UnitConverter converter = currentUnit.getConverterTo(desiredUnit);
        double convertedValue = converter.convert(Measure.valueOf(currentValue, currentUnit).doubleValue(currentUnit));
        return convertedValue;
    }

    public void setValue(Object newValue)
    {
        if(newValue == null)
        {
            newValue = "";
        }

        valueView.setText(String.valueOf(newValue));
    }

    public String getUnit()
    {
        return unitView.getText().toString();
    }

    public void setUnit(String newUnit)
    {
        setUnit(newUnit, true);
    }

    public void setUnit(String newUnit, boolean convertValue) {
        if (convertValue) {
            double currentValue = getValue();
            Unit currentUnit = Unit.valueOf(getUnit());
            Unit desiredUnit = Unit.valueOf(newUnit);

            UnitConverter converter = currentUnit.getConverterTo(desiredUnit);
            double convertedValue = converter.convert(Measure.valueOf(currentValue, currentUnit).doubleValue(currentUnit));
            valueView.setText(String.valueOf(convertedValue));
        }

        previousUnit = getUnit();
        unitView.setText(newUnit);

    }

    public void setCustomEventListener(OnCustomEventListener eventListener) {
        customEventListener = eventListener;
    }
}
