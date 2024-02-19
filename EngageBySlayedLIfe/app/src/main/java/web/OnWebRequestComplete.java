package web;

import org.json.JSONException;
import java.text.ParseException;

public interface OnWebRequestComplete {
    void onWebRequestComplete(Object obj) throws JSONException, ParseException, InterruptedException;
}
