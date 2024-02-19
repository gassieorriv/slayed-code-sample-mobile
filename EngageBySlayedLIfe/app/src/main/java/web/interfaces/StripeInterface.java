package web.interfaces;

import java.util.HashMap;
import java.util.Map;
import Base.BaseEngage;
import models.support.SupportNote;
import web.Endpoints;
import web.HttpClient;
import web.OnWebRequestComplete;

public class StripeInterface extends HttpClient {

    public StripeInterface(OnWebRequestComplete listener) {
        super(listener);
    }

    public void GetConnectAccountLink() {
        execute(HttpClient.WebRequestUrl + Endpoints.GetConnectAccountLink + "/" + BaseEngage.user.id,"POST", new HashMap<>(), BaseEngage.getAuthroizedUser());
    }

    public void GetAccountStatus() {
        execute(HttpClient.WebRequestUrl + Endpoints.GetAccountStatus + "/" + BaseEngage.user.id,"GET", new HashMap<>(), BaseEngage.getAuthroizedUser());
    }
}
