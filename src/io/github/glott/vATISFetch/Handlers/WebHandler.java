package io.github.glott.vATISFetch.Handlers;

import io.github.glott.vATISFetch.Main;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebHandler
{

    static
    {
        disableSslVerification();
    }

    private List<String> exceptions = new ArrayList<>();
    private String externalURL = "";
    private String internalURL = "";
    private String id = "";

    private static String hexToString(String hexStr)
    {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < hexStr.length(); i += 2)
        {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    private static void disableSslVerification()
    {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
            {
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType)
                {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType)
                {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e)
        {
            e.printStackTrace();
        }
    }

    public void init()
    {
        try
        {
            URL url = new URL(Main.URL_BASE + "vaf/Exceptions.data");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String input;
            if ((input = br.readLine()) != null)
                exceptions = Arrays.asList(input.split(", "));
            br.close();
        } catch (Exception ignored)
        {
        }

        try
        {
            URL url = new URL(Main.URL_BASE + "vaf/URL.data");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String input;
            if ((input = br.readLine()) != null)
                externalURL = hexToString(input);
            br.close();
        } catch (Exception ignored)
        {
        }

        try
        {
            URL url = new URL(Main.URL_BASE + "vaf/ID.data");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String input;
            if ((input = br.readLine()) != null)
                id = hexToString(input);
            br.close();
        } catch (Exception ignored)
        {
        }
        internalURL = "https://datis.clowd.io/%ARPT%#";

    }

    public String getURL(String airport, boolean configLogic)
    {
        if (externalURL.length() > 0 && id.length() > 0 && exceptions.size() > 0 && exceptions.contains(airport) && configLogic)
            return externalURL.replace("%ID%", id).replace("%ARPT%", airport);
        return internalURL.replace("%ARPT%", airport.toLowerCase());
    }
}
