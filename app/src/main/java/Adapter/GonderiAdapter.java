package Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pintagramodev.R;
import com.example.pintagramodev.TakipcilerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import Cerceve.GonderiDetayiFragment;
import Cerceve.ProfilFragment;
import Model.Gonderi;
import Model.Kullanici;

public class GonderiAdapter extends  RecyclerView.Adapter<GonderiAdapter.ViewHolder> {

    public Context mContext;
    public List<Gonderi> mGonderi;

    private FirebaseUser mevcutFirebaseUser;
    //o anda işlem yapan demek


    public GonderiAdapter(Context mContext, List<Gonderi> mGonderi) {
        this.mContext = mContext;
        this.mGonderi = mGonderi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.gonderi_ogesi,viewGroup,false);

        return new GonderiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        mevcutFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Gonderi gonderi = mGonderi.get(i);

        FirebaseDatabase.getInstance().getReference().child("Gonderiler")
                .child(mevcutFirebaseUser.getUid()).child(gonderi.getGonderiId()).setValue(true);


        //resmi alıyoruz şimdide
        Glide.with(mContext).load(gonderi.getGonderiResmi()).into(viewHolder.gonderi_resmi);

        if(gonderi.getGonderiHakkinda().equals(""))
        {
            viewHolder.txt_gonderiHakkinda.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.txt_gonderiHakkinda.setVisibility(View.VISIBLE);
            viewHolder.txt_gonderiHakkinda.setText(gonderi.getGonderiHakkinda());

        }




        gonderenBilgileri(viewHolder.profil_resmi,viewHolder.txt_kullanici_adi,viewHolder.txt_gonderen,gonderi.getGonderen());
        //beğenileri çağırıcam şimdi
        begenildi(gonderi.getGonderiId(),viewHolder.begeni_resmi);
        begeniSayisi(viewHolder.txt_begeni,gonderi.getGonderiId());
        kaydedildi(gonderi.getGonderiId(),viewHolder.kaydetme_resmi);

        //Gönderi Detayı görmek için profil resmine tıklama olayı
        viewHolder.profil_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();
                //burda tıklayınca yeni çerçeve gelecek profil çerçevesine gidecek



            }
        });

        //kullanıcı adı tıklandığındada yukarıdakiyle aynı yere gitsin
        viewHolder.txt_kullanici_adi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();
                //burda tıklayınca yeni çerçeve gelecek profil çerçevesine gidecek



            }
        });
//gonderen adının üzerine tıklandığındada yukarıdakilerle aynı yere gitsin
        viewHolder.txt_gonderen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();
                //burda tıklayınca yeni çerçeve gelecek profil çerçevesine gidecek



            }
        });
