package Cerceve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pintagramodev.ProfilDuzenle;
import com.example.pintagramodev.R;
import com.example.pintagramodev.SeceneklerActivity;
import com.example.pintagramodev.TakipcilerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Adapter.FotografAdapter;
import Model.Gonderi;
import Model.Kullanici;


public class ProfilFragment extends Fragment {

    ImageView resimSecenekler , profil_resmi ;

    TextView txt_gonderiler , txt_takipciler , txt_takipEdilenler , txt_Ad , txt_bio , txt_kullaniciAdi;

    Button btn_profili_Duzenle;

    ImageView imagebtn_Fotograflarim, imagebtn_kaydedilenFotograflar;

    private List<String> kaydettiklerim ;

    //kaydettiklerimi görme recyclerı
    RecyclerView recyclerViewKaydettiklerim;
    FotografAdapter fotografAdapterKaydettiklerim;
    private List<Gonderi> gonderiList_kaydettiklerim;



//fotoğrafları profilde görme recycler
    RecyclerView recyclerViewFotograflar;
    FotografAdapter fotografAdapter ;
    List<Gonderi> gonderiList ;

    FirebaseUser mevcutKullanici;
    String profilId;





    public ProfilFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profil, container, false);

        mevcutKullanici  = FirebaseAuth.getInstance().getCurrentUser();


        SharedPreferences prefs =getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profilId = prefs.getString("profileid" , "none");
        profilId=mevcutKullanici.getUid();

        resimSecenekler = view.findViewById(R.id.resimSecenekler_profilCercevesi);
        profil_resmi = view.findViewById(R.id.profil_resmi_profilCercevesi);

       txt_gonderiler = view.findViewById(R.id.txt_gonderiler_profilCercevesi);
       txt_takipciler = view.findViewById(R.id.txt_takipciler_profilCercevesi);
       txt_takipEdilenler = view.findViewById(R.id.txt_takipEdilenler_profilCercevesi);
       txt_bio = view.findViewById(R.id.txt_bio_profilCercevesi);
       txt_Ad = view.findViewById(R.id.txt_ad_profilCercevesi);
       txt_kullaniciAdi = view.findViewById(R.id.txt_kullaniciadi_profilCerceve);

       btn_profili_Duzenle = view.findViewById(R.id.btn_profiliDuzenle_profilCercevesi);

       imagebtn_Fotograflarim = view.findViewById(R.id.imagebtn_fotograflarim_profilCercevesi);
       imagebtn_kaydedilenFotograflar = view.findViewById(R.id.imagebtn_kaydedilenFotograflar_profilCercevesi);

       //fotoğrafları profilde görme recycler
       //leyout manager yerleşim yöneticisi
        recyclerViewFotograflar = view.findViewById(R.id.recycler_view_profilCercevesi);
       recyclerViewFotograflar.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerViewFotograflar.setLayoutManager(linearLayoutManager);
        gonderiList = new ArrayList<>();
        fotografAdapter =new FotografAdapter(getContext(),gonderiList);
        recyclerViewFotograflar.setAdapter(fotografAdapter);


        //kaydettiklerimi görme recyclerı
        recyclerViewKaydettiklerim = view.findViewById(R.id.recycler_view_kaydet_profilCercevesi);
        recyclerViewKaydettiklerim.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_Kaydettiklerim = new GridLayoutManager(getContext(),3);
        recyclerViewKaydettiklerim.setLayoutManager(linearLayoutManager_Kaydettiklerim);
        gonderiList_kaydettiklerim =new ArrayList<>();
        fotografAdapterKaydettiklerim = new FotografAdapter(getContext(),gonderiList_kaydettiklerim);
        recyclerViewKaydettiklerim.setAdapter(fotografAdapterKaydettiklerim);

        //profil Layoutta GONE olaak tanımlıydı yani görünmüyordu Vısıble yapıp onu görünür yapıyoruz
        recyclerViewFotograflar.setVisibility(View.VISIBLE); //ilk oluşturduğumda fotoğraflar görünsün
        recyclerViewKaydettiklerim.setVisibility(View.GONE); // kaydettiklerim görünmesin
         //daha sonra kaydettiklerimi visibility yapıcaz





