package com.example.piotr.filmapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    String API_KEY = "47dc5a0f";

    private Realm realm;
    private FilmModel currentFilm;

    EditText search_field;
    ImageButton search_button;
    LinearLayout result, info_box;
    TextView title_field, year_field, country_field, text_field, rating_field;
    ImageView image_field, icon;
    Button save_button;
    ProgressBar loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupView();
        setupRealm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_favorites:
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Initialize views
    private void setupView(){
        search_field = findViewById(R.id.search_field);
        search_button = findViewById(R.id.search_button);
        result = findViewById(R.id.result);
        info_box = findViewById(R.id.info_box);
        title_field = findViewById(R.id.title_field);
        year_field = findViewById(R.id.year_field);
        country_field = findViewById(R.id.country_field);
        rating_field = findViewById(R.id.rating_field);
        image_field = findViewById(R.id.image_field);
        icon = findViewById(R.id.icon);
        loader = findViewById(R.id.loader);
        text_field = findViewById(R.id.text_field);
        save_button = findViewById(R.id.save_button);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = search_field.getText().toString();
                searchFilm(title);

                hideKeyboard();
                search_field.setText("");
                search_field.clearFocus();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFavorite(currentFilm);
                showError("Zapisano");
            }
        });
    }

    private void setupRealm(){
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
    }

    //Set film data to view
    private void setFilm(FilmModel filmModel){
        currentFilm = filmModel;
        title_field.setText(filmModel.getTitle());
        year_field.setText(filmModel.getYear());
        country_field.setText(filmModel.getCountry());
        rating_field.setText(filmModel.getImdbRating());
        Picasso.get().load(filmModel.getPoster()).error(R.drawable.ic_photo).into(image_field);
    }

    //Save film in favorite
    private void saveFavorite(final FilmModel filmModel){
        // Asynchronously update objects on a background thread
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(filmModel);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                save_button.setVisibility(View.GONE);
            }
        });
    }

    //Show error message
    private void showError(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //Show loader view
    private void showLoader(){
        result.setVisibility(View.GONE);
        info_box.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
        text_field.setVisibility(View.GONE);
        icon.setVisibility(View.GONE);
    }

    //Show result screen
    private void showResult(){
        result.setVisibility(View.VISIBLE);
        info_box.setVisibility(View.GONE);
        save_button.setVisibility(View.VISIBLE);
    }

    //Show no results screen
    private void showNoResult(){
        result.setVisibility(View.GONE);
        info_box.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
        text_field.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(R.drawable.ic_list);
        text_field.setText("Nic nie znaleziono");
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void searchFilm(String title) {
        if (!Helper.isNetworkAvailable(getApplicationContext())) {
            showError("Brak połączenia internetowego");
        } else {
            DownloadFilm getFilm = new DownloadFilm();
            getFilm.execute(title);
        }
    }

    //Asynchronously download task
    class DownloadFilm extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader();
        }
        protected String doInBackground(String...args) {
            String response = Helper.downloadUrl("http://www.omdbapi.com/?t=" + args[0] +
                    "&type=movie&apikey=" + API_KEY);
            return response;
        }
        @Override
        protected void onPostExecute(String response) {

            if (response == null){
                showNoResult();
                return;
            }
            try {
                JSONObject json = new JSONObject(response);

                FilmModel filmModel = new FilmModel();
                filmModel.setImdbID(json.getString("imdbID"));
                filmModel.setTitle(json.getString("Title"));
                filmModel.setYear(json.getString("Year"));
                filmModel.setCountry(json.getString("Country"));
                filmModel.setDirector(json.getString("Director"));
                filmModel.setImdbRating(json.getString("imdbRating"));
                filmModel.setPoster(json.getString("Poster"));
                filmModel.setRuntime(json.getString("Runtime"));

                setFilm(filmModel);
                showResult();

            } catch (JSONException e) {
                showNoResult();
            }
        }
    }
}
