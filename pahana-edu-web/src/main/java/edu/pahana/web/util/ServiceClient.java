package edu.pahana.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ServiceClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static String baseUrl;

    static {
        try {
            Properties p = new Properties();
            // Works inside WAR: load from webapp resource
            InputStream in = ServiceClient.class.getClassLoader().getResourceAsStream("WEB-INF/config.properties");
            if (in == null) {
                // Fallback for some servlet containers
                in = ServiceClient.class.getResourceAsStream("/WEB-INF/config.properties");
            }
            if (in != null) {
                p.load(in);
                in.close();
            }
            baseUrl = p.getProperty("service.baseUrl", "http://localhost:8080/pahana-edu-service/api").replaceAll("/+$", "");
        } catch (Exception e) {
            baseUrl = "http://localhost:8080/pahana-edu-service/api";
        }
    }

    public static <T> T postJson(String path, Object body, String token, Class<T> clazz) throws Exception {
        HttpURLConnection con = open("POST", path, token);
        String json = MAPPER.writeValueAsString(body);
        try(OutputStream os = con.getOutputStream()){ os.write(json.getBytes(StandardCharsets.UTF_8)); }
        return readResponse(con, clazz);
    }

    // NEW: PUT
    public static <T> T putJson(String path, Object body, String token, Class<T> clazz) throws Exception {
        HttpURLConnection con = open("PUT", path, token);
        String json = MAPPER.writeValueAsString(body);
        try(OutputStream os = con.getOutputStream()){ os.write(json.getBytes(StandardCharsets.UTF_8)); }
        return readResponse(con, clazz);
    }

    public static <T> T getJson(String path, String token, Class<T> clazz) throws Exception {
        HttpURLConnection con = open("GET", path, token);
        return readResponse(con, clazz);
    }

    // NEW: DELETE
    public static <T> T delete(String path, String token, Class<T> clazz) throws Exception {
        HttpURLConnection con = open("DELETE", path, token);
        return readResponse(con, clazz);
    }

    private static HttpURLConnection open(String method, String path, String token) throws Exception {
        URL u = new URL(baseUrl + path);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type","application/json");
        con.setRequestProperty("Accept","application/json");
        if(token != null && !token.isEmpty()){
            con.setRequestProperty("Authorization","Bearer "+token);
        }
        if("POST".equals(method) || "PUT".equals(method)){
            con.setDoOutput(true);
        }
        return con;
    }

    private static <T> T readResponse(HttpURLConnection con, Class<T> clazz) throws Exception {
        int code = con.getResponseCode();
        InputStream is = code >= 200 && code < 300 ? con.getInputStream() : con.getErrorStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (is != null) is.transferTo(bos);
        String s = bos.toString(StandardCharsets.UTF_8);
        if(code >= 200 && code < 300){
            if(clazz == String.class) return clazz.cast(s);
            if(clazz == Void.class) return null;
            return new ObjectMapper().readValue(s, clazz);
        } else {
            throw new RuntimeException("HTTP "+code+": "+s);
        }
    }
}
