package com.movies.app.moviesapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.movies.app.moviesapp.R;
import com.movies.app.moviesapp.data.CommonTasks;
import com.movies.app.moviesapp.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int MOVIES_NOTIFICATION_ID = 3004;
    private static final String MOVIE_BASE_URL =
            "http://api.themoviedb.org/3/discover/movie?";
    private static final String SORT_BY = "sort_by";
    private final String API_KEY = "api_key";


//    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[] {
//            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
//            MoviesContract.MoviesEntry..COLUMN_MAX_TEMP,
//            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
//            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
//    };

    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;
    private static final int INDEX_SHORT_DESC = 3;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        String locationQuery = "";
        if(CommonTasks.isPopularFilter(getContext())){
            locationQuery = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        }else{
            locationQuery = MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }


        try {
        String json = null;
        json = downloadContent(MOVIE_BASE_URL,SORT_BY,API_KEY);

            getDataFromJson(json, locationQuery,"movies",0);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return;

    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getDataFromJson(String jsonStr,
                                        String sortSetting, String type, int id)
            throws JSONException {

        switch (type){
            case "movies" :
                final String OWM_LIST = "results";
                final String MOVIE_ID = "id";
                final String TITLE = "original_title";
                final String OVERVIEW = "overview";
                final String RELEASE_DATE = "release_date";
                final String POSTER_PATH = "poster_path";
                final String POPULARITY = "popularity";
                final String RATING = "vote_average";


                try {

                    JSONObject forecastJson = new JSONObject(jsonStr);
                    JSONArray moviesArray = forecastJson.getJSONArray(OWM_LIST);

                    if(moviesArray!=null) {
                        // Insert  into the database
                        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

                        for (int i = 0; i < moviesArray.length(); i++) {
                            // These are the values that will be collected.
                            int movieId;
                            String title;
                            String overview;
                            String release_date;
                            String poster_path;
                            String popularity;
                            String rating;


                            // Get the JSON object representing a movie
                            JSONObject movieItem = moviesArray.getJSONObject(i);
                            title = movieItem.getString(TITLE);
                            movieId = movieItem.getInt(MOVIE_ID);
                            overview = movieItem.getString(OVERVIEW);
                            release_date = movieItem.getString(RELEASE_DATE);
                            poster_path = movieItem.getString(POSTER_PATH);
                            popularity = movieItem.getString(POPULARITY);
                            rating = movieItem.getString(RATING);


                            ContentValues movieValues = new ContentValues();
                            Log.i("SyncAdapter", title);
                            movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movieId);
                            movieValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, title);
                            movieValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, overview);
                            movieValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, release_date);
                            movieValues.put(MoviesContract.MoviesEntry.COLUMN_THUMB_URL, poster_path);
                            movieValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, rating);
                            movieValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, popularity);
                            movieValues.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, false);

                            cVVector.add(movieValues);
//                        insert trailers
                            String json = null;
                            String urlTrailers = "http://api.themoviedb.org/3/movie/" + movieId + "/videos?";
                            json = downloadContent(urlTrailers, "", API_KEY);
                            getDataFromJson(json, "", "trailers", movieId);

