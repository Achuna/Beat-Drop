package com.example.beatdrop;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class Restore extends AsyncTask<Void, Void, ArrayList<Song>> {

    interface AsyncResponse {
        void processFinished(ArrayList<Song> cloudSongs);
    }

    Context context;
    AsyncResponse delegate;
    String ip;
    int storage; //1 = cloud, 0 = remote

    public Restore(Context context, String ip, int storage, AsyncResponse response) {
        this.context = context;
        this.ip = ip;
        this.storage = storage;
        this.delegate = response;
    }

    @Override
    protected ArrayList<Song> doInBackground(Void... params) {
        String url;
        if(storage == 1) {
            url = "https://achunaofonedu.000webhostapp.com/BeatDrop/get.php";
        } else {
            url = "http://" + ip + "/beatDrop/get.php";
        }

        ArrayList<Song> songs = new ArrayList<>();

        try {
            URL con = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) con.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            InputStream is = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            //Build JSON Response
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            Log.i("JSON", builder.toString());

            //Close byte stream
            is.close();
            bufferedReader.close();

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jb = jsonArray.getJSONObject(i);
                songs.add(new Song(jb.getString("link"), jb.getString("mood")));
            }

            //Checking
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.i("JSON", songs.get(i).toString());
            }

            return songs;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Song>(0);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Song> cloudSongs) {
        delegate.processFinished(cloudSongs);
    }


}
