package com.example.pintagramodev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaslangicActivity extends AppCompatActivity {

    Button btn_baslangicGiris;
    Button getBtn_baslangicKaydol;

    FirebaseUser baslangicKullanici;

    @Override
    protected void onStart() {
        super.onStart(); //başladığında demel oncreate oluşturulduğunda demek

        baslangicKullanici= FirebaseAuth.getInstance().getCurrentUser();

        //Eğer veri tabanında varsa direkt anasayfaya göndder

        if(baslangicKullanici != null){
            startActivity(new Intent(BaslangicActivity.this,AnaSayfaActivity.class));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bslangic);

        btn_baslangicGiris=findViewById(R.id.btn_giris);
        getBtn_baslangicKaydol=findViewById(R.id.btn_kaydol);

        btn_baslangicGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaslangicActivity.this,GirisActivity.class));

            }
        });
        getBtn_baslangicKaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaslangicActivity.this,KaydolActivity.class));

            }
        });

    }
}
