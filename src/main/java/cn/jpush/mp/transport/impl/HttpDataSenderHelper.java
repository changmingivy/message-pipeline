package cn.jpush.mp.transport.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpDataSenderHelper {
    private static Logger logger = LoggerFactory.getLogger(HttpDataSenderHelper.class);

    private static class SimpleTrustManager implements TrustManager, X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            return;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            return;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static class SimpleHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;

        }
    }

    protected static void initSSL() {
        try {
            TrustManager[] tmCerts = new TrustManager[1];
            tmCerts[0] = new SimpleTrustManager();
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, tmCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier hv = new SimpleHostnameVerifier();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e) {
            logger.error("init SSL exception.", e);
        }
    }


    public static String postByteArray(String url, byte [] data) {
        logger.debug("Send request to - " + url + ", with data length - " + data.length);
        HttpURLConnection conn = null;
        OutputStream out = null;
        StringBuffer sb = new StringBuffer();
        String responseContent = null;

        try {
            if (url.toLowerCase().trim().startsWith("https")) {
                initSSL();
            }

            URL aUrl = new URL(url);

            conn = (HttpURLConnection) aUrl.openConnection();
            conn.setConnectTimeout(60*1000);
            conn.setReadTimeout(60*1000);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/octet-stream");

            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            out = conn.getOutputStream();
            out.write(data);
            out.flush();
            int status = conn.getResponseCode();
            InputStream in = null;
            if (status == 200) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            InputStreamReader reader = new InputStreamReader(in, "UTF-8");
            char[] buff = new char[1024];
            int len;
            while ((len = reader.read(buff)) > 0) {
                sb.append(buff, 0, len);
            }

            responseContent = sb.toString();

            if (status == 200) {
                logger.debug("Succeed to get response - 200 OK");
                logger.debug("Response Content - " + responseContent);

            } else {
                logger.warn("Got error response - responseCode:" + status + ", responseContent:"
                        + responseContent);
            }

        } catch (SocketTimeoutException e) {
            logger.error("Post data to " + url, e);
        } catch (IOException e) {
            logger.error("Post data to " + url, e);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("Failed to close stream.", e);
                }
            }
            if (null != conn) {
                conn.disconnect();
            }
        }
        logger.debug(String.format("Send Response to - %s, Response Content - %s", url, responseContent));
        return responseContent;
    }
}
