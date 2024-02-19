package web;

public class Endpoints {
    public static String FBAPIAuthorization = ".auth/login/facebook";
    public static String GoogleAPIAuthorization = ".auth/login/google";
    public static String AuthorizeUser = "dev/user/authorize";
    public static String UpdateTwitterInfo = "dev/social/twitter";
    public static String InstagramSocialConnect = "dev/social/instagram/connect";
    public static String GetFacebookPost = "dev//social/facebook/posts";
    public static String UpdateConnectedAccountStatus = "dev/social/update/connected/accounts";
    public static String getUser = "dev/user";
    public static String CreateAndVerifyFacebookProfile = "dev/user/fb";
    public static String CreateAndVerifyGoogleProfile = "dev/user/google";
    public static String UpdateUser = "dev/user/update";
    public static String GetUserPreferences = "dev/user/preferences";
    public static String CreateUserSupportNote = "dev/support/user/note";
    public static String GetConnectAccountLink = "dev/stripe/connect";
    public static String GetAccountStatus = "dev/stripe/status";
    public static String ShopProduct = "dev/shop/product";
    public static String ShopProductUpdate = "dev/shop/product/update";
    public static String ShopService = "dev/shop/service";
    public static String ShopServiceUpdate = "dev/shop/service/update";
    public static String GetStateSalesTaxes = "dev/shop/state/taxes";
    public static String UserSchedule = "dev/user/schedule";
    public static String UpdateUserSchedule = "dev/user/schedule/update";
}
