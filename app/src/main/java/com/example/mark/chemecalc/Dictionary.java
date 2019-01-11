package com.example.mark.chemecalc;

import android.app.Activity;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Dictionary {

    protected HashMap<String, HashMap<String, String>> dict;

    public Dictionary(Activity act, String csvFilename)
    {
        dict = new HashMap<String, HashMap<String, String>>();
        dict = loadCsv(act, csvFilename, true, true);
    }

    public Dictionary(Activity act, String csvFilename, boolean hasColumnHeader, boolean hasRowHeader)
    {
        dict = new HashMap<String, HashMap<String, String>>();
        dict = loadCsv(act, csvFilename, hasColumnHeader, hasRowHeader);
    }

    public HashMap<String, HashMap<String, String>> loadCsv(Activity act, String csvFilename, boolean hasColumnHeader, boolean hasRowHeader)
    {
        HashMap<String, HashMap<String, String>> dictionaryMap = new HashMap<String, HashMap<String, String>>();
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
                dictionaryMap.put(row[0], indexedRow);
            }
        }
        catch(IOException e) {
            //woopsy
            e.printStackTrace();
        }
        return dictionaryMap;
    }

    public ArrayList<String> getRowHeaders()
    {
        ArrayList<String> rowHeaders = new ArrayList<String>(dict.keySet());
        return rowHeaders;
    }

    public HashMap<String, String> getRow(String rowHeader)
    {
        if(dict.containsKey(rowHeader))
        {
            return dict.get(rowHeader);
        }
        return new HashMap<String, String>();
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

    public HashMap<String, String> getColumn(String columnHeader)
    {
        return new HashMap<String, String>();
    }

    public String get(Object rowId, Object colId)
    {
        String row = String.valueOf(rowId);
        String col = String.valueOf(colId);
        if(!dict.containsKey(row) || dict.get(row) == null || !dict.get(row).containsKey(col))
        {
            String message = "[" + row + "," + col + "] not within bounds of dictionary";
            throw new IndexOutOfBoundsException(message);
        }
        return dict.get(row).get(col);
    }
}
