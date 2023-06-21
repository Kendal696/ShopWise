package com.example.shopwise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CrearListaActivity extends AppCompatActivity {
    EditText etNombreLista;
    Button btnGuardarLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_lista);

        etNombreLista = findViewById(R.id.etNombreLista);
        btnGuardarLista = findViewById(R.id.btnGuardarLista);

        btnGuardarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreLista = etNombreLista.getText().toString().trim();
                if (!nombreLista.isEmpty()) {
                    Intent intent = new Intent();
                    intent.putExtra("nombreLista", nombreLista);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(CrearListaActivity.this, "Ingrese un nombre de lista válido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


