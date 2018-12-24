package com.example.mark.chemecalc;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener/*,OnMenuItemClickListener */{
//    protected Button popupMenuButton;
//    protected PopupMenu popupMenuTest;
    protected Menu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        popupMenuButton = (Button) findViewById(R.id.popupMenuButton);
//        popupMenuButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupMenuTest = new PopupMenu(MainActivity.this, popupMenuButton);
//
//                popupMenuTest.getMenuInflater().inflate(R.menu.popup_menu, popupMenuTest.getMenu());
//
//                popupMenuTest.setOnMenuItemClickListener(MainActivity.this);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();
        Fragment frag_page = null;
        String title = "ChemECalc";
        switch(item.getItemId())
        {
            case R.id.search_formulas:
                // TODO
                ;break;
            case R.id.nav_calculations:
//                frag_page = new CalcReynolds();
//                title = "Reynolds Number";
//                break;
                frag_page = new FragCalculations();
                title = FragCalculations.title;
                break;
            case R.id.nav_favorites:
                frag_page = new FragFavorites();
                title = FragFavorites.title;
                break;
            case R.id.nav_history:
                frag_page = new FragHistory();
                title = FragHistory.title;
                break;
            case R.id.nav_tools:
                frag_page = new FragTools();
                title = FragTools.title;
                break;
            case R.id.nav_settings:
                frag_page = new FragSettings();
                title = FragSettings.title;
                break;
        }

        if(frag_page != null)
        {
            getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                frag_page).commit();
            getSupportActionBar().setTitle(title);
        }

        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        popupMenu = menu;
//        popupMenu = (PopupMenu) menu;
//        popupMenu.setOnMenuItemClickListener(this);
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
int id = item.getItemId();
        switch(id)
        {
            case R.id.popup_info:
                Toast.makeText(this, "Info", Toast.LENGTH_SHORT).show();
                break;
            case R.id.popup_use_si:
                item.setChecked(!item.isChecked());
                break;
        }
//        popupMenu.show();
        return true;

    }

//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//        item.setActionView(new View(this));
//        int id = item.getItemId();
//        switch(id)
//        {
//            case R.id.popup_info:
//                Toast.makeText(this, "Info", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.popup_use_si:
//                item.setChecked(!item.isChecked());
//                break;
//        }
////        popupMenu.show()
//        return true;
////        return false;
//    }
}
