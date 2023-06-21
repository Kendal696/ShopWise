package com.example.shopwise;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout layoutListas;
    private Button btnNuevaLista;
    private Button btnBuscarProductos;

    private List<String> listas;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ListasPrefs";
    private static final String KEY_LISTAS = "listas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutListas = findViewById(R.id.layoutListas);
        btnNuevaLista = findViewById(R.id.btnNuevaLista);
        btnBuscarProductos = findViewById(R.id.btnBuscarProductos);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        btnNuevaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CrearListaActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        btnBuscarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BusquedaActivity.class);
                startActivity(intent);
            }
        });

        // Recuperar las listas guardadas
        recuperarListas();
        // Mostrar las listas
        mostrarListas();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verificar si se creó una nueva lista
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String nombreLista = data.getStringExtra("nombreLista");
            listas.add(nombreLista);
            // Guardar las listas
            guardarListas();
            // Mostrar las listas
            mostrarListas();
        }
    }

    private void guardarListas() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String listasJson = gson.toJson(listas);
        editor.putString(KEY_LISTAS, listasJson);
        editor.apply();
    }

    private void recuperarListas() {
        String listasJson = sharedPreferences.getString(KEY_LISTAS, null);
        if (listasJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            listas = gson.fromJson(listasJson, type);
        } else {
            listas = new ArrayList<>();
        }
    }

    private void mostrarListas() {
        layoutListas.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (final String lista : listas) {
            View listItem = inflater.inflate(R.layout.lista_item, layoutListas, false);
            Button btnLista = listItem.findViewById(R.id.btnLista);
            ImageButton btnEliminar = listItem.findViewById(R.id.btnEliminar);

            btnLista.setText(lista);

            btnLista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ListaProductosActivity.class);
                    intent.putExtra("nombreLista", lista);
                    startActivity(intent);
                }
            });

            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarLista(lista);
                }
            });

            layoutListas.addView(listItem);
        }
    }

    private void eliminarLista(String lista) {
        listas.remove(lista);
        // Guardar las listas actualizadas
        guardarListas();
        // Mostrar las listas actualizadas
        mostrarListas();
    }
}












