package web.interfaces;

import Base.BaseEngage;
import web.HttpClient;
import java.util.HashMap;
import java.util.Map;
import web.Endpoints;
import web.OnWebRequestComplete;

public class AuthorizationInterface extends HttpClient {
    public AuthorizationInterface(OnWebRequestComplete listener) {
        super(listener);
    }

    public void FacebookApiAuthorization(String accessToken) {
       Map<String,Object> params = new HashMap<>();
       params.put("access_token", accessToken);
       execute(HttpClient.WebRequestUrl + Endpoints.FBAPIAuthorization ,"POST", params, BaseEngage.getAuthroizedUser());
    }

    public void GoogleApiAuthorization(String idToken) {
        Map<String,Object> params = new HashMap<>();
        params.put("id_token", idToken);
        execute(HttpClient.WebRequestUrl + Endpoints.GoogleAPIAuthorization ,"POST", params, BaseEngage.getAuthroizedUser());
    }
}
