package Cerceve;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.pintagramodev.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.KullaniciAdapter;
import Model.Kullanici;

/**
 * A simple {@link Fragment} subclass.
 */
public class AramaFragment extends Fragment {


    private RecyclerView recyclerView ;
    private KullaniciAdapter kullaniciAdapter;
    private List<Kullanici> mKullaniciler;

    EditText arama_bar;

    public AramaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_arama, container, false);

       recyclerView = view.findViewById(R.id.recyler_view_Arama);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


       arama_bar= view.findViewById(R.id.edt_arama_bar);

       mKullaniciler=new ArrayList<>();
       kullaniciAdapter = new KullaniciAdapter(getContext(),mKullaniciler,true);

       recyclerView.setAdapter(kullaniciAdapter);
       //kullanıcı adaptörüde recylerview e bağladım

        kullanicileriOku(); //aramaya bir şey yazmadığımızda direk okuyor

        arama_bar.addTextChangedListener(new TextWatcher() {
            //aramaya bir şey yazdığımızda ona göre sonuçlar çıkartacak
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kullaniciAra(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

       return view;
    }
    private  void kullaniciAra(String s) {
        Query sorgu = FirebaseDatabase.getInstance().getReference("Kullanıcılar").orderByChild("kullaniciadi")
                .startAt(s)
                .endAt(s + "\uf8ff");
        //aradığımızı çağırıyor

        sorgu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mKullaniciler.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Kullanici kullanici = snapshot.getValue(Kullanici.class);
                    mKullaniciler.add(kullanici);
                    //veritabanından verileri alıp snapshota ekledik onuda kullaniciye onuda listeye aktardık

                }
                kullaniciAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void kullanicileriOku()
    {
        DatabaseReference  kullanicilerYolu = FirebaseDatabase.getInstance().getReference("Kullanıcılar");

        kullanicilerYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( arama_bar.getText().toString().equals("")) //her şeyi yansıtmak için boş yazdık
                    //arama yeri eğer boşsa hepsini çağırıyor
                {
                    mKullaniciler.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Kullanici kullanici = snapshot.getValue(Kullanici.class);
                        mKullaniciler.add(kullanici); //buraya ekliyoruz

                    }
                    kullaniciAdapter.notifyDataSetChanged();  //her türlü değişiklik hemen yansısın diye
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
