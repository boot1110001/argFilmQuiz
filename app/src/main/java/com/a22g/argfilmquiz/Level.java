package com.a22g.argfilmquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

public class Level extends AppCompatActivity {

    private int id;
    private JSONObject JSONobj;

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in setId(int).
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    private String readJsonFile(int id) throws Exception {

        InputStream is = getResources().openRawResource(id);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        return writer.toString();
    }

    /* PARA ACTUALIZAR AL VOLVER ATRAS
    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int result=data.getIntExtra("result",0);
                //Log.d("### LALALALA","RESULT: "+result);
                ImageView imgb = findViewById(result);
                imgb.setBackground(getResources().getDrawable(R.drawable.border_true));
                imgb.setPadding(10,0,10,0);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Log.d("### LALALALA","NO HAY RESULT");
            }
        }
    }//onActivityResult

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int i;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        Bundle b = getIntent().getExtras();
        id = b.getInt("id",0);
        setTitle(b.getString("levelName","¿nivel?"));

        try {
            JSONobj=new JSONObject(readJsonFile(R.raw.level_content));
        } catch (Exception e) {
            Log.d("##### EXCPETION","readJsonFile || new JSONArray(levelData)");
            e.printStackTrace();
        }

        LinearLayout ll = findViewById(R.id.level_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        try {
            JSONArray moviesArr = JSONobj.getJSONArray("movies");
            JSONArray levelArr = (JSONArray) moviesArr.get(id);

            i=0;
            while (i<levelArr.length()) {

                JSONObject levelItem = (JSONObject) levelArr.get(i);
                JSONArray itemFrames = levelItem.getJSONArray("frame");
                String frameStrId = itemFrames.getString(0);

                ImageView imgb = new ImageView(this);
                Context context = imgb.getContext();
                int frameId = context.getResources().getIdentifier(frameStrId, "drawable", context.getPackageName());
                imgb.setImageResource(frameId);
                int newId = generateViewId();
                imgb.setId(newId);
                imgb.setAdjustViewBounds(true);

                if (SDMng.checkItemOk(id,levelItem.getString("id"))) {
                    imgb.setBackground(getResources().getDrawable(R.drawable.border_true));
                } else {
                    imgb.setBackground(getResources().getDrawable(R.drawable.border_false));
                }
                imgb.setPadding(10,0,10,0);

                levelItem.put("levelImageViewId",newId);
                levelItem.put("levelId",id);

                imgb.setOnClickListener(new MyOnClickListener2(levelItem) {
                    @Override
                    public void onClick(View view) {
                        Intent intentLevelItem = new Intent(getApplicationContext(), LevelItem.class);
                        intentLevelItem.putExtra("levelItemJson", this.params.toString());
                        startActivityForResult(intentLevelItem, 1);
                        //startActivity(intentLevelItem);
                    }
                });

                ll.addView(imgb,lp);

                i++;
            }

        } catch (JSONException e) {
            Log.d("##### EXCPETION", "jsonResponse.get(" + id + ") heyeyeyeyeyeyeee");
            e.printStackTrace();
        }

    }
}
