package com.example.piotr.filmapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class FavoritesActivity extends AppCompatActivity {

    private RealmResults<FilmModel> results;
    private Realm realm;

    LinearLayout info_box;
    TextView text_field;
    ImageView icon;
    ProgressBar loader;
    RecyclerView list;

    CustomAdapter adapter = new CustomAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Ulubione");
        }

        setupView();
        setupAdapter();
        setupRealm();
    }

    private void setupView(){
        info_box = findViewById(R.id.info_box);
        icon = findViewById(R.id.icon);
        loader = findViewById(R.id.loader);
        text_field = findViewById(R.id.text_field);
        list = findViewById(R.id.list);
    }

    //show list screen
    private void showList(){
        list.setVisibility(View.VISIBLE);
        info_box.setVisibility(View.GONE);
    }

    //Show message
    private void showMessage(){
        list.setVisibility(View.GONE);
        info_box.setVisibility(View.VISIBLE);
    }

    private void setupAdapter(){
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setClickListener(new CustomAdapter.ClickListener() {
            @Override
            public void clickItem(FilmModel item) {
                Intent intent = new Intent(FavoritesActivity.this, FilmActivity.class);
                intent.putExtra("imdbID", item.getImdbID());
                startActivity(intent);
            }
        });

        adapter.setRemoveListener(new CustomAdapter.ClickListener() {
            @Override
            public void clickItem(final FilmModel item) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(FavoritesActivity.this);
                builder1.setMessage("Czy chcesz usunać ten film ?");
                builder1.setPositiveButton(
                        "Tak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                deleteFilm(item.getImdbID());
                            }
                        });

                builder1.setNegativeButton(
                        "Nie",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    private void setupRealm(){

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        // Query Realm for all favorite films
        results = realm.where(FilmModel.class).findAllAsync();

        // Load favorites films
        if (results.isLoaded() && results.size() > 0){
            showList();
            adapter.setData(realm.copyFromRealm(results));
        }else{
            showMessage();
        }

        results.addChangeListener(new RealmChangeListener<RealmResults<FilmModel>>() {
            @Override
            public void onChange(@NonNull RealmResults<FilmModel> filmModels) {
                if (filmModels.size() > 0){
                    showList();
                    adapter.setData(realm.copyFromRealm(filmModels));
                    adapter.notifyDataSetChanged();
                }else{
                    showMessage();
                }
            }
        });
    }

    //Remove film from local memory
    private void deleteFilm(String imdbID) {
        realm.beginTransaction();
        FilmModel filmModel = realm.where(FilmModel.class).equalTo("imdbID", imdbID).findFirst();
        if (filmModel != null){
            filmModel.deleteFromRealm();
        }
        realm.commitTransaction();
    }
}
