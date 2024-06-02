package com.example.randommovie;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView randomMovieTextView;
    private Button showRandomMovieButton;
    private Button restartButton;
    private JSONArray moviesArray;
    private List<Integer> randomIndices = new ArrayList<>();
    private ImageView posterImageView;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        randomMovieTextView = findViewById(R.id.movieInfoTextView);
        showRandomMovieButton = findViewById(R.id.randomMovieButton);
        restartButton = findViewById(R.id.restartMovieButton);
        posterImageView = findViewById(R.id.posterImage);

        try {
            // Чтение JSON из ресурсов (res/raw/movies.json)
            InputStream inputStream = getResources().openRawResource(R.raw.movie);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            moviesArray = new JSONArray(json);

            // Создание списка случайных индексов
            for (int i = 0; i < moviesArray.length(); i++) {
                randomIndices.add(i);
            }
            Collections.shuffle(randomIndices);

            showRandomMovieButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentIndex < moviesArray.length()) {
                        showRandomMovie(currentIndex);
                        currentIndex++;
                        if (!randomIndices.isEmpty()) {
                            int index = randomIndices.remove(0);
                            showRandomMovie(index);
                        }
                    } else {
                        posterImageView.setImageDrawable(null);
                        randomMovieTextView.setText("Фильмы закончились");
                        restartButton.setVisibility(View.VISIBLE);
                        showRandomMovieButton.setVisibility(View.GONE);
                    }
                }
            });

            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentIndex = 0;

                    randomIndices.clear();
                    for (int i = 0; i < moviesArray.length(); i++) {
                        randomIndices.add(i);
                    }
                    Collections.shuffle(randomIndices);

                    randomMovieTextView.setText(null);
                    restartButton.setVisibility(View.GONE); // Скрыть кнопку "Начать сначала"
                    showRandomMovieButton.setVisibility(View.VISIBLE); // Сделать кнопку "Показать случайный фильм" видимой
                }
            });


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void showRandomMovie(int index) {
        try {
            JSONObject randomMovie = moviesArray.getJSONObject(index);


            TextView movieInfoTextView = findViewById(R.id.movieInfoTextView);

            String name = randomMovie.getString("name");
            String year = randomMovie.getString("year");
            String genre = randomMovie.getString("genre");
            String directed = randomMovie.getString("directed");
            String budget = randomMovie.getString("budget");
            String duration = randomMovie.getString("duration");
            String description = randomMovie.getString("description");

            // Получите имя файла плаката из JSON
            String posterFileName = randomMovie.getString("poster");
            // Определите ресурс изображения по имени файла
            int posterResourceId = getResources().getIdentifier(posterFileName, "raw", getPackageName());

            // Установите изображение плаката
            posterImageView.setImageResource(posterResourceId);

            movieInfoTextView.setText(
                    "Название: " + name + "\n\n" +
                    "Год: " + year + "\n\n" +
                    "Жанр: " + genre + "\n\n" +
                    "Режиссер: " + directed + "\n\n" +
                    "Бюджет: " + budget + "\n\n" +
                    "Продолжительность: " + duration + "\n\n" +
                    "Описание: " + description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}