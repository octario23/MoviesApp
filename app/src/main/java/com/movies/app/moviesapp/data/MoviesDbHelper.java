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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Manages a local database for weather data.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIES_TABLE =  "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID +  " INTEGER PRIMARY KEY," +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER  NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE +  " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE +  " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_THUMB_URL + " TEXT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_FAVORITE + " BOOLEAN FALSE, " +

                " UNIQUE (" + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

        final String SQL_CREATE_TRAILERS_TABLE =  "CREATE TABLE " + MoviesContract.TrailersEntry.TABLE_NAME + " (" +
                MoviesContract.TrailersEntry._ID +  " INTEGER PRIMARY KEY," +
                MoviesContract.TrailersEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL," +
                MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MoviesContract.TrailersEntry.COLUMN_KEY +  " TEXT NOT NULL," +
                MoviesContract.TrailersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_SITE +  " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_SIZE + " INTEGER NULL, " +
                MoviesContract.TrailersEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_FORMAT + " TEXT NOT NULL, " +
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + " (" + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + ") , " +

                " UNIQUE (" + MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + ", " +
                MoviesContract.TrailersEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);

        final String SQL_CREATE_REVIEWS_TABLE =  "CREATE TABLE " + MoviesContract.ReviewEntry.TABLE_NAME + " (" +
                MoviesContract.ReviewEntry._ID +  " INTEGER PRIMARY KEY," +
                MoviesContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL," +
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MoviesContract.ReviewEntry.COLUMN_AUTHOR +  " TEXT NOT NULL," +
                MoviesContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_URL +  " TEXT NOT NULL, " +
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + " (" + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + ") , " +

                " UNIQUE (" + MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + ", " +
                MoviesContract.ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TrailersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
