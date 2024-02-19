package web.interfaces;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Base.BaseEngage;
import models.Authorization;
import models.preferences.CurrentPreferences;
import models.users.GoogleUser;
import models.users.Schedule;
import web.Endpoints;
import web.HttpClient;
import web.OnWebRequestComplete;

public class UserInterface extends HttpClient {

    public UserInterface(OnWebRequestComplete listener) {
        super(listener);
    }

    public void CreateAndVerifyFacebookProfile() {
        Map<String,Object> params = new HashMap<>();
        execute(HttpClient.WebRequestUrl + Endpoints.CreateAndVerifyFacebookProfile ,"POST", params, BaseEngage.getAuthroizedUser());
    }

    public void CreateAndVerifyGoogleProfile(GoogleUser googleUser) {
        execute(HttpClient.WebRequestUrl + Endpoints.CreateAndVerifyGoogleProfile ,"POST", getGoogleUserParams(googleUser), BaseEngage.getAuthroizedUser());
    }

    public void UpdateUser() {
        execute(HttpClient.WebRequestUrl + Endpoints.UpdateUser ,"POST", getUserParams(), BaseEngage.getAuthroizedUser());
    }

    public void AuthorizeUser() {
        execute(HttpClient.WebRequestUrl + Endpoints.AuthorizeUser ,"POST", new HashMap<>(), BaseEngage.getAuthroizedUser());
    }

    public void GetUserById() {
        Authorization auth = BaseEngage.getAuthroizedUser();
        execute(HttpClient.WebRequestUrl + Endpoints.getUser + "/" + auth.engageUserId ,"GET", new HashMap<>(), auth);
    }

    public void GetUserPreferences() {
        execute(HttpClient.WebRequestUrl + Endpoints.GetUserPreferences + "/" + BaseEngage.user.id ,"GET", new HashMap<>(), BaseEngage.getAuthroizedUser());
    }

    public void UpdateUserPreferences(List<CurrentPreferences> preferences) {
        execute(HttpClient.WebRequestUrl + Endpoints.GetUserPreferences + "/" + BaseEngage.user.id ,"POSTARRAY", getCurrentPreferenceParameter(preferences), BaseEngage.getAuthroizedUser());
    }

    public void CreateUserSchedule(List<Schedule> schedules) {
        execute(HttpClient.WebRequestUrl + Endpoints.UserSchedule,"POSTARRAY", getUserScheduleParameter(schedules), BaseEngage.getAuthroizedUser());
    }

    public void GetUserSchedule() {
        execute(HttpClient.WebRequestUrl + Endpoints.UserSchedule + "/" + BaseEngage.user.id ,"GET", new HashMap<>(), BaseEngage.getAuthroizedUser());
    }

    public void UpdateUserSchedule(List<Schedule> schedules) {
        execute(HttpClient.WebRequestUrl + Endpoints.UpdateUserSchedule, "POSTARRAY", getUserScheduleParameter(schedules), BaseEngage.getAuthroizedUser());
    }

    private Map<String, Object> getGoogleUserParams(GoogleUser googleUser) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", googleUser.id);
        params.put("email", googleUser.email);
        params.put("givenName", googleUser.givenName);
        params.put("familyName", googleUser.familyName);
        params.put("photo", googleUser.photo);
        return  params;
    }

    private Map<String, Object> getUserParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", BaseEngage.user.id);
        params.put("userName", BaseEngage.user.userName);
        params.put("email", BaseEngage.user.email);
        params.put("firstName", BaseEngage.user.firstName);
        params.put("lastName", BaseEngage.user.lastName);
        params.put("dob", BaseEngage.user.dob);
        params.put("phone", BaseEngage.user.phone);
        return  params;
    }

    private Map<String, Object> getCurrentPreferenceParameter(List<CurrentPreferences> preferences) {
        Map<String, Object> params = new HashMap<>();
        Gson gson = new Gson();
        String json = gson.toJson(preferences);
        params.put("preferences", json);
        return params;
    }

    private Map<String, Object> getUserScheduleParameter(List<Schedule> schedule) {
        Map<String, Object> params = new HashMap<>();
        Gson gson = new Gson();
        String json = gson.toJson(schedule);
        params.put("schedule", json);
        return params;
    }
}
