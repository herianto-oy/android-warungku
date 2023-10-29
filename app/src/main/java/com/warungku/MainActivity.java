package com.warungku;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.warungku.ui.auth.login.LoginActivity;
import com.warungku.utils.retrofit.SessionManager;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_pos, R.id.navigation_transaction, R.id.navigation_product, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        checkSession();

        if (getIntent().getExtras() != null){
            if(getIntent().getExtras().getString("state", "").equals("product")){
                NavInflater navInflater = navController.getNavInflater();
                NavGraph graph = navInflater.inflate(R.navigation.mobile_navigation);
                graph.setStartDestination(R.id.navigation_product);
                navController.setGraph(graph);
            }
        }
    }

    public void checkSession(){
        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}