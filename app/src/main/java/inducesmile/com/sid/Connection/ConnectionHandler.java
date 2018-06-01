package inducesmile.com.sid.Connection;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class ConnectionHandler {

    public JSONArray getJSONFromUrl(String url, HashMap<String, String> params) {
        try {

            StringBuilder sb_params = new StringBuilder();
            int i = 0;
            for (String key : params.keySet()) {
                if (i != 0) {
                    sb_params.append("&");
                }
                sb_params.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
                i++;
            }

            Log.d("MSG2", url);

            URL urlObj = new URL(String.valueOf(url));
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.connect();

            String paramsString = sb_params.toString();
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(paramsString);
            wr.flush();
            wr.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d("Line", line);
                result.append(line);
            }
            conn.disconnect();


            Log.d("msg", result.toString());

            JSONArray jsonArray = new JSONArray(result.toString());

            return jsonArray;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

