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
            imageUrls.add(image.attr("src"));
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
                if (colNames.get(j) == null || colNames.get(j).isEmpty()) {
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
        if (product == null) {
            System.out.println("Null product passed");
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath , true));
            String imagesList = "";
            if (product.getImagesList() != null) {
                for (String imageUrl : product.getImagesList()) {
                    imagesList += "\"" + imageUrl + "\",";
                }
            }
            bw.write("\"" + imagesList + "\", \"" +
                    formatForCSV(product.getName()) + "\", \"" +
                    formatForCSV(product.getModel()) + "\", \"" +
                    formatForCSV(product.getBrand()) + "\", \"" +
                    formatForCSV(product.getDescription()) + "\", \"" +
                    formatForCSV(product.getSourceId()) + "\", \"" +
                    formatForCSV(product.getPrice()) + "\",");

            if (product.getSpecifications() != null) {
                Map<String, String> map = new HashMap<>();
                for (Specification spec : product.getSpecifications()) {
                    map.put(spec.getName(), spec.getValues());
                }
                bw.write("\"" + formatForCSV(map.get("Gender")) + "\", \"" +
                        formatForCSV(map.get("Hand")) + "\", \"" +
                        formatForCSV(map.get("Loft")) + "\", \"" +
                        formatForCSV(map.get("Shaft Origin")) + "\", \"" +
                        formatForCSV(map.get("Shaft Type")) + "\", \"" +
                        formatForCSV(map.get("Shaft Manufacturer")) + "\", \"" +
                        formatForCSV(map.get("Shaft Material")) + "\", \"" +
                        formatForCSV(map.get("Shaft Flex")) + "\", \"" +
                        formatForCSV(map.get("Grip")) + "\", \"" +
                        formatForCSV(map.get("Wraps")) + "\", \"" +
                        formatForCSV(map.get("Length")) + "\", \"" +
                        formatForCSV(map.get("Lie Angle") ) + "\"");
            }

            bw.newLine();
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(BrandSpecs brand, String filePath) {
        if (brand == null) {
            System.out.println("Null brand passed");
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + File.separator + "Test" + File.separator + brand.getName() + ".csv"));
            bw.write("\"key\",\"value\"");
            bw.newLine();
            String imagesList = "";
            if (brand.getImagesList() != null) {
                for (String imageUrl : brand.getImagesList()) {
                    imagesList += "\"" + imageUrl + "\",";
                }
            }
            bw.write("\"images\", " + imagesList);
            bw.write("\"name\",\"" + formatForCSV(brand.getName()) + "\"");
            bw.write("\"description\",\"" + formatForCSV(brand.getDescription()) + "\"");
            for (Map.Entry<String, String> entry : brand.getFeatures().entrySet()) {
                bw.write("\"" + formatForCSV(entry.getKey()) + "\",\"" + formatForCSV(entry.getValue()) + "\"," +
                        "\"" + formatForCSV(brand.getFeatureImages().get(entry.getKey())) + "\"");
            }
            bw.close();

            Map<String, List<Specification>> generalSpecs = brand.getGeneralSpecs();
            if (generalSpecs != null) {
                for (Map.Entry<String, List<Specification>> entry : generalSpecs.entrySet()) {
                    bw = new BufferedWriter(new FileWriter(filePath + File.separator + "Test" + File.separator + brand.getName() + "_ " + entry.getKey() + ".csv"));
                    if (entry.getValue() != null) {
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
            if (generalSpecs != null) {
                for (Map.Entry<String, List<Specification>> entry : generalSpecs.entrySet()) {
                    bw = new BufferedWriter(new FileWriter(filePath + File.separator + "Test" + File.separator + brand.getName() + "_ " + entry.getKey() + ".csv"));
                    if (entry.getValue() != null) {
                        for (Specification spec : entry.getValue()) {
                            bw.write("RTB" + "\", \"" + formatForCSV(spec.getName()) + "\", \"" + formatForCSV(spec.getValues()) + "\"");
                        }
                    }
                    bw.flush();
                    bw.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