//bu seferde gönderi resmi tıklandığında GonderiDetayıFragmenta göndersin
        viewHolder.gonderi_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",gonderi.getGonderiId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new GonderiDetayiFragment()).commit();
                //burda tıklayınca yeni çerçeve gelecek profil çerçevesine gidecek



            }
        });

        //kaydetme resmi tıklama olayı
        viewHolder.kaydetme_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.kaydetme_resmi.getTag().equals("kaydet"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Kaydedilenler")
                            .child(mevcutFirebaseUser.getUid()).child(gonderi.getGonderiId()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Kaydedilenler").child(mevcutFirebaseUser.getUid())
                            .child(gonderi.getGonderiId()).removeValue();
                }

            }
        });

        //beğeni resmi tıklama olayı
        viewHolder.begeni_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( viewHolder.begeni_resmi.getTag().equals("beğen"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).setValue(true);

                    //bildirim alırken
                    bildirimleriEkle(gonderi.getGonderen(),gonderi.getGonderiId());
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).removeValue();
                }
            }

        });
 //TakipcilerActivity ile bağlıyorum burayı beğeniler textine tıkladığımda bunlar olacak

        viewHolder.txt_begeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, TakipcilerActivity.class);
                intent.putExtra("id",gonderi.getGonderiId());
                intent.putExtra("baslik","begeniler");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGonderi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //gonderiogesi itemlerini alıyoruz burda 5 tane imageview ı buraya yerleştiriyoruz

        public ImageView profil_resmi,gonderi_resmi,begeni_resmi,kaydetme_resmi;

        public TextView txt_kullanici_adi,txt_begeni,txt_gonderen,txt_gonderiHakkinda;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profil_resmi = itemView.findViewById(R.id.profil_resmi_Gonderi_Ogesi);
            gonderi_resmi = itemView.findViewById(R.id.gonderi_Resmi_Gonderi_Ogesi);
            begeni_resmi = itemView.findViewById(R.id.begeni_Gonderi_Ogesi);
            kaydetme_resmi = itemView.findViewById(R.id.kaydet_Gonderi_Ogesi);


            txt_kullanici_adi = itemView.findViewById(R.id.txt_kullaniciadi_Gonderi_Ogesi);
            txt_begeni = itemView.findViewById(R.id.txt_begeniler_Gonderi_Ogesi);
            txt_gonderen= itemView.findViewById(R.id.txt_gonderen_Gonderi_Ogesi);
            txt_gonderiHakkinda = itemView.findViewById(R.id.txt_gonderiHakkında_Gonderi_Ogesi);
        }
    }

    private void begenildi(String gonderiId , final ImageView imageView)
    {
        final FirebaseUser mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference begeniVeriTabanıYolu = FirebaseDatabase.getInstance().getReference()
                .child("Begeniler")
                .child(gonderiId);

        begeniVeriTabanıYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(mevcutKullanici.getUid()).exists())
                {
                    imageView.setImageResource(R.drawable.ic_begenildi);
                    imageView.setTag("beğenildi");
                }
                else
                {
                    imageView.setImageResource(R.drawable.ic_begeni);
                    imageView.setTag("beğen");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void begeniSayisi(final TextView begeniler , String gonderiId)
    {
        DatabaseReference begeniSayisiVeriTabaniYolu= FirebaseDatabase.getInstance().getReference()
                .child("Begeniler")
                .child(gonderiId);

        begeniSayisiVeriTabaniYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                begeniler.setText(dataSnapshot.getChildrenCount()+" beğeni");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gonderenBilgileri(final ImageView profil_resmi, final TextView kullaniciadi, final TextView gonderen , final String kullaniciId)
    {
        DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(kullaniciId);

        veriYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);

                Glide.with(mContext).load(kullanici.getResimurl()).into(profil_resmi);
                kullaniciadi.setText(kullanici.getKullaniciadi());
                gonderen.setText(kullanici.getKullaniciadi());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void kaydedildi (final String gonderiId , final ImageView imageView)
    {
       FirebaseUser mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();

       DatabaseReference kaydetmeYolu = FirebaseDatabase.getInstance().getReference().child("Kaydedilenler")
               .child(mevcutKullanici.getUid());

       kaydetmeYolu.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.child(gonderiId).exists())
               {
                   imageView.setImageResource(R.drawable.ic_kaydedildi);
                   imageView.setTag("kaydedildi");
               }
               else
               {
                   imageView.setImageResource(R.drawable.ic_kaydet);
                   imageView.setTag("kaydet");
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
    private  void bildirimleriEkle(String kullaniciId , String gonderiId)
    {
        DatabaseReference bildirimEklemeYolu = FirebaseDatabase.getInstance().getReference("Bildirimler").child(kullaniciId);

        HashMap<String ,Object>hashMap = new HashMap<>();
        hashMap.put("kullaniciid",mevcutFirebaseUser.getUid());
        hashMap.put("text", "gönderini beğendi");
        hashMap.put("gonderiid",gonderiId);
        hashMap.put("ispost",true);

        bildirimEklemeYolu.push().setValue(hashMap);

    }


}
