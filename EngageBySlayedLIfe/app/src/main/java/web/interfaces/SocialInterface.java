package web.interfaces;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Base.BaseEngage;
import models.Authorization;
import models.social.ConnectedSocialAccount;
import models.social.TwitterAccess;
import web.Endpoints;
import web.HttpClient;
import web.OnWebRequestComplete;

public class SocialInterface extends HttpClient{

    private final Map<String,Object> params = new HashMap<>();
    public SocialInterface(OnWebRequestComplete listener) {
        super(listener);
    }

    public void UpdateTwitterInformation(TwitterAccess twitterAccess) {
        params.put("token", twitterAccess.token);
        params.put("secret", twitterAccess.secret);
        execute(HttpClient.WebRequestUrl + Endpoints.UpdateTwitterInfo + "/" + BaseEngage.user.id ,"POST", params, BaseEngage.getAuthroizedUser());
    }

    public void SocialConnectInstagram(String accessToken) {
        Authorization authorization = BaseEngage.getAuthroizedUser();
        authorization.accessToken = accessToken;
        execute(HttpClient.WebRequestUrl + Endpoints.InstagramSocialConnect ,"POST", params, authorization);
    }

    public void UpdateAccountStatus(List<ConnectedSocialAccount> accounts) {
        execute(HttpClient.WebRequestUrl + Endpoints.UpdateConnectedAccountStatus ,"POSTARRAY", getSocialConnectedAccountParameters(accounts), BaseEngage.getAuthroizedUser());
    }

    public void GetAccessTokenFromAuthCode() {
        execute("https://oauth2.googleapis.com/token" ,"POSTGOOGLE", new HashMap<>(), BaseEngage.getAuthroizedUser());
    }

    private Map<String, Object> getSocialConnectedAccountParameters(List<ConnectedSocialAccount> accounts) {
        Map<String, Object> params = new HashMap<>();
        Gson gson = new Gson();
        String json = gson.toJson(accounts);
        params.put("connectedAccountsDto", json);
        return params;
    }
}

