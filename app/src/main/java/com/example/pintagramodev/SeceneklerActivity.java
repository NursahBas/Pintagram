package com.example.pintagramodev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SeceneklerActivity extends AppCompatActivity {

    TextView txt_cikisYap , txt_ayarlar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secenekler);

        txt_cikisYap = findViewById(R.id.txt_cikisyap_seceneklerActivity);
        txt_ayarlar= findViewById(R.id.txt_ayarlar_seceneklerActivity);

        //Toolbar ayarlamları
        Toolbar toolbar = findViewById(R.id.toolbar_seceneklerActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Seçenekler");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_cikisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SeceneklerActivity.this,BaslangicActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }
}
