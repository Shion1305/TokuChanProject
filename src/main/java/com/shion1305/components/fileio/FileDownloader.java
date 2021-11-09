package com.shion1305.components.fileio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileDownloader {
    URL source;
    public FileDownloader(String url) throws MalformedURLException {
        source=new URL(url);
    }
    public FileDownloader(URL url){
        source=url;
    }
    public byte[] doTask() throws IOException {
        HttpURLConnection connection=((HttpURLConnection) source.openConnection());
        connection.setRequestMethod("GET");
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setRequestProperty("Accept-Language", "en");
        connection.connect();
        InputStream stream=connection.getInputStream();
        byte[] b = new byte[4096];
        int readByte = 0;
        ByteArrayOutputStream stream1=new ByteArrayOutputStream();
        while(-1 != (readByte = stream.read(b))){
            stream1.write(b, 0, readByte);
        }
        return stream1.toByteArray();
    }



}
