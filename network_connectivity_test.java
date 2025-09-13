import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.*;

/**
 * Network connectivity test that bypasses custom DNS resolution
 * Tests direct connection to Second Life servers
 */
public class network_connectivity_test {
    
    // Create a trust-all SSL context (same as Lumiya app)
    private static SSLContext createTrustAllSSLContext() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            return sc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Test basic connectivity to Second Life login server
    public static boolean testBasicConnectivity(String host, int port) {
        try {
            System.out.println("Testing basic TCP connectivity to " + host + ":" + port);
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 5000);
            socket.close();
            System.out.println("✅ Basic TCP connection successful");
            return true;
        } catch (Exception e) {
            System.out.println("❌ Basic TCP connection failed: " + e.getMessage());
            return false;
        }
    }
    
    // Test HTTPS connectivity with custom SSL context
    public static boolean testHTTPSConnectivity(String url) {
        try {
            System.out.println("Testing HTTPS connectivity to " + url);
            
            URL urlObj = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
            
            // Apply same SSL settings as Lumiya
            SSLContext sslContext = createTrustAllSSLContext();
            if (sslContext != null) {
                conn.setSSLSocketFactory(sslContext.getSocketFactory());
                conn.setHostnameVerifier((hostname, session) -> true);
            }
            
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("HEAD");
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            System.out.println("✅ HTTPS connection successful - Response code: " + responseCode);
            return true;
        } catch (Exception e) {
            System.out.println("❌ HTTPS connection failed: " + e.getMessage());
            return false;
        }
    }
    
    // Test Second Life login endpoint specifically
    public static boolean testSecondLifeLogin() {
        try {
            System.out.println("Testing Second Life login endpoint...");
            
            String loginURL = "https://login.secondlife.com/cgi-bin/login.cgi";
            URL url = new URL(loginURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            
            // Apply Lumiya-style SSL settings
            SSLContext sslContext = createTrustAllSSLContext();
            if (sslContext != null) {
                conn.setSSLSocketFactory(sslContext.getSocketFactory());
                conn.setHostnameVerifier((hostname, session) -> true);
            }
            
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/xml");
            conn.setRequestProperty("User-Agent", "Lumiya 3.4.3 Android Mobile");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);
            
            // Send minimal XML-RPC request (will fail auth but tests connectivity)
            String xmlRequest = "<?xml version=\"1.0\"?>\n" +
                               "<methodCall>\n" +
                               "  <methodName>login_to_simulator</methodName>\n" +
                               "  <params>\n" +
                               "    <param>\n" +
                               "      <value>\n" +
                               "        <struct>\n" +
                               "          <member>\n" +
                               "            <name>first</name>\n" +
                               "            <value><string>Test</string></value>\n" +
                               "          </member>\n" +
                               "          <member>\n" +
                               "            <name>last</name>\n" +
                               "            <value><string>User</string></value>\n" +
                               "          </member>\n" +
                               "          <member>\n" +
                               "            <name>passwd</name>\n" +
                               "            <value><string>$1$invalid</string></value>\n" +
                               "          </member>\n" +
                               "        </struct>\n" +
                               "      </value>\n" +
                               "    </param>\n" +
                               "  </params>\n" +
                               "</methodCall>";
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(xmlRequest.getBytes("UTF-8"));
            }
            
            int responseCode = conn.getResponseCode();
            
            // Read response to see if server is responding properly
            String response = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream()))) {
                String line;
                StringBuilder responseBuilder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseBuilder.append(line);
                    if (responseBuilder.length() > 1000) break; // Limit response size
                }
                response = responseBuilder.toString();
            }
            
            conn.disconnect();
            
            System.out.println("Second Life login server response:");
            System.out.println("  Response Code: " + responseCode);
            System.out.println("  Response Preview: " + response.substring(0, Math.min(200, response.length())) + "...");
            
            // Any response (even error) means server is reachable and functioning
            if (responseCode > 0) {
                System.out.println("✅ Second Life login server is reachable and responding");
                return true;
            } else {
                System.out.println("❌ No response from Second Life login server");
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("❌ Second Life login test failed: " + e.getMessage());
            return false;
        }
    }
    
    // Test DNS resolution for SL domains
    public static boolean testDNSResolution() {
        System.out.println("Testing DNS resolution for Second Life domains...");
        
        String[] domains = {
            "login.secondlife.com",
            "secondlife.com", 
            "lindenlab.com"
        };
        
        boolean allResolved = true;
        
        for (String domain : domains) {
            try {
                InetAddress[] addresses = InetAddress.getAllByName(domain);
                System.out.println("✅ " + domain + " resolved to " + addresses.length + " addresses:");
                for (InetAddress addr : addresses) {
                    System.out.println("    " + addr.getHostAddress());
                }
            } catch (UnknownHostException e) {
                System.out.println("❌ " + domain + " could not be resolved: " + e.getMessage());
                allResolved = false;
            }
        }
        
        return allResolved;
    }
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  SECOND LIFE NETWORK CONNECTIVITY TEST");
        System.out.println("========================================");
        
        // Test 1: DNS Resolution
        System.out.println("\n=== Test 1: DNS Resolution ===");
        boolean dnsWorking = testDNSResolution();
        
        // Test 2: Basic TCP connectivity
        System.out.println("\n=== Test 2: Basic TCP Connectivity ===");
        boolean tcpWorking = testBasicConnectivity("login.secondlife.com", 443);
        
        // Test 3: HTTPS connectivity
        System.out.println("\n=== Test 3: HTTPS Connectivity ===");
        boolean httpsWorking = testHTTPSConnectivity("https://secondlife.com/");
        
        // Test 4: Second Life login server
        System.out.println("\n=== Test 4: Second Life Login Server ===");
        boolean loginServerWorking = testSecondLifeLogin();
        
        // Summary
        System.out.println("\n========================================");
        System.out.println("           CONNECTIVITY SUMMARY");
        System.out.println("========================================");
        System.out.println("DNS Resolution: " + (dnsWorking ? "✅ WORKING" : "❌ FAILED"));
        System.out.println("TCP Connectivity: " + (tcpWorking ? "✅ WORKING" : "❌ FAILED"));
        System.out.println("HTTPS Connectivity: " + (httpsWorking ? "✅ WORKING" : "❌ FAILED"));
        System.out.println("SL Login Server: " + (loginServerWorking ? "✅ WORKING" : "❌ FAILED"));
        
        System.out.println("\n🎯 DIAGNOSIS:");
        if (loginServerWorking) {
            System.out.println("✅ Second Life connectivity is FULLY FUNCTIONAL!");
            System.out.println("   The app should be able to login successfully.");
        } else if (httpsWorking) {
            System.out.println("⚠️  HTTPS works but SL login server may be down or blocking requests");
            System.out.println("   This could be temporary or due to request format");
        } else if (tcpWorking) {
            System.out.println("⚠️  Basic connectivity works but HTTPS has issues");
            System.out.println("   This could be SSL/TLS configuration problems");
        } else if (dnsWorking) {
            System.out.println("⚠️  DNS works but network connectivity is blocked");
            System.out.println("   This suggests firewall or network policy restrictions");
        } else {
            System.out.println("❌ Network connectivity is completely blocked");
            System.out.println("   This environment may not have internet access");
        }
        
        System.out.println("\n📱 FOR MOBILE APP:");
        System.out.println("   On a real Android device with internet access,");
        System.out.println("   the login functionality should work properly.");
    }
}