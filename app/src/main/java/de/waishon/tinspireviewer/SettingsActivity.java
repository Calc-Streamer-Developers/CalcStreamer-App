package de.waishon.tinspireviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import de.waishon.tinspireviewer.WelcomeActivity;
import de.waishon.tinspireviewer.fragments.SettingsFragment;

/**
 * Created by Sören on 13.03.2016.
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity.instance.recreate();
                finish();
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.settings_activity_frame_layout, new SettingsFragment()).commit();
    }


}
