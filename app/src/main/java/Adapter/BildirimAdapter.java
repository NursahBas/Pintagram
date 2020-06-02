package Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import javax.net.ssl.SSLSessionBindingListener;

import Cerceve.GonderiDetayiFragment;
import Cerceve.ProfilFragment;
import Model.Bildirim;
import Model.Gonderi;
import Model.Kullanici;

public class BildirimAdapter extends  RecyclerView.Adapter<BildirimAdapter.ViewHolder> {

    private Context mContext;
    private List<Bildirim> mBildirim ;

    public BildirimAdapter(Context mContext, List<Bildirim> mBildirim) {
        this.mContext = mContext;
        this.mBildirim = mBildirim;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bildirim_ogesi,viewGroup,false);
        return new BildirimAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder , int i) {

        final Bildirim bildirim  = mBildirim.get(i);

        kullaniciBilgisiAl(viewHolder.profil_resmi,viewHolder.txt_kullaniciadi,bildirim.getKullaniciid());

        if(bildirim.isIspost())
        {
            //eğer bir gönderi gönderilmişse
            viewHolder.gonderi_resmi.setVisibility(View.VISIBLE);
            gonderiresmiAl(viewHolder.gonderi_resmi,bildirim.getGonderiid());
        }
        else
        {
            viewHolder.gonderi_resmi.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bildirim.isIspost())
                {
                    SharedPreferences.Editor editor  = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("postid",bildirim.getGonderiid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                            new GonderiDetayiFragment()).commit();
                }
                else
                {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",bildirim.getKullaniciid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                            new ProfilFragment()).commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBildirim.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView profil_resmi , gonderi_resmi;
        public TextView txt_kullaniciadi;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Resimler
            profil_resmi = itemView.findViewById(R.id.profil_resmi_bildirimOgesi);
           gonderi_resmi = itemView.findViewById(R.id.gonderi_resmi_bildirimOgesi);

            //Textler
            txt_kullaniciadi = itemView.findViewById(R.id.txt_kullaniciadi_bildirimOgesi);

        }
    }

    private void kullaniciBilgisiAl(final ImageView imageView , final TextView kullaniciadi , String gonderenid)
    {
        DatabaseReference kullaniciYolu = FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(gonderenid);

        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                Glide.with(mContext).load(kullanici.getResimurl()).into(imageView);
                kullaniciadi.setText(kullanici.getKullaniciadi());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void gonderiresmiAl (final ImageView imageView , final String gonderiId)
    {
        DatabaseReference gonderiYolu = FirebaseDatabase.getInstance().getReference("Gonderiler").child(gonderiId);
        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Gonderi gonderi =dataSnapshot.getValue(Gonderi.class);

                Glide.with(mContext).load(gonderi.getGonderiResmi()).into(imageView);

            }@Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
