package com.example.piotr.filmapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmResults;

public class FilmActivity extends AppCompatActivity {

    private String imdbID;
    private Realm realm;

    TextView title_field, year_field, country_field, rating_field, director_field, runtime_field;
    ImageView image_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film);

        if (getIntent().getStringExtra("imdbID") != null){
            imdbID = getIntent().getStringExtra("imdbID");
        }else{
            finish();
        }

        setupView();
        setupRealm();
    }

    //Initialize views
    private void setupView(){
        title_field = findViewById(R.id.title_field);
        year_field = findViewById(R.id.year_field);
        country_field = findViewById(R.id.country_field);
        rating_field = findViewById(R.id.rating_field);
        image_field = findViewById(R.id.image_field);
        director_field = findViewById(R.id.director_field);
        runtime_field = findViewById(R.id.runtime_field);
    }

    private void setupRealm(){
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Query Realm for favorite film
        FilmModel result = realm.where(FilmModel.class).equalTo("imdbID", imdbID).findFirst();

        // Load favorites films
        if (result != null){
            setFilm(result);
        }
    }

    //Set film data to view
    private void setFilm(FilmModel filmModel){
        title_field.setText(filmModel.getTitle());
        year_field.setText(filmModel.getYear());
        country_field.setText("Kraj: " + filmModel.getCountry());
        rating_field.setText("Ocena: " +filmModel.getImdbRating());
        director_field.setText("Reżyser: " +filmModel.getDirector());
        runtime_field.setText("Długość filmu: " +filmModel.getRuntime());
        Picasso.get().load(filmModel.getPoster()).error(R.drawable.ic_photo).into(image_field);
    }
}
