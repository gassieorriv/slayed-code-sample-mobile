package web.interfaces;
import com.google.gson.Gson;
import java.util.HashMap;
import Base.BaseEngage;
import models.shop.Product;
import models.shop.Service;
import web.Endpoints;
import web.HttpClient;
import web.OnWebRequestComplete;

public class ShopInterface extends HttpClient{
    public ShopInterface(OnWebRequestComplete listener) {
        super(listener);
    }

    public void CreateProduct(Product product) {
        execute(HttpClient.WebRequestUrl + Endpoints.ShopProduct ,"POST", getProductParameters(product), BaseEngage.getAuthroizedUser());
    }

    public void UpdateProduct(Product product) {
        execute(HttpClient.WebRequestUrl + Endpoints.ShopProductUpdate ,"POST", getProductParameters(product), BaseEngage.getAuthroizedUser());
    }

    public void GetProducts(int skip, int take) {
        execute(HttpClient.WebRequestUrl + Endpoints.ShopProduct + "/" + BaseEngage.user.id + "/" + skip + "/" + take ,"GET",new HashMap<>(), BaseEngage.getAuthroizedUser());
    }

    public void CreateService(Service service) {
        execute(HttpClient.WebRequestUrl + Endpoints.ShopService ,"POST", getServiceParameters(service), BaseEngage.getAuthroizedUser());
    }

    public void UpdateService(Service service) {
        execute(HttpClient.WebRequestUrl + Endpoints.ShopServiceUpdate ,"POST", getServiceParameters(service), BaseEngage.getAuthroizedUser());
    }

    public void GetServices(int skip, int take) {
        execute(HttpClient.WebRequestUrl + Endpoints.ShopService + "/" + BaseEngage.user.id + "/" + skip + "/" + take ,"GET",new HashMap<>(), BaseEngage.getAuthroizedUser());
    }


    public void GetStateTaxes() {
        execute(HttpClient.WebRequestUrl + Endpoints.GetStateSalesTaxes ,"GET",new HashMap<>(), BaseEngage.getAuthroizedUser());
    }
    private String getProductParameters(Product product) {
        Gson gson = new Gson();
        return gson.toJson(product);
    }

    private String getServiceParameters(Service service) {
        Gson gson = new Gson();
        return gson.toJson(service);
    }
}

