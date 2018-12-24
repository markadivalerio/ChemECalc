package com.example.mark.chemecalc;

import android.app.Activity;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Dictionary {

    protected HashMap<String, HashMap<String, String>> dict;

    public Dictionary()
    {
        dict = new HashMap<String, HashMap<String, String>>();
    }

    public void loadFromCSV(Activity act, String csvFilename, boolean columnHeader, boolean rowHeader)
    {
        dict = new HashMap<String, HashMap<String, String>>();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(act.getAssets().open(csvFilename)));
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
                dict.put(row[0], indexedRow);
            }
        }
        catch(IOException e) {
            //woopsy
            e.printStackTrace();
        }
    }

    public ArrayList<String> getRowHeaders()
    {
        ArrayList<String> rowHeaders = new ArrayList<String>(dict.keySet());
        return rowHeaders;
    }

    public ArrayList<String> getColumnHeaders()
    {
        ArrayList<String> rowHeaders = getRowHeaders();
        if(rowHeaders.isEmpty())
            return new ArrayList<String>();

        String firstRowHeader = rowHeaders.get(0);
        ArrayList<String> colHeaders = new ArrayList<String>(dict.get(firstRowHeader).keySet());
        return colHeaders;
    }

    public String get(Object row, Object col)
    {
        return getValue(String.valueOf(row), String.valueOf(col));
    }

    public String getValue(String row, String col)
    {
        if(!dict.containsKey(row) || dict.get(row) == null || !dict.get(row).containsKey(col))
        {
            String message = "[" + row + "," + col + "] not within bounds of dictionary";
            throw new IndexOutOfBoundsException(message);
        }
        return dict.get(row).get(col);
    }
}
