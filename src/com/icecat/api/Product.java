package com.icecat.api;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

public class Product{
    public void getProduct(String productId) throws IOException {
        HttpClient httpClient = new HttpClient();
        productId = "http://www.javaworld.com/article/2071835/web-app-frameworks/";
        HttpMethod httpMethod = new PostMethod(productId);
        int i = httpClient.executeMethod(httpMethod);
        System.out.println(i);

    }

    public static void main(String[] args)throws Exception {
        Product product = new Product();
        product.getProduct("http://www.javaworld.com/article/2071835/web-app-frameworks/");
    }
}
