package com.regional.autonoma.corporacion.eva;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_LogOut) {
            SharedPreferences userPreferences = getSharedPreferences(
                    getResources().getString(R.string.user_logIn), MODE_PRIVATE);
            SharedPreferences.Editor preferenceEditor = userPreferences.edit();
            preferenceEditor.remove("publicKey");
            preferenceEditor.apply();

            //go to the log in page
            Intent logIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(logIntent);

            return true;
        }

        if (id == R.id.action_modifyPk){
            SharedPreferences userPreferences = getSharedPreferences(
                    getResources().getString(R.string.user_logIn), MODE_PRIVATE);
            SharedPreferences.Editor preferenceEditor = userPreferences.edit();
            //this corrupts the stored public key, to check invalid responses are handled correctly
            preferenceEditor.putString("publicKey", "ABC");
            preferenceEditor.commit();
        }

        return super.onOptionsItemSelected(item);
    }

}
