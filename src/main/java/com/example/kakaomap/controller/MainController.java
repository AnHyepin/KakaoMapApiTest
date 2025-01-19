package com.example.kakaomap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Controller
public class MainController {

    @RequestMapping("/test")
    public String root() {
        return "main";
    }


    @RequestMapping("/")
    public String index() {
        return "check-out";
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
//    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody Map<String, String> requestData) throws Exception {
    public String confirmPayment(@RequestBody Map<String, String> requestData) throws Exception {
        String paymentKey = requestData.get("paymentKey");
        String orderId = requestData.get("orderId");
        String amount = requestData.get("amount");

        Map<String, Object> requestBody = Map.of(
                "orderId", orderId,
                "amount", amount,
                "paymentKey", paymentKey
        );

        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        String authorizations = "Basic " + Base64.getEncoder()
                .encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        ObjectMapper objectMapper = new ObjectMapper();
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(objectMapper.writeValueAsBytes(requestBody));
        }

        int code = connection.getResponseCode();
        InputStream responseStream = (code == 200) ? connection.getInputStream() : connection.getErrorStream();

        Map<String, Object> responseBody;
        try (Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            responseBody = objectMapper.readValue(reader, Map.class);
        }

//        return ResponseEntity.status(code).body(responseBody);
        return "redirect:/success";
    }

    @RequestMapping("/success")
    public String successPage() {
        return "success";
    }

    @RequestMapping("/layout")
    public String layout() {

        return "layout";
    }
}

