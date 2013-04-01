package com.usual.and.mb;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MagicBox extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magic_box);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.magic_box, menu);
        return true;
    }
    
}
