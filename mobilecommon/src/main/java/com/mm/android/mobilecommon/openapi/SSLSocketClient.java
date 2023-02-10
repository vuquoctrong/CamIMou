package com.mm.android.mobilecommon.openapi;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

class SSLSocketClient {

    static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static TrustManager[] getTrustManager() throws CertificateException {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
    }

    static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
                boolean flag = false;
                try {
                    X509Certificate[] certs = (X509Certificate[]) sslSession.getPeerCertificates();
                    for (X509Certificate cert : certs) {
                        String cname = cert.getSubjectDN().getName().split(",")[0];
                        if (hostname.endsWith(cname.substring(4))) {
                            flag = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return flag;
            }
        };
    }
}