//                        insert reviews
                            String jsonReview = null;
                            String urlReviews = "http://api.themoviedb.org/3/movie/" + movieId + "/reviews?";
                            jsonReview = downloadContent(urlReviews, "", API_KEY);
                            getDataFromJson(jsonReview, "", "reviews", movieId);

                        }

                        int inserted = 0;
                        // add to database
                        if (cVVector.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[cVVector.size()];
                            cVVector.toArray(cvArray);
                            getContext().getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);

                        }

                        Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            break;
            case "trailers":
                final String RESULTS_TRAILER = "results";
                final String COLUMN_TRAILER_ID = "id";
                final String COLUMN_KEY = "key";
                final String COLUMN_NAME = "name";
                final String COLUMN_SITE = "site";
                final String COLUMN_SIZE = "size";
                final String COLUMN_TYPE = "type";
                final String COLUMN_FORMAT = "iso_639_1";

                try {
                    JSONObject forecastJson = new JSONObject(jsonStr);
                    JSONArray trailersArray = forecastJson.getJSONArray(RESULTS_TRAILER);
                    if(trailersArray!=null) {
                        // Insert  into the database
                        Vector<ContentValues> cVVector = new Vector<ContentValues>(trailersArray.length());

                        for (int i = 0; i < trailersArray.length(); i++) {
                            // These are the values that will be collected.
                            String trailerId;
                            int movieId;
                            String key;
                            String name;
                            String site;
                            String size;
                            String typeTrailer;
                            String format;


                            // Get the JSON object representing a movie
                            JSONObject movieItem = trailersArray.getJSONObject(i);
                            trailerId = movieItem.getString(COLUMN_TRAILER_ID);
                            movieId = id;
                            key = movieItem.getString(COLUMN_KEY);
                            name = movieItem.getString(COLUMN_NAME);
                            site = movieItem.getString(COLUMN_SITE);
                            size = movieItem.getString(COLUMN_SIZE);
                            typeTrailer = movieItem.getString(COLUMN_TYPE);
                            format = movieItem.getString(COLUMN_FORMAT);


                            ContentValues trailerValues = new ContentValues();
                            Log.i("SyncAdapter", name);
                            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_MOVIE_ID, movieId);
                            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_TRAILER_ID, trailerId);
                            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_KEY, key);
                            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_NAME, name);
                            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_SITE, site);
                            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_SIZE, size);
                            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_TYPE, typeTrailer);
                            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_FORMAT, format);

                            cVVector.add(trailerValues);
                        }

                        int inserted = 0;
                        // add to database
                        if (cVVector.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[cVVector.size()];
                            cVVector.toArray(cvArray);
                            getContext().getContentResolver().bulkInsert(MoviesContract.TrailersEntry.CONTENT_URI, cvArray);
                        }

                        Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                break;

            case "reviews":
                final String RESULTS_REVIEW = "results";
                final String COLUMN_REVIEW_ID = "id";
                final String COLUMN_AUTHOR = "author";
                final String COLUMN_CONTENT = "content";
                final String COLUMN_URL = "url";

                try {
                    JSONObject forecastJson = new JSONObject(jsonStr);
                    JSONArray reviewArrays = forecastJson.getJSONArray(RESULTS_REVIEW);
                        // Insert  into the database
                    if(reviewArrays!=null) {
                        Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArrays.length());
                            for (int i = 0; i < reviewArrays.length(); i++) {
                                // These are the values that will be collected.
                                String reviewId;
                                int movieId;
                                String author;
                                String content;
                                String url;

                                // Get the JSON object representing a movie
                                JSONObject movieItem = reviewArrays.getJSONObject(i);
                                reviewId = movieItem.getString(COLUMN_REVIEW_ID);
                                movieId = id;
                                author = movieItem.getString(COLUMN_AUTHOR);
                                content = movieItem.getString(COLUMN_CONTENT);
                                url = movieItem.getString(COLUMN_URL);

                                ContentValues reviewValues = new ContentValues();
                                reviewValues.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                                reviewValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_ID, reviewId);
                                reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, author);
                                reviewValues.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, content);
                                reviewValues.put(MoviesContract.ReviewEntry.COLUMN_URL, url);

                                cVVector.add(reviewValues);
                            }


                        int inserted = 0;
                        // add to database
                        if (cVVector.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[cVVector.size()];
                            cVVector.toArray(cvArray);
                            getContext().getContentResolver().bulkInsert(MoviesContract.ReviewEntry.CONTENT_URI, cvArray);
                        }

                        Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                } catch (NullPointerException ex){
                    Log.e(LOG_TAG, ex.getMessage(), ex);
                    ex.printStackTrace();
                }
                break;
        }

    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private String downloadContent(String urlString, String sortByString, String keyString){

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Uri builtUri = null;

                String sortBy = "popularity.desc";
        String key = "855b728a711aebd622c8a53457b23f70";

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast


            if(sortByString!=""){
                builtUri = Uri.parse(urlString).buildUpon()
                        .appendQueryParameter(sortByString, sortBy)
                        .appendQueryParameter(keyString, key)
                        .build();
            }
            else{
                builtUri = Uri.parse(urlString).buildUpon()
                        .appendQueryParameter(keyString, key)
                        .build();
            }

            URL url = new URL(builtUri.toString());

            // Create the request movies DB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return "";
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return forecastJsonStr;
    }
}