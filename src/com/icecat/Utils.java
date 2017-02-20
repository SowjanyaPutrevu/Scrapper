package com.icecat;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sowji on 21/01/2017.
 * Utility methods or commonly used methods!
 */
public class Utils {

    public static List<String> getImageUrls(Elements images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        List<String> imageUrls = new ArrayList<>();
        for (Element image : images) {
            String imageUrl = image.attr("src");
            if(imageUrl.contains("sw=100")){
               String replacedUrl =  imageUrl.replace("sw=100","sw=500");
                imageUrls.add(replacedUrl);
            }else{
                imageUrls.add(imageUrl);
            }
        }

        return imageUrls;
    }

    public static List<List<Specification>> getTableData(Element element) {
        List<List<Specification>> tableData = new ArrayList<>();

        List<String> colNames = new ArrayList<>();
        Elements rows = element.getElementsByTag("tr");
        Element header = rows.get(0);
        Elements columns = header.getElementsByTag("th");
        for (Element th : columns) {
            String name = th.text();
            colNames.add(name);
        }

        for (int i = 1; i < rows.size(); i++) {
            List<Specification> specList = new ArrayList<>();
            columns = rows.get(i).getElementsByTag("td");
            int j = 0;
            for (Element td : columns) {
                Specification specification = new Specification();
                if (j >= colNames.size() || colNames.get(j) == null || colNames.get(j).isEmpty()) {
                    j++;
                    continue;
                } else {
                    specification.setName(colNames.get(j));
                    String values = td.text();
                    specification.setValues(values);
                }
                specList.add(specification);
                j++;
            }
            tableData.add(specList);
        }
        return tableData;
    }

    public static String formatForCSV(String string) {
        return string == null ? "\"\"" : string.replace("\"", "");
    }
}
