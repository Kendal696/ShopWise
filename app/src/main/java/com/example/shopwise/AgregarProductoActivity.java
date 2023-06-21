package com.example.shopwise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AgregarProductoActivity extends AppCompatActivity {
    EditText etNombreProducto, etCantidadProducto, etPrecioProducto;
    Button btnGuardarProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        etNombreProducto = findViewById(R.id.etNombreProducto);
        etCantidadProducto = findViewById(R.id.etCantidadProducto);
        etPrecioProducto = findViewById(R.id.etPrecioProducto);
        btnGuardarProducto = findViewById(R.id.btnGuardarProducto);

        btnGuardarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreProducto = etNombreProducto.getText().toString().trim();
                int cantidadProducto = Integer.parseInt(etCantidadProducto.getText().toString().trim());
                double precioProducto = Double.parseDouble(etPrecioProducto.getText().toString().trim());

                Intent intent = new Intent();
                intent.putExtra("nombreProducto", nombreProducto);
                intent.putExtra("cantidadProducto", cantidadProducto);
                intent.putExtra("precioProducto", precioProducto);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
