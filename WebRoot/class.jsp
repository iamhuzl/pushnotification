<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="javax.net.ssl.SSLContext" %>
<%@ page import="java.security.KeyStore" %>
<%@ page import="java.net.URL" %>
<%@ page import="org.androidpn.server.util.Config" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="javax.net.ssl.KeyManagerFactory" %>
<%@ page import="javax.net.ssl.TrustManagerFactory" %>
<%@ page import="org.androidpn.server.xmpp.net.Connection" %>
<%--
  Created by IntelliJ IDEA.
  User: HuZL
  Date: 14-5-30
  Time: 下午3:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
      
<%
     final Log log = LogFactory.getLog(SSLConfig.class);
    
         SSLContext sslContext;
    
         String storeType;
    
         KeyStore keyStore;
    
         String keyStoreLocation;
    
         String keyPass;
    
         KeyStore trustStore;
    
         String trustStoreLocation;
    
         String trustPass;
        
         URL classPath;

    {
            try {
            storeType = Config.getString("xmpp.ssl.storeType", "JKS");
            keyStoreLocation = Config.getString("xmpp.ssl.keystore", "conf"
                    + File.separator + "security" + File.separator + "keystore");
            keyStoreLocation = classPath.getPath() + File.separator
                    + keyStoreLocation;
            keyPass = Config.getString("xmpp.ssl.keypass", "changeit");
            trustStoreLocation = Config.getString("xmpp.ssl.truststore", "conf"
                    + File.separator + "security" + File.separator + "truststore");
            trustStoreLocation = classPath.getPath()
                    + File.separator + trustStoreLocation;
            trustPass = Config.getString("xmpp.ssl.trustpass", "changeit");

            classPath = Connection.class.getResource("/");

            log.debug("keyStoreLocation=" + keyStoreLocation);
            log.debug("trustStoreLocation=" + trustStoreLocation);

            // Load keystore

                keyStore = KeyStore.getInstance(storeType);
                keyStore.load(new FileInputStream(keyStoreLocation), keyPass
                        .toCharArray());
            } catch (Exception e) {
                log.error(
                        "SSLConfig startup problem.\n" + "  storeType: ["
                                + storeType + "]\n" + "  keyStoreLocation: ["
                                + keyStoreLocation + "]\n" + "  keyPass: ["
                                + keyPass + "]", e);
                keyStore = null;
            }

            // Load truststore
            try {
                trustStore = KeyStore.getInstance(storeType);
                trustStore.load(new FileInputStream(trustStoreLocation), trustPass
                        .toCharArray());

            } catch (Exception e) {
                try {
                    trustStore = KeyStore.getInstance(storeType);
                    trustStore.load(null, trustPass.toCharArray());
                } catch (Exception ex) {
                    log.error("SSLConfig startup problem.\n" + "  storeType: ["
                            + storeType + "]\n" + "  trustStoreLocation: ["
                            + trustStoreLocation + "]\n" + "  trustPass: ["
                            + trustPass + "]", e);
                    trustStore = null;
                }
            }

            // Init factory
            try {
                sslContext = SSLContext.getInstance("TLS");

                KeyManagerFactory keyFactory = KeyManagerFactory
                        .getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyFactory.init(keyStore, SSLConfig.getKeyPassword().toCharArray());

                TrustManagerFactory c2sTrustFactory = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                c2sTrustFactory.init(trustStore);

                sslContext.init(keyFactory.getKeyManagers(), c2sTrustFactory
                        .getTrustManagers(), new java.security.SecureRandom());

            } catch (Exception e) {
                log.error("SSLConfig factory setup problem." + "  storeType: ["
                        + storeType + "]\n" + "  keyStoreLocation: ["
                        + keyStoreLocation + "]\n" + "  keyPass: [" + keyPass
                        + "]\n" + "  trustStoreLocation: [" + trustStoreLocation
                        + "]\n" + "  trustPass: [" + trustPass + "]", e);
                keyStore = null;
                trustStore = null;
            }
        }
%>
</body>
</html>
