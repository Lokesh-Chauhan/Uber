package com.example.uber;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {

    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("VU6N4Eg0NpJ3ktq4l98bpF7ZzsicWzt7JM8aTD25")
                // if defined
                .clientKey("UtdGn8L200FdKp7I9W4oDUOdfsqHZi5KE0uOE5CX")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
