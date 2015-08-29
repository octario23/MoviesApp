/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.movies.app.moviesapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_RATING = 101;
    static final int MOVIE_WITH_POPULARITY = 102;

    private static final SQLiteQueryBuilder sMoviesBySettingQueryBuilder = null;
//
//    static{
//        sMoviesBySettingQueryBuilder = new SQLiteQueryBuilder();
//
//        //This is an inner join which looks like
//        //weather INNER JOIN location ON weather.location_id = location._id
//        sMoviesBySettingQueryBuilder    .setTables(
//                MoviesContract.MoviesEntry.TABLE_NAME + " INNER JOIN " +
//                        MoviesContract.LocationEntry.TABLE_NAME +
//                        " ON " + MoviesContract.WeatherEntry.TABLE_NAME +
//                        "." + MoviesContract.WeatherEntry.COLUMN_LOC_KEY +
//                        " = " + MoviesContract.LocationEntry.TABLE_NAME +
//                        "." + MoviesContract.LocationEntry._ID);
//    }

    //location.location_setting = ?
    private static final String sRatingSettingSelection =
            MoviesContract.MoviesEntry.TABLE_NAME+
                    "." + MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " = ? ";


    //location.location_setting = ?
//    private static final String sPopularSettingSelection =
//            MoviesContract.MoviesEntry.TABLE_NAME +
//                    "." + MoviesContract.MoviesEntry.COLUMN_OVERVIEW + " = ? AND " +
//                    MoviesContract.WeatherEntry.COLUMN_DATE + " = ? ";

    private Cursor getMoviesByVoteAverage(Uri uri, String[] projection, String sortOrder) {
        String ratingSettings = MoviesContract.MoviesEntry.getLocationSettingFromUri(uri);
        long startDate = MoviesContract.MoviesEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

//        if (startDate == 0) {
            selection = sRatingSettingSelection;
            selectionArgs = new String[]{ratingSettings};
//        } else {
//            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
//            selection = sLocationSettingWithStartDateSelection;
//        }

        return sMoviesBySettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

//    private Cursor getWeatherByLocationSettingAndDate(
//            Uri uri, String[] projection, String sortOrder) {
//        String locationSetting = MoviesContract.WeatherEntry.getLocationSettingFromUri(uri);
//        long date = MoviesContract.WeatherEntry.getDateFromUri(uri);
//
//        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                projection,
//                sLocationSettingAndDaySelection,
//                new String[]{locationSetting, Long.toString(date)},
//                null,
//                null,
//                sortOrder
//        );
//    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES +  "/*", MOVIE_WITH_RATING);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*/#",MOVIE_WITH_POPULARITY);

        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new MoviesDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE_WITH_POPULARITY:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_RATING:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
//            // "movie/*/*"
//            case MOVIE_WITH_POPULARITY:
//            {
////                retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
//                break;
//            }
//            // "movie/*"
//            case MOVIE_WITH_RATING: {
////                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
//                break;
//            }
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
//                normalizeDate(values);
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.MoviesEntry.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
//        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if(null == selection) selection = "1";
        switch (match){
            case MOVIE:
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,selection,selectionArgs
                );
                break;
            default:
                throw  new UnsupportedOperationException("Unknown uri: " + uri);
        }
         if (rowsDeleted !=0){
             getContext().getContentResolver().notifyChange(uri,null);
         }
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)) {
            long dateValue = values.getAsLong(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE);
            values.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, MoviesContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        if(null == selection) selection = "1";
        switch (match){
            case MOVIE:
//                normalizeDate(values);
                rowsUpdated = db.update(
                        MoviesContract.MoviesEntry.TABLE_NAME, values,selection,selectionArgs
                );
                break;
            default:
                throw  new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
//                        normalizeDate(value);
                        long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}