//metotları çağırıyoruz aşağıdaki kullandıklarımızı
        kullaniciBilgisi();
        takipcileriAl();
        gonderiSayısıAl();
        fotograflarim();
        kaydettiklerim();;

        if(profilId.equals(mevcutKullanici.getUid()))
        {
            btn_profili_Duzenle.setText("Profili Düzenle");
        }
        else
        {
            takipKontrolu();
            //takip ediyor muyuz etmiyor muyuz
            imagebtn_kaydedilenFotograflar.setVisibility(View.GONE);
            //değilse kaydedilen fotoğraflar görünmesin
        }


       btn_profili_Duzenle.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String btn = btn_profili_Duzenle.getText().toString();

               if(btn.equals("Profili Düzenle"))
               {
                   //profili düzenlemeye gider (ProfilDuzenle.java ile bağlantı kuruyoruz)
                   startActivity(new Intent(getContext(), ProfilDuzenle.class));


               }
               else if(btn.equals("takip et"))
               {
                   FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                           .child("takipEdilenler").child(profilId).setValue(true);

                   FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                           .child("takipciler").child(mevcutKullanici.getUid()).setValue(true);

                   //bildirim fonk çağırılıyor
                   bildirimleriEkle();

               }
               else if(btn.equals("takip ediliyor"))
               {
                   FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                           .child("takipEdilenler").child(profilId).removeValue();

                   FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                           .child("takipciler").child(mevcutKullanici.getUid()).removeValue();

               }
           }
       });

        //Seçenekler sayfasına git çıkış kısmı
        resimSecenekler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SeceneklerActivity.class);
                startActivity(intent);
            }
        });


        imagebtn_Fotograflarim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewFotograflar.setVisibility(View.VISIBLE);
                recyclerViewKaydettiklerim.setVisibility(View.GONE);
            }
        });
        imagebtn_kaydedilenFotograflar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewFotograflar.setVisibility(View.GONE);
                recyclerViewKaydettiklerim.setVisibility(View.VISIBLE);


            }
        });

        //TakipçilerActivity ile bağlıyoruz

        txt_takipciler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), TakipcilerActivity.class);
                intent.putExtra("id",profilId);
                intent.putExtra("baslik","takipçiler");
                startActivity(intent);

            }
        });

        txt_takipEdilenler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),TakipcilerActivity.class);
                intent.putExtra("id",profilId);
                intent.putExtra("baslik","takip edilenler");
                startActivity(intent);
            }
        });


        return view;

    }
    private void kullaniciBilgisi()
    {
        DatabaseReference kullaniciYolu =FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(profilId);

        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext() == null)
                {
                    return;
                }
                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);

                Glide.with(getContext()).load(kullanici.getResimurl()).into(profil_resmi);
                txt_kullaniciAdi.setText(kullanici.getKullaniciadi());
                        //load ordan aldığı resmi yüklesin demek
                txt_Ad.setText(kullanici.getAd());
                txt_bio.setText(kullanici.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void takipKontrolu()
    {
        DatabaseReference takipYolu = FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                .child("takipEdilenler");

        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profilId).exists())
                {
                    //başkasının profili ise bu butonun textini değiştirsin
                    btn_profili_Duzenle.setText("takip ediliyor");
                }
                else
                {
                    btn_profili_Duzenle.setText("takip et");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void takipcileriAl()
    {
        //takipçi sayısı
        DatabaseReference takipciYolu =FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId).child("takipciler");

        takipciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                txt_takipciler.setText(""+dataSnapshot.getChildrenCount());
                //kaç takipçi varsa onu gösterecek
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // takip edilen sayısı

        DatabaseReference takipEdilenYolu =FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId).child("takipEdilenler");

        takipEdilenYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                txt_takipEdilenler.setText(""+dataSnapshot.getChildrenCount());
                //kaç takip edilenleri varsa onu gösterecek
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void gonderiSayısıAl()
    {
        DatabaseReference gonderiYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");

        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Gonderi gonderi = snapshot.getValue(Gonderi.class);

                    if( gonderi.getGonderen() != null && gonderi.getGonderen().equals(profilId))
                    {
                        i++;
                    }
                }
                txt_gonderiler.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fotograflarim()
    {

        DatabaseReference fotografYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");
        fotografYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiList.clear();

                for(DataSnapshot snapshot  : dataSnapshot.getChildren()){

                    Gonderi gonderi = snapshot.getValue(Gonderi.class);
                    if(gonderi.getGonderen() != null && gonderi.getGonderen().equals(profilId))
                    {
                        gonderiList.add(gonderi);
                    }
                }
                Collections.reverse(gonderiList);
                fotografAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void kaydettiklerim ()
    {
       kaydettiklerim= new ArrayList<>();

       final DatabaseReference kaydettiklerimYolu = FirebaseDatabase.getInstance().getReference("Kaydedilenler")
       .child(mevcutKullanici.getUid());

       kaydettiklerimYolu.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    kaydettiklerim.add(snapshot.getKey());
                }
                kaydettiklerimiOku();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void kaydettiklerimiOku() {

        DatabaseReference gonderidenOku = FirebaseDatabase.getInstance().getReference("Gonderiler");

        gonderidenOku.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiList_kaydettiklerim.clear(); //veriler tekrar etmesin
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Gonderi gonderi = snapshot.getValue(Gonderi.class);

                    for (String id : kaydettiklerim)
                    {
                        if(gonderi.getGonderiId() !=null && gonderi.getGonderiId().equals(id))
                        {
                            gonderiList_kaydettiklerim.add(gonderi);
                        }
                    }
                    fotografAdapterKaydettiklerim.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void bildirimleriEkle()
    {
        DatabaseReference bildirimEklemeYolu = FirebaseDatabase.getInstance().getReference("Bildirimler").child(profilId);

        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciid",mevcutKullanici.getUid());
        hashMap.put("text", "seni takip etmeye başladı");
        hashMap.put("gonderiid","");
        hashMap.put("ispost",false);

        bildirimEklemeYolu.push().setValue(hashMap);

    }
}


