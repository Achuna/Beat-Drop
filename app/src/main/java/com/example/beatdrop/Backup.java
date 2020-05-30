package com.example.beatdrop;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Backup extends AsyncTask<ArrayList<Song>, Void, Boolean> {

    interface AsyncResponse {
        void processFinished(boolean result);
    }

    Context context;
    AsyncResponse delegate;
    String ip;
    int storage; //1 = cloud, 0 = remote
    int action;  //1 = add, 0 = delete

    public Backup(Context context, String ip, int storage, int action, AsyncResponse response) {
        this.context = context;
        this.ip = ip;
        this.storage = storage;
        this.action = action;
        this.delegate = response;
    }

    @Override
    protected Boolean doInBackground(ArrayList<Song>... params) {
        String url;
        if(storage == 1) {
            if(action == 1) {
                url = "https://achunaofonedu.000webhostapp.com/BeatDrop/add.php";
            } else {
                url = "https://achunaofonedu.000webhostapp.com/BeatDrop/delete.php";
            }
        } else {
            if(action == 1) {
                url = "http://" + ip + "/beatDrop/add.php";
            } else {
                url = "http://" + ip + "/beatDrop/delete.php";
            }
        }

        ArrayList<Song> songs = params[0];

        Gson gson = new Gson();
        String json = gson.toJson(songs);

        Log.e("JSON", json);

        try {
            URL con = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) con.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
            String data = "";
            if(action == 0) {
                data = URLEncoder.encode("link", "UTF-8") + "=" + URLEncoder.encode(songs.get(0).getLink(), "UTF-8");
            } else {
                data = URLEncoder.encode("songData", "UTF-8") + "=" + URLEncoder.encode(json, "UTF-8");
            }
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            os.close();

            InputStream is = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line = "";
            String response = "";
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }

            Log.e("JSON", response);

            return response.contains("SUCCESS");


        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processFinished(result);
    }


}




