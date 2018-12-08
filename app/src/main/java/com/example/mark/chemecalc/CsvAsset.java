package com.example.mark.chemecalc;

import android.app.Activity;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CsvAsset {

    private Activity activity;
    protected HashMap<String, HashMap<String, String>> dictionary;

    public CsvAsset(Activity act)
    {
        activity = act;
        dictionary = new HashMap<String, HashMap<String, String>>();
    }

    public HashMap<String, HashMap<String, String>> loadCSV(String csvFilename, boolean columnHeader, boolean rowHeader)
    {
        dictionary = new HashMap<String, HashMap<String, String>>();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(activity.getAssets().open(csvFilename)));
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

    public String get(Object row, Object col)
    {
        return getCell(String.valueOf(row), String.valueOf(col));
    }

    public String getCell(String row, String col)
    {
        if(!dictionary.containsKey(row) || dictionary.get(row) == null || !dictionary.get(row).containsKey(col))
        {
            String message = "[" + row + "," + col + "] not within bounds of dictionary";
            throw new IndexOutOfBoundsException(message);
        }
        return dictionary.get(row).get(col);
    }
}
