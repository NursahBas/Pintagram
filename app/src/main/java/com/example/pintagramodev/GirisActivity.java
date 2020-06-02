package com.example.pintagramodev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GirisActivity extends AppCompatActivity {


    EditText edt_email_Giris , edt_Sifre_Giris;

    Button btn_giris_Yap;

    TextView txt_kayitSayfasina_Git;

    FirebaseAuth girisauth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        edt_email_Giris=findViewById(R.id.edt_Email_giris);
        edt_Sifre_Giris=findViewById(R.id.edt_Sifre_giris);

        btn_giris_Yap=findViewById(R.id.btn_Giris_activity);

       txt_kayitSayfasina_Git=findViewById(R.id.txt_kayitSayfasina_git);

       txt_kayitSayfasina_Git.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(GirisActivity.this,KaydolActivity.class));

           }
       });

       btn_giris_Yap.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //Giriş yapma kodları

               String str_emailGiris = edt_email_Giris.getText().toString();
               String str_sifreGiris = edt_Sifre_Giris.getText().toString();

               if(TextUtils.isEmpty(str_emailGiris)||TextUtils.isEmpty(str_sifreGiris)){
                   Toast.makeText(GirisActivity.this,"Bütün alanları doldurunuz..", Toast.LENGTH_LONG).show();

               }else{
                   //giriş yapma kodları

                   girisauth.signInWithEmailAndPassword(str_emailGiris,str_sifreGiris).addOnCompleteListener(GirisActivity.this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               DatabaseReference  yolGiris= FirebaseDatabase.getInstance().getReference().child("Kullanıcılar").child(girisauth.getCurrentUser().getUid());

                               yolGiris.addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       Intent intent = new Intent (GirisActivity.this,AnaSayfaActivity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                       finish(); //tekrar başa dönmemesi için
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });
                           } else {
                               Toast.makeText(GirisActivity.this,"giriş başarısız oldu ",Toast.LENGTH_LONG).show();
                           }
                       }
                   });
               }
           }
       });

    }
}
