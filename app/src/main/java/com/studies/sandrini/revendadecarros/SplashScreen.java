package com.studies.sandrini.revendadecarros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dropbox.client2.android.AndroidAuthSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.session.AppKeyPair;

public class SplashScreen extends AppCompatActivity {

    //private final  int DELAY = 3000; //3 seconds
    private final String URL = "https://content.dropboxapi.com/1/files/auto/s/d24im9i7e3tczls/carros.json";
    private final String APP_KEY = "tss6wc39fcnupfn";
                                    //tss6wc39fcnupfn   my app key
                                    //d24im9i7e3tczls
                                    //kpmnej4x11m6dbj
    private final String APP_SECRET = "d24im9i7e3tczls";
    private final String ACCOUNT_PREFS_NAME = "prefs_dropbox";
    private final String ACCESS_TOKEN = "ACCESS_TOKEN";

    private DropboxAPI<AndroidAuthSession> dropboxApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);

        AndroidAuthSession session = buildSession();
        dropboxApi = new DropboxAPI<AndroidAuthSession>(session);

        //dropboxApi.getSession().startOAuth2Authentication(SplashScreen.this);
        //dropboxApi.getSession().isLinked();

        if(session.authenticationSuccessful()){
            new GetCarros().execute(URL);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        AndroidAuthSession session = dropboxApi.getSession();

        if(session.authenticationSuccessful()){
            session.finishAuthentication();

            saveLogged(session);
        }
    }

    public AndroidAuthSession buildSession(){
        AppKeyPair akp = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(akp);
        loadSession(session);

        return(session);
    }

    public void loadSession(AndroidAuthSession session){
        SharedPreferences sp = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String token = sp.getString(ACCESS_TOKEN, null);

        if(token == null || token.length() == 0){
            return;
        }
        else{
            session.setOAuth2AccessToken(token);
        }
    }

    public void saveLogged(AndroidAuthSession session){
        String token = session.getOAuth2AccessToken();
        if(token != null){
            SharedPreferences sp = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(ACCESS_TOKEN, token);
            edit.commit();
        }
    }

    private class GetCarros extends AsyncTask<String, Void, Void> {

        Bundle bundle;
        Intent intent = new Intent(SplashScreen.this, MasterActivity.class);
        private DropboxFileInfo info;

        @Override
        protected Void doInBackground(String... params) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Thread th = new Thread(new Runnable() {
                            public void run() {
                                java.io.File file = new java.io.File((getApplicationContext().getFileStreamPath("carros.json").getPath()));
                                try {
                                    FileOutputStream outputStream = new FileOutputStream(file);
                                    Log.i("step", "converted");
                                    info = dropboxApi.getFile(params, null, outputStream, null);
                                    Log.i("step", "downloaded");
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }catch (DropboxException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        th.start();
                    }
                });

/*
               new Thread(){
                    public void run(){
                        //File file = null;
                        try {
                            java.io.File file = new java.io.File((getApplicationContext().getFileStreamPath("carros.json").getPath()));
                            //file = new File(Environment.getExternalStorageDirectory(), "carros.json");
                            Log.i("step", "File created");
                            Log.i("step", file.toString());
                            FileOutputStream os = new FileOutputStream(file);
                            Log.i("step", "File converted");
                            DropboxFileInfo info = dropboxApi.getFile(URL, null, os, null);
                            Log.i("step", "The file's rev is: " + info.getMetadata().rev);
                            Log.i("step", "File got");
                        }
                        catch (DropboxException e) {
                            e.printStackTrace();
                        }
                        catch(FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();


                String finalJson = buffer.toString();
                Log.i("step", finalJson);

                JSONObject jsonObj = new JSONObject(finalJson);
                Log.i("step", "converted to json obj");

                JSONArray carros = jsonObj.getJSONArray("carros");
                Log.i("step", "converted to json array");
                //percorrendo array e pegando times

                for(int i = 0; i < carros.length(); i++) {
                    JSONObject c = carros.getJSONObject(i);
                    Log.i("step", "looping json array");
                    String id = c.getString("id");
                    String foto = c.getString("foto");
                    String modelo = c.getString("modelo");
                    String fabricante = c.getString("fabricante");
                    String ano = c.getString("ano");
                    String cor = c.getString("cor");
                    String preco = c.getString("preco");


                    bundle.putString("id", id);
                    bundle.putString("foto", foto);
                    bundle.putString("modelo", modelo);
                    bundle.putString("fabricante", fabricante);
                    bundle.putString("ano", ano);
                    bundle.putString("cor", cor);
                    bundle.putString("preco", preco);
                }
                Log.i("step", "in bundle");

                intent.putExtras(bundle);*/
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i("step", "Starting new activity");
            startActivity(intent);
            Log.i("step", "Activity started");
            finish();
            Log.i("step", "Activity ended");
        }
    }
}
