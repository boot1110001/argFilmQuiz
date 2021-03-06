package com.a22g.argfilmquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public final class SDMng {

    private static final String fileName = "argFilmQuiz_saved_data";
    public static final int levelMin = 20; //3
    public static JSONArray jasave = new JSONArray();



    private static ArrayList<String> JAtoAL(JSONArray JSONa) throws JSONException {
        ArrayList<String> out = new ArrayList<>();
        // (JSONArray) JSONa --> (ArrayList<String>) out
        if (JSONa != null) {
            for (int i=0;i<JSONa.length();i++){
                out.add(JSONa.getString(i));
            }
        }
        return out;
    }

    public static void saveData(Context ctx) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(fileName, MODE_PRIVATE).edit();
        String JSONstr=jasave.toString();
        //Log.d("### LALALALALA","JSON to save: "+JSONstr);
        editor.putString("JSON",JSONstr);
        editor.commit();
    }

    public static void saveProgress (Context ctx, int levelId, String filmId) throws JSONException {
        if (!jasave.isNull(levelId)) {
            ((JSONArray)jasave.get(levelId)).put(filmId);
        } else {
            jasave.put(new JSONArray());
            ((JSONArray)jasave.get(levelId)).put(filmId);
        }
        saveData(ctx);
    }

    public static void clearSavedData(Context ctx) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(fileName, MODE_PRIVATE).edit();
        editor.putString("JSON","[]");
        editor.commit();
        jasave = new JSONArray();
        //Log.d("### LALALALALA","CREATED EMPTY JSON SAVE DATA");
    }

    public static boolean checkItemOk(int levelId, String filmnId) {
        boolean out = false;

        try {
            if (!jasave.isNull(levelId)) {
                out = JAtoAL((JSONArray) jasave.get(levelId)).contains(filmnId);
            }
        } catch (JSONException e) {
            Log.d("### EXCEPTION","FALLO EN ''out = JAtoAL((JSONArray)jasave.get("+levelId+")).contains("+filmnId+");''");
            e.printStackTrace();
        }

        return out;
    }

    public static boolean checkIfLevelIsAvaliable(int levelId) {
        boolean out=false;

        if (levelId!=0) {
            try {
                if (!jasave.isNull(levelId-1)) {
                    out = ((JSONArray) jasave.get(levelId - 1)).length() >= levelMin;
                }
            } catch (JSONException e) {
                Log.d("### EXCEPTION", "FALLO EN ''out = ((JSONArray)jasave.get(levelId)).length()>=levelMin;''");
                e.printStackTrace();
            }
        } else out=true;

        return out;
    }

    public static int howManyFilmsAreMissing(int levelId) {
        int out = levelMin;

        try {
            if (!jasave.isNull(levelId-1)) {
                out = levelMin - ((JSONArray) jasave.get(levelId - 1)).length();
            }
        } catch (JSONException e) {
            Log.d("### EXCEPTION", "FALLO EN ''out = ((JSONArray)jasave.get(levelId)).length()>=levelMin;''");
            e.printStackTrace();
        }

        return out;
    }

    public static String exportSavedData() {
        byte[] data = new byte[0];
        try {
            data = jasave.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d("### EXCEPTION", "FALLO EN ''data = jasave.toString().getBytes(\"UTF-8\");''");
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }

    public static boolean validateSavedData(String JSONstr) {
        boolean out = false;

        try {
            JSONArray ja=new JSONArray(JSONstr);
            if (ja!=null) {
                out=true;
            }
        } catch (JSONException e) {
            Log.d("### EXCEPTION","FALLO AL EVALUAR --> JSONstr: ''"+JSONstr+"'' en SDMng.java: ''jasave=new JSONArray(JSONstr);''");
            out=false;
        }

        return out;
    }

    public static void importSavedData(Context ctx, String JSONstr) {
        try {
            JSONArray ja=new JSONArray(JSONstr);
            if (ja!=null) {
                jasave=ja;
                saveData(ctx);
            }
        } catch (JSONException e) {
            Log.d("### EXCEPTION","FALLO AL EVALUAR --> JSONstr: ''"+JSONstr+"'' en SDMng.java: ''jasave=new JSONArray(JSONstr);''");
            e.printStackTrace();
        }
    }

    public static void iniSavedData(Context ctx) {
        SharedPreferences savedData = ctx.getSharedPreferences(fileName, MODE_PRIVATE);
        String JSONstr = savedData.getString("JSON", null);
        if (JSONstr == null) {
            clearSavedData(ctx);
            savedData = ctx.getSharedPreferences(fileName, MODE_PRIVATE);
            JSONstr = savedData.getString("JSON", null);
        }
        try {
            jasave=new JSONArray(JSONstr);
        } catch (JSONException e) {
            Log.d("### EXCEPTION","FALLO AL EVALUAR --> JSONstr: ''"+JSONstr+"'' en SDMng.java: ''jasave=new JSONArray(JSONstr);''");
            e.printStackTrace();
            //clearSavedData(ctx);
        }
        //Log.d("### LALALALALA","JSONstr: "+JSONstr);
    }

}
