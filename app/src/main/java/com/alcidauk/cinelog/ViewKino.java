package com.alcidauk.cinelog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alcidauk.cinelog.dao.LocalKino;
import com.alcidauk.cinelog.dao.TmdbKino;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewKino extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.view_poster)
    ImageView poster;
    @BindView(R.id.view_title)
    TextView title;
    @BindView(R.id.view_year)
    TextView year;
    @BindView(R.id.view_overview)
    TextView overview;
    @BindView(R.id.view_rating)
    RatingBar rating;
    @BindView(R.id.view_review)
    TextView review;
    @BindView(R.id.view_review_date)
    TextView review_date;

    LocalKino kino;
    int position;
    boolean editted = false;

    private static final int RESULT_ADD_REVIEW = 3;

    @OnClick(R.id.fab)
    public void onClick(View view) {
        Intent intent = new Intent(this, AddReview.class);
        intent.putExtra("kino", Parcels.wrap(kino));
        startActivityForResult(intent, RESULT_ADD_REVIEW);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_kino);
        ButterKnife.bind(this);

        kino = unwrapKino(getIntent().getParcelableExtra("kino"));
        position = getIntent().getIntExtra("kino_position", -1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int maxRating;
        if (kino.getMaxRating() == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String defaultMaxRateValue = prefs.getString("default_max_rate_value", "5");
            maxRating = Integer.parseInt(defaultMaxRateValue);
        } else {
            maxRating = kino.getMaxRating();
        }

        rating.setNumStars(maxRating);
        rating.setRating(kino.getRating());
        rating.setStepSize(0.5f);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Nullable
    private LocalKino unwrapKino(Parcelable kino) {
        LocalKino unwrap = Parcels.unwrap(kino);

        unwrap.__setDaoSession(((KinoApplication) getApplication()).getDaoSession());
        return unwrap;
    }

    @Override
    protected void onStart() {
        super.onStart();

        TmdbKino tmdbKino = kino.getKino();

        if(tmdbKino != null) {
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185" + tmdbKino.getPoster_path())
                    .centerCrop()
                    //.placeholder(R.drawable.loading_spinner)
                    .crossFade()
                    .into(poster);
            year.setText(tmdbKino.getRelease_date());
            overview.setText(tmdbKino.getOverview());
        }

        title.setText(kino.getTitle());


        rating.setRating(kino.getRating());
        review.setText(kino.getReview());
        review_date.setText(getReviewDateAsString(kino.getReview_date()));

        toolbar.setTitle(kino.getTitle());
        System.out.println("onStart()");
    }

    private String getReviewDateAsString(Date review_date) {
        if (review_date != null) {
            return new SimpleDateFormat("dd/MM/yyyy").format(review_date);
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_ADD_REVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                //addNewLocation(data);
                kino = (LocalKino) unwrapKino(data.getParcelableExtra("kino"));
                editted = true;
                System.out.println("Result Ok");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("Result Cancelled");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (editted) {
                    Intent returnIntent = getIntent();
                    returnIntent.putExtra("kino", Parcels.wrap(kino));
                    returnIntent.putExtra("kino_position", position);
                    setResult(Activity.RESULT_OK, returnIntent);
                }

                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }

}
