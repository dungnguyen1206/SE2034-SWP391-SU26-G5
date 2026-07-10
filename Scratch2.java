import java.net.*;
import java.io.*;
import java.util.Base64;

public class Scratch {
    public static void main(String[] args) throws Exception {
        String token = "ryXmxfzmRj1fTQ4QWIWZnIMaqAVikYzr:x";
        String encoded = Base64.getEncoder().encodeToString(token.getBytes());
        URL url = new URL("https://api.speedsms.vn/index.php/sms/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Basic " + encoded);
        conn.setDoOutput(true);
        String[] types = {"type: 2, sender empty", "type: 4, sender Verify"};
        String[] payloads = {
            "{\"to\":\"0352365217\", \"content\":\"Test\", \"type\":2}",
            "{\"to\":\"0352365217\", \"content\":\"Test\", \"type\":4, \"sender\":\"Verify\"}"
        };
        for (int i=0; i<payloads.length; i++) {
            System.out.println("Testing: " + types[i]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + encoded);
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payloads[i].getBytes());
            }
            int code = conn.getResponseCode();
            InputStream is = code < 400 ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("----------");
        }
    }
}
