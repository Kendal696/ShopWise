package com.example.shopwise;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListaProductosActivity extends AppCompatActivity {
    TextView tvNombreLista;
    LinearLayout layoutProductos;
    Button btnAgregarProducto, btnOrdenarPorPrecio, btnOrdenarPorCantidad;
    String nombreLista;
    List<Producto> productos = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ProductosPrefs";
    private static final String KEY_PRODUCTOS = "productos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);

        tvNombreLista = findViewById(R.id.tvNombreLista);
        layoutProductos = findViewById(R.id.layoutProductos);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);
        btnOrdenarPorPrecio = findViewById(R.id.btnOrdenarPorPrecio);
        btnOrdenarPorCantidad = findViewById(R.id.btnOrdenarPorCantidad);

        nombreLista = getIntent().getStringExtra("nombreLista");
        tvNombreLista.setText(nombreLista.toUpperCase());

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaProductosActivity.this, AgregarProductoActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        btnOrdenarPorPrecio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ordenarPorPrecio();
            }
        });

        btnOrdenarPorCantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ordenarPorCantidad();
            }
        });

        // Recuperar los productos de la lista seleccionada
        recuperarProductos();
        // Mostrar los productos de la lista
        mostrarProductos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verificar si se agregó un nuevo producto
        if (requestCode == 2 && resultCode == RESULT_OK) {
            String nombreProducto = data.getStringExtra("nombreProducto");
            int cantidadProducto = data.getIntExtra("cantidadProducto", 0);
            double precioProducto = data.getDoubleExtra("precioProducto", 0.0);

            Producto producto = new Producto(nombreProducto, cantidadProducto, precioProducto);
            productos.add(producto);
            // Guardar los productos de la lista
            guardarProductos();
            // Mostrar los productos de la lista
            mostrarProductos();
        }
    }

    private void guardarProductos() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String productosJson = gson.toJson(productos);
        editor.putString(KEY_PRODUCTOS + nombreLista, productosJson);
        editor.apply();
    }

    private void recuperarProductos() {
        String productosJson = sharedPreferences.getString(KEY_PRODUCTOS + nombreLista, null);
        if (productosJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Producto>>() {}.getType();
            productos = gson.fromJson(productosJson, type);
        }
    }

    private void mostrarProductos() {
        layoutProductos.removeAllViews();

        for (final Producto producto : productos) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvNombre = new TextView(this);
            tvNombre.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            ));
            tvNombre.setText(producto.getNombre());

            TextView tvCantidad = new TextView(this);
            tvCantidad.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            ));
            tvCantidad.setText(String.valueOf(producto.getCantidad()));

            TextView tvPrecio = new TextView(this);
            tvPrecio.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            ));
            tvPrecio.setText(String.valueOf(producto.getPrecio()) + " Bs");

            CheckBox checkBoxComprado = new CheckBox(this);
            checkBoxComprado.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            ));

            ImageButton btnEliminar = new ImageButton(this);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    dpToPx(50), // Convierte 50dp a píxeles
                    dpToPx(50) // Convierte 50dp a píxeles
            );
            btnEliminar.setLayoutParams(btnParams);
            btnEliminar.setImageResource(R.drawable.ic_baseline_delete_24);
            btnEliminar.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5)); // Establece el relleno interno
            btnEliminar.setScaleType(ImageView.ScaleType.FIT_CENTER); // Ajusta la escala de la imagen
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarProducto(producto);
                }
            });

            itemLayout.addView(tvNombre);
            itemLayout.addView(tvCantidad);
            itemLayout.addView(tvPrecio);
            itemLayout.addView(checkBoxComprado);
            itemLayout.addView(btnEliminar);

            layoutProductos.addView(itemLayout);
        }
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private void ordenarPorPrecio() {
        Collections.sort(productos, new Comparator<Producto>() {
            @Override
            public int compare(Producto p1, Producto p2) {
                return Double.compare(p1.getPrecio(), p2.getPrecio());
            }
        });
        mostrarProductos();
    }

    private void ordenarPorCantidad() {
        Collections.sort(productos, new Comparator<Producto>() {
            @Override
            public int compare(Producto p1, Producto p2) {
                return Integer.compare(p1.getCantidad(), p2.getCantidad());
            }
        });
        mostrarProductos();
    }

    private void eliminarProducto(Producto producto) {
        productos.remove(producto);
        guardarProductos();
        mostrarProductos();
    }
}

