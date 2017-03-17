package com.icecat.cassedro;

import com.icecat.BrandSpecs;
import com.icecat.Utils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

/**
 * Created by Sowjanya on 3/13/2017.
 */
public class Test {

    public static void main(String[] args) {

        String headers = "";

        List<String> extraHeaders = new ArrayList<>();
        Set<String> extraHeaderSet = new HashSet<>();

        String filePath = "C:\\Users\\Sowjanya\\Documents\\casadellibro\\En Otros Idioms Engles Literature\\sowji.csv";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

            for(int index = 1 ; index <= 5 ;index++) {

                String day = "Day" + index;

                File folder = new File("C:\\Users\\Sowjanya\\Documents\\casadellibro\\En Otros Idioms Engles Literature\\" + day);
                System.out.println("C:\\Users\\Sowjanya\\Documents\\casadellibro\\En Otros Idioms Engles Literature\\" + day);

                File[] files = folder.listFiles();

                for (File xls : files) {

                    System.out.println(xls.getName());

                    CSVParser parser = new CSVParser(new FileReader(xls), CSVFormat.RFC4180);
                    List<CSVRecord> records  = parser.getRecords();
                    CSVRecord header = records.get(0);

                    for (String key : header) {
                        if (!extraHeaderSet.contains(key)) {
                            extraHeaders.add(key);
                            extraHeaderSet.add(key);
                        }
                    }
                    for(int mn = 1; mn < records.size(); mn++ ) {
                        Map<String, String> features = new HashMap<>();

                        int lmn = 0;
                        for(String s : records.get(mn)) {
                            features.put( header.get(lmn), s);
                            lmn++;
                        }

                        if(records.get(mn).get(0) == null || records.get(mn).get(0).isEmpty())
                            continue;

                        for (String key : extraHeaders) {
                            bw.write("\"" + Utils.formatForCSV(features.get(key)) + "\",");
                        }
                        bw.newLine();
                    }
                }
                bw.flush();
            }
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            for(String s : extraHeaders){
                headers += "\"" + s + "\",";
            }
            headers += "";

            System.out.println(headers);

            File mFile = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String result = "";
            String line = "";
            while( (line = br.readLine()) != null){
                result = result + line;
                result += "";
            }
            result = headers + result;
            FileOutputStream fos = new FileOutputStream(mFile);
            fos.write(result.getBytes());
            fos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }


    }
}
