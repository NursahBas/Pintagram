package com.example.pintagramodev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import Cerceve.AramaFragment;
import Cerceve.BildirimFragment;
import Cerceve.HomeFragment;
import Cerceve.ProfilFragment;

public class AnaSayfaActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment seciliCerceve = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,new HomeFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


            switch(menuItem.getItemId()){
                case R.id.nav_home:
                    //ana çerçeveyi çağırsın
                    seciliCerceve = new HomeFragment();

                    break;
                case R.id.nav_arama:
                    //arama çerçeveyi çağırsın
                    seciliCerceve = new AramaFragment();
                    break;

                case R.id.nav_ekle:
                    //çerçeve boş olsun sonra Gonderi activtye gitsin
                    seciliCerceve  =null;
                    startActivity(new Intent(AnaSayfaActivity.this,GonderiActivity.class));
                    break;

                case R.id.nav_kalp:
                    //bildirim çerçevesini çağırsın
                    seciliCerceve = new BildirimFragment();
                    break;

                case R.id.nav_profil:
                    //profil çerçevesini çağırsın
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor.putString("profiled" , FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    seciliCerceve  = new ProfilFragment();
                    break;

            }
            if(seciliCerceve !=null){

                getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,seciliCerceve).commit();

            }
            return true;
        }
    };
}
