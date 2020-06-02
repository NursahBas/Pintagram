package com.example.pintagramodev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class GonderiActivity extends AppCompatActivity {

    Uri resimUri;
    String benimUrim="";

    StorageTask yuklemeGorevi;
    StorageReference resimYukleYolu;

    ImageView image_Kapat , image_Eklendi;
   TextView txt_Gonder;
   EditText edt_Gonderi_Hakkında;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gonderi);

        image_Kapat= findViewById(R.id.close_Gonderi);
        image_Eklendi= findViewById(R.id.eklenen_Resim_Gonderi);

        txt_Gonder= findViewById(R.id.txt_Gonder);

        edt_Gonderi_Hakkında= findViewById(R.id.edt_Gonderi_Hakkinda);

        resimYukleYolu  = FirebaseStorage.getInstance().getReference("gonderiler");

        image_Kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GonderiActivity.this,AnaSayfaActivity.class));
                finish();
            }
        });

        txt_Gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resimYukle();
            }
        });

        //resmi kırpma özelliği

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(GonderiActivity.this);
    }

    private String dosyaUzantisiAl(Uri uri)
    {
        ContentResolver contentResolver  = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }
    private void resimYukle() {
        //resim yükleme kodları

        if(resimUri != null)
        {
            final StorageReference dosyaYolu = resimYukleYolu.child(System.currentTimeMillis()
                    +"."+dosyaUzantisiAl(resimUri));

            yuklemeGorevi =  dosyaYolu.putFile(resimUri);
            yuklemeGorevi.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw  task.getException();
                        //throw artsın demek burda görev artsın anlamında kullandık
                    }

                    return dosyaYolu.getDownloadUrl() ;

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri indirmeUrisi = task.getResult();
                        benimUrim = indirmeUrisi.toString();

                        DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");

                        String gonderiId = veriYolu.push().getKey();

                        HashMap<String,Object> hashMap = new HashMap<>();

                        hashMap.put("gonderiId",gonderiId);
                        hashMap.put("gonderiResmi",benimUrim);
                        hashMap.put("gonderiHakkinda",edt_Gonderi_Hakkında.getText().toString());
                        hashMap.put("gonderen", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        veriYolu.child(gonderiId).setValue(hashMap);

                        startActivity(new Intent(GonderiActivity.this,AnaSayfaActivity.class));
                        finish();
                        //buraya kadar ekleme başarılı ise durumlarıydı
                    }
                    else
                    {
                        Toast.makeText(GonderiActivity.this,"Gonderme Başarısız...",Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GonderiActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        //buraya kadar resimUri si boş değilse tüm bunları yapacaktı
        //ama boşsa... şimdi onu yazıyoruz
     else
        {
            Toast.makeText(this,"Seçilen resim yok !",Toast.LENGTH_SHORT).show();

        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resimUri = result.getUri();
            //buraya resmi aldım Uri ye şimdi onu ımage View a yüklemem gerekiyor

            image_Eklendi.setImageURI(resimUri);
        }
        else
        {
            Toast.makeText(this, "Resim seçilemedi",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GonderiActivity.this,AnaSayfaActivity.class));
            finish();
        }
    }


}
