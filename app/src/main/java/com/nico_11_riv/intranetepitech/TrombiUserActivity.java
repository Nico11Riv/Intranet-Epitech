package com.nico_11_riv.intranetepitech;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.astuetz.PagerSlidingTabStrip;
import com.nico_11_riv.intranetepitech.api.IntrAPI;
import com.nico_11_riv.intranetepitech.database.User;
import com.nico_11_riv.intranetepitech.database.setters.user.GUserInfos;
import com.nico_11_riv.intranetepitech.database.setters.user.PUserInfos;
import com.nico_11_riv.intranetepitech.toolbox.CircleTransform;
import com.nico_11_riv.intranetepitech.toolbox.Tools;
import com.nico_11_riv.intranetepitech.ui.adapters.TrombiUserAdapter;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

/**
 * Created by victor on 29/03/2016.
 */

@EActivity(R.layout.activity_trombi_user)
public class TrombiUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Tools tools;

    @RestService
    IntrAPI api;
    @ViewById
    DrawerLayout drawer_layout;
    @ViewById
    Toolbar toolbar;
    @ViewById
    NavigationView nav_view;
    @ViewById
    ViewPager pager;
    @ViewById
    PagerSlidingTabStrip tab_layout;

    @UiThread
    void filluserinfosui() {
        TextView tv = (TextView) findViewById(R.id.menu_login);
        tv.setText(tools.getgUserInfos().getLogin());
        tv = (TextView) findViewById(R.id.menu_email);
        tv.setText(tools.getgUserInfos().getEmail());

        ImageView iv = (ImageView) findViewById(R.id.menu_img);
        Picasso.with(getApplicationContext()).load(tools.getgUserInfos().getPicture()).transform(new CircleTransform()).into(iv);
    }

    void setUserInfos() {
        filluserinfosui();
        if (tools.getIc().connected()) {
            api.setCookie("PHPSESSID", tools.getgUser().getToken());
            try {
                PUserInfos infos = new PUserInfos(tools.getgUser().getLogin());
                infos.init(api.getuserinfo(tools.getgUser().getLogin()));
            } catch (HttpClientErrorException e) {
                Log.d("Response", e.getResponseBodyAsString());
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            tools.setgUserInfos(new GUserInfos());
            filluserinfosui();
        }
    }

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this);
        String login = getIntent().getStringExtra("login");
        tools = new Tools(getApplicationContext());
        setUserInfos();
        TrombiUserAdapter adapter = new TrombiUserAdapter(getSupportFragmentManager(), login);
        pager.setAdapter(adapter);
        tab_layout.setViewPager(pager);
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.exit)
                    .negativeText("Retour")
                    .positiveText("Oui")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            List<User> users = User.find(User.class, "connected = ?", "true");
                            if (users.size() > 0) {
                                users.get(0).setConnected("false");
                                users.get(0).save();
                            }
                            startActivity(new Intent(getApplicationContext(), LoginActivity_.class));
                        }
                    })
                    .icon(getApplicationContext().getDrawable(R.drawable.logo)).limitIconToDefaultSize()
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Tools tools = new Tools(getApplicationContext());
        startActivity(tools.menu(item,this,drawer_layout));
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }
}
