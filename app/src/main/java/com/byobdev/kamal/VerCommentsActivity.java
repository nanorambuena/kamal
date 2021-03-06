package com.byobdev.kamal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byobdev.kamal.DBClasses.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.byobdev.kamal.R.id.delete;

/**
 * Created by crono on 08-10-17.
 */

public class VerCommentsActivity extends AppCompatActivity implements ListVerCommentActivity.customButtonListener {
    private DatabaseReference mDatabase;
    protected static final int DIALOG_REMOVE_CALC = 1;
    protected static final int DIALOG_REMOVE_PERSON = 2;
    String[] completarLista;
    String[] SectorLista;
    String[] keyLista;
    String[] descriptionLista;
    String[] imageLista;
    String[] respuesaLista;
    String[] completarListaAux;
    String[] SectorListaAux;
    String[] keyListaAux;
    String[] descriptionListaAux;
    String[] imageListaAux;
    String[] respuesaListaAux;
    ListView lista;
    String Key;
    ImageButton sendCom;
    EditText Comentario;
    int position;
    FirebaseUser currentUser;
    int prevPosition=-1;
    boolean selected=false, organizador = false;
    MenuItem edit;
    int ORG=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_comments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConfigComments);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        Intent i=getIntent();
        getSupportActionBar().setTitle(i.getStringExtra("Titulo"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.textLightPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        final RelativeLayout ll = (RelativeLayout) findViewById(R.id.verComments);
        FirebaseAuth.AuthStateListener authListener  = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser  = firebaseAuth.getCurrentUser();

            }
        };

        FirebaseAuth.getInstance().addAuthStateListener(authListener);
        sendCom = (ImageButton) findViewById(R.id.VerSendComment);
        sendCom.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                SendComment();
            }
        });
        Comentario = (EditText) findViewById(R.id.VerCommentInput);
        lista = (ListView) findViewById(R.id.verCommentList);
        mDatabase = FirebaseDatabase.getInstance().getReference("Comments").child(i.getStringExtra("IDIniciativa"));
        final int[] k = {0};
        k[0] = 0;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int t=0;
                int aux=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    t++;
                    if(t>1){
                        k[0] = 1;
                    }

                }
                SectorLista = new String[t-1];
                completarLista = new String[t-1];
                keyLista = new String[t-1];
                descriptionLista = new String[t-1];
                imageLista = new String[t-1];
                respuesaLista = new String[t-1];
                aux =t-1;
                SectorListaAux = new String[t-1];
                completarListaAux = new String[t-1];
                keyListaAux = new String[t-1];
                descriptionListaAux = new String[t-1];
                imageListaAux = new String[t-1];
                respuesaListaAux = new String[t-1];
                t=0;
                for (final DataSnapshot child : snapshot.getChildren()) {
                    // Create a LinearLayout element
                    if(child.child("Comentario").getValue().toString().equals("Creador")){
                        if(currentUser != null){
                            if(currentUser.getDisplayName().equals(child.child("Nombre").getValue().toString())){
                                organizador = true;
                                ORG=1;
                            }
                        }


                    }else{
                        respuesaLista[t] = child.child("Respuesta").getValue().toString();
                        SectorLista[t] = child.getKey();
                        completarLista[t] = child.child("Nombre").getValue().toString();
                        keyLista[t] = child.getKey().toString();
                        descriptionLista[t] = child.child("Comentario").getValue().toString();
                        imageLista[t] = child.child("Image").getValue().toString();
                        t++;
                    }

                }

                if(currentUser == null){
                    sendCom.setVisibility(View.GONE);
                    Comentario.setVisibility(View.GONE);
                }
                else{
                    sendCom.setVisibility(View.VISIBLE);
                    Comentario.setVisibility(View.VISIBLE);
                }
                for(int i=0;i<aux;i++){
                    SectorListaAux[i] = SectorLista[t-1];
                    completarListaAux[i] = completarLista[t-1];
                    keyListaAux[i] = keyLista[t-1];
                    descriptionListaAux[i] = descriptionLista[t-1];
                    imageListaAux[i] = imageLista[t-1];
                    respuesaListaAux[i] =respuesaLista[t-1];
                    t--;
                }
                if(k[0] ==0){
                    TextView noHay = new TextView(VerCommentsActivity.this);
                    noHay.setText("No existen consultas sobre esta iniciativa");
                    noHay.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    ll.addView(noHay);
                }else{
                    com.byobdev.kamal.ListVerCommentActivity adapter;
                    ArrayList<String> dataItems = new ArrayList<String>();
                    List<String> dataTemp = Arrays.asList(completarListaAux);
                    dataItems.addAll(dataTemp);
                    adapter = new com.byobdev.kamal.ListVerCommentActivity(VerCommentsActivity.this, dataItems,keyListaAux,descriptionListaAux,imageListaAux,lista, respuesaListaAux, ORG);
                    adapter.setCustomButtonListner(VerCommentsActivity.this);
                    lista.setAdapter(adapter);
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });



    }

    public void SendComment(){

        mDatabase = FirebaseDatabase.getInstance().getReference("Comments/");
        Key=mDatabase.push().getKey();

        DatabaseReference comments = FirebaseDatabase.getInstance().getReference("Comments/");
        Comment comment = new Comment(currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), Comentario.getText().toString(), "");
        comments.child(getIntent().getStringExtra("IDIniciativa")).child(Key).setValue(comment);
        Comentario.setText("");
        Toast.makeText(getApplicationContext(), "Comentario enviado", Toast.LENGTH_LONG).show();
        finish();
        startActivity(getIntent());
    }


    @Override
    public void setRespuesta(int position) {
        Responder1(position);
    }

    public void Responder1(final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(VerCommentsActivity.this);
        LayoutInflater inflater = VerCommentsActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        alert.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        alert.setTitle("Respuesta");
        alert.setMessage("Escribe una respuesta a la consulta");
        alert.setPositiveButton("Aceptar", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = getIntent();
                DatabaseReference Respuesta = FirebaseDatabase.getInstance().getReference("Comments/"+i.getStringExtra("IDIniciativa")+"/"+SectorListaAux[position]);
                Respuesta.child("Respuesta").setValue(edt.getText().toString());
                finish();
                startActivity(getIntent());
                dialog.dismiss();


            }
        });
        alert.setNegativeButton("Cancelar", new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
