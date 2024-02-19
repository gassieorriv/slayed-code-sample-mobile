package web;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Map;

import Base.BaseEngage;

public class HttpClient extends AsyncTask<String, String, String> {
    protected String endpoint;
    protected OnWebRequestComplete listener;
    private String response;
    private String type;
    private Map<String, Object> params;
    private String json = "";

    public HttpClient(OnWebRequestComplete listener) {
        this.listener = listener;
    }

    private models.Authorization Authorization;

    public static String WebRequestUrl = "https://slayed-life-api-dev-apim.azure-api.net/";

    public void execute(String endpoint, String type, Map<String, Object> params, models.Authorization Authorization) {
        this.endpoint = endpoint;
        this.params = params;
        this.type = type;
        this.Authorization = Authorization;
        execute();
    }

    public void execute(String endpoint, String type, String json, models.Authorization Authorization) {
        this.endpoint = endpoint;
        this.json = json;
        this.type = type;
        this.Authorization = Authorization;
        execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... parameters) {
        StringBuilder parameterList = new StringBuilder();
        if (params != null) {
            for (String key : params.keySet()) {
                try {
                    parameterList.append(key)
                            .append("=")
                            .append(URLEncoder.encode(String.valueOf(params.get(key)), "UTF-8"))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            switch (type.toUpperCase()) {
                case "POST":
                    POST(endpoint);
                    break;
                case "POSTARRAY":
                    POSTARRAY(endpoint);
                    break;
                case "GET":
                    GET(endpoint, parameterList.toString());
                    break;
                case "POSTGOOGLE":
                    POSTGOOGLE();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void POST(String endpoint) throws IOException {
        if (endpoint != null) {
            URL url = new URL(endpoint);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                JSONObject jsonParam = new JSONObject();
                if(params != null) {
                    for (String key : params.keySet()) {
                        jsonParam.put(key, params.get(key));
                    }
                }

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("X-ZUMO-AUTH", Authorization.apiToken);
                urlConnection.setRequestProperty("ENG-AUTH-TYPE", "" + Authorization.authType);
                urlConnection.setRequestProperty("ENG-UID", Authorization.uid);
                urlConnection.setRequestProperty("ENG-TOKEN", Authorization.accessToken);
                urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key", "9675f83599d04252a3819787e2a8338d");
                DataOutputStream output = new DataOutputStream(urlConnection.getOutputStream());
                if(!json.equals("")) {
                    output.writeBytes(json);
                } else {
                    output.writeBytes(jsonParam.toString());
                }
                output.flush();
                urlConnection.connect();
                String inputLine;
                StringBuilder httpResponse = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((inputLine = bufferedReader.readLine()) != null) {
                    httpResponse.append(inputLine);
                }
                bufferedReader.close();
                response = httpResponse.toString();
            } catch (Exception e) {
                String message;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                while ((message = bufferedReader.readLine()) != null) {
                    errorResponse.append(message);
                }

                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        }
    }

    private void POSTGOOGLE() {
        if (endpoint != null) {
            try {
                GoogleTokenResponse tokenResponse =
                    new GoogleAuthorizationCodeTokenRequest(
                            new NetHttpTransport(),
                            JacksonFactory.getDefaultInstance(),
                            "https://oauth2.googleapis.com/token",
                            BaseEngage.googleAuth.clientId,
                            BaseEngage.googleAuth.clientSecret,
                            BaseEngage.googleAuth.authCode, "").execute();
                Gson gson = new Gson();
                response = gson.toJson(tokenResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void POSTARRAY(String endpoint) throws IOException {
        if (endpoint != null) {
            URL url = new URL(endpoint);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {

                JSONArray jsonParam = new JSONArray();
                for (String key : params.keySet())
                {
                    jsonParam = new JSONArray(String.valueOf(params.get(key)));
                }

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("X-ZUMO-AUTH", Authorization.apiToken);
                urlConnection.setRequestProperty("ENG-AUTH-TYPE", "" + Authorization.authType);
                urlConnection.setRequestProperty("ENG-UID", Authorization.uid);
                urlConnection.setRequestProperty("ENG-TOKEN", Authorization.accessToken);
                urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key", "9675f83599d04252a3819787e2a8338d");
                DataOutputStream ouptput = new DataOutputStream(urlConnection.getOutputStream());
                ouptput.writeBytes(jsonParam.toString());
                ouptput.flush();
                urlConnection.connect();
                String inputLine;
                StringBuilder httpResponse = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((inputLine = bufferedReader.readLine()) != null) {
                    httpResponse.append(inputLine);
                }
                bufferedReader.close();
                response = httpResponse.toString();
            } catch (Exception e) {
                String message;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                while ((message = bufferedReader.readLine()) != null) {
                    errorResponse.append(message);
                }

                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        }
    }

    private void GET(String endpoint, String parameters) throws IOException {
        if (endpoint != null) {
            URL url = new URL(endpoint + "?" + parameters);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("GET");
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("X-ZUMO-AUTH", Authorization.apiToken);
                urlConnection.setRequestProperty("ENG-AUTH-TYPE", "" + Authorization.authType);
                urlConnection.setRequestProperty("ENG-UID", Authorization.uid);
                urlConnection.setRequestProperty("ENG-TOKEN", Authorization.accessToken);
                urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key", "9675f83599d04252a3819787e2a8338d");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder httpResponse = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    httpResponse.append(inputLine);
                }
                in.close();
                response = httpResponse.toString();
            }
            catch(Exception ex) {
                String message;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                while ((message = bufferedReader.readLine()) != null)
                {
                    errorResponse.append(message);
                }

                ex.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }
        }
      }

    protected void onPostExecute(String result){
        if( listener != null && result != null) {
            try {
                listener.onWebRequestComplete(result);
            } catch (JSONException | InterruptedException | ParseException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                if (listener != null) {
                    listener.onWebRequestComplete(-1);
                }
            } catch (JSONException | ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}