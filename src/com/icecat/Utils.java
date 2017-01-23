package com.icecat;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sowji on 21/01/2017.
 * Utility methods or commonly used methods!
 */
public class Utils {

    public static List<String> getImageUrls(Elements images){
        if(images == null || images.isEmpty()){
            return null;
        }
        List<String> imageUrls = new ArrayList<>();
        for(Element image : images) {
            imageUrls.add( image.attr("src") );
        }

        return imageUrls;
    }

    public static List<List<Specification>> getTableData(Element element) {
        List<List<Specification>> tableData = new ArrayList<>();

        List<String> colNames = new ArrayList<>();
        Elements rows = element.getElementsByTag("tr");
        Element header = rows.get(0);
        Elements columns = header.getElementsByTag("th");
        for(Element th : columns) {
            String name = th.text();
            colNames.add(name);
        }

        for(int i= 1; i< rows.size(); i++ ){
            List<Specification> specList = new ArrayList<>();
            columns = rows.get(i).getElementsByTag("td");
            int j = 0;
            for(Element td : columns) {
                Specification specification = new Specification();
                if(colNames.get(j) == null || colNames.get(j).isEmpty()){
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
        return string == null ? "" : string.replace("\"", "");
    }

    public static void writeFile(ProductSpecs product, String filePath) {
        if(product == null) {
            System.out.println("Nulll product passed");
            return;
        }
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter( filePath + File.separator + product.getSourceId() + ".csv"));
            bw.write("\"key\",\"value\"");
            bw.newLine();
            String imagesList = "";
            if(product.getImagesList() != null) {
                for (String imageUrl : product.getImagesList()) {
                    imagesList += "\"" + imageUrl + "\",";
                }
            }
            bw.write("\"images\", " +  imagesList);
            bw.newLine();
            bw.write("\"name\",\"" + formatForCSV(product.getName())  + "\"");
            bw.newLine();
            bw.write("\"model\",\"" + formatForCSV(product.getModel())  + "\"");
            bw.newLine();
            bw.write("\"brand\",\"" + formatForCSV(product.getBrand())  + "\"");
            bw.newLine();
            bw.write("\"description\",\"" + formatForCSV(product.getDescription())  + "\"");
            bw.newLine();
            bw.write("\"sku\",\"" + formatForCSV(product.getSourceId())  + "\"");
            bw.newLine();
            bw.write("\"price\",\"" + formatForCSV(product.getPrice())  + "\"");
            bw.newLine();

            if(product.getSpecifications() != null) {
                for (Specification spec : product.getSpecifications()) {
                    bw.write("\"" + formatForCSV(spec.getName()) + "\", \"" + formatForCSV(spec.getValues()) + "\"");
                    bw.newLine();
                }
            }
            bw.flush();
            bw.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void writeFile(BrandSpecs brand, String filePath) {
        if(brand == null) {
            System.out.println("Nulll brand passed");
            return;
        }
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter( filePath + File.separator + "Brand Specs" + File.separator + brand.getName() + ".csv"));
            bw.write("\"key\",\"value\"");
            bw.newLine();
            String imagesList = "";
            if(brand.getImagesList() != null) {
                for (String imageUrl : brand.getImagesList()) {
                    imagesList += "\"" + imageUrl + "\",";
                }
            }
            bw.write("\"images\", " +  imagesList);
            bw.newLine();
            bw.write("\"name\",\"" + formatForCSV(brand.getName())  + "\"");
            bw.newLine();
            bw.write("\"description\",\"" + formatForCSV(brand.getDescription())  + "\"");
            bw.newLine();
            bw.close();

            Map<String,List<Specification>> generalSpecs = brand.getGeneralSpecs();
            if(generalSpecs != null ) {
                for(Map.Entry<String, List<Specification>> entry : generalSpecs.entrySet() ){
                    bw = new BufferedWriter(new FileWriter( filePath + File.separator + "Brand Specs" + File.separator + brand.getName() + "_ " + entry.getKey() + ".csv" ));
                    if(entry.getValue() != null) {
                        for (Specification spec : entry.getValue()) {
                            bw.write("\"" + formatForCSV(spec.getName()) + "\", \"" + formatForCSV(spec.getValues()) + "\"");
                            bw.newLine();
                        }
                    }
                    bw.flush();
                    bw.close();
                }
            }

            generalSpecs = brand.getModelSpecs();
            if(generalSpecs != null ) {
                for(Map.Entry<String, List<Specification>> entry : generalSpecs.entrySet() ){
                    bw = new BufferedWriter(new FileWriter( filePath + File.separator + "Brand Specs" + File.separator + brand.getName() + "_ " + entry.getKey() + ".csv" ));
                    if(entry.getValue() != null) {
                        for (Specification spec : entry.getValue()) {
                            bw.write("\"" + formatForCSV(spec.getName()) + "\", \"" + formatForCSV(spec.getValues()) + "\"");
                            bw.newLine();
                        }
                    }
                    bw.flush();
                    bw.close();
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
