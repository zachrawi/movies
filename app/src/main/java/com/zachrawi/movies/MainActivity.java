package com.zachrawi.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<Movie> mMovies;
    CustomAdapter customAdapter;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovies = new ArrayList<>();
        gridView = findViewById(R.id.gridView);
        mProgressBar = findViewById(R.id.progressBar);

        customAdapter = new CustomAdapter(this, R.layout.item_movie, mMovies);
        gridView.setAdapter(customAdapter);

        String url = "https://api.themoviedb.org/3/discover/movie/?sort_by=popularity.desc&api_key=490bc3f8bd238721511d3c3c21b9e925&include_video=true&region=ID";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("###", "onFailure: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray results = jsonObject.getJSONArray("results");

                        for (int i=0;i<results.length();i++) {
                            JSONObject result = (JSONObject) results.get(i);

                            String title = result.getString("title");
                            String poster = "https://image.tmdb.org/t/p/w500" + result.getString("poster_path");
                            String overview = result.getString("overview");

                            Movie movie = new Movie(poster, title, overview);
                            mMovies.add(movie);
                        }

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                customAdapter.notifyDataSetChanged();

                                gridView.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
