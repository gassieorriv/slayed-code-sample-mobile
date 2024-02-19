package web.interfaces;

import java.util.HashMap;
import java.util.Map;
import Base.BaseEngage;
import models.support.SupportNote;
import web.Endpoints;
import web.HttpClient;
import web.OnWebRequestComplete;

public class SupportInterface extends HttpClient {

    public SupportInterface(OnWebRequestComplete listener) {
        super(listener);
    }

    public void CreateSupportUserNote(SupportNote supportNote) {
        execute(HttpClient.WebRequestUrl + Endpoints.CreateUserSupportNote ,"POST", getSupportNoteParam(supportNote), BaseEngage.getAuthroizedUser());
    }

    private Map<String, Object> getSupportNoteParam(SupportNote supportNote) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", BaseEngage.user.id);
        params.put("note", supportNote.note);
        params.put("resolved", supportNote.resolved);
        return  params;
    }
}
