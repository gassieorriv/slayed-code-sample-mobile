package models.users;

import models.shop.Product;
import models.shop.Service;
import models.social.ConnectedSocialAccount;

public class User {
    public int id;
    public String userName;
    public String firstName;
    public String lastName;
    public String dob;
    public String phone;
    public String email;
    public ConnectedSocialAccount[] connectedSocialAccounts;
    public int totalFollowers;
    public int totalFollowing;
    public int level;
    public UserPaymentAccount userPaymentAccount;
    public Product[] userProducts;
    public Service[] userServices;
    public Schedule[] userSchedule;
    public String connectedInstagram;
    public String connectedTwitter;
}
