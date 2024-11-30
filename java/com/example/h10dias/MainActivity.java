package com.example.h10dias;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button btnFechaInicio, btnFechaFinal, btnCalcular;
    private EditText etNombre;
    private Spinner spDiasRojos, spDiasDescanso;
    private TextView tvResultado;

    private Calendar fechaInicio = Calendar.getInstance();
    private Calendar fechaFinal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        etNombre = findViewById(R.id.et_nombre);
        btnFechaInicio = findViewById(R.id.btn_fecha_inicio);
        btnFechaFinal = findViewById(R.id.btn_fecha_final);
        spDiasRojos = findViewById(R.id.sp_dias_rojos);
        spDiasDescanso = findViewById(R.id.sp_dias_descanso);
        btnCalcular = findViewById(R.id.btn_calcular);
        tvResultado = findViewById(R.id.tv_resultado);

        // Configurar los spinners
        configurarSpinners();

        // Configurar botones de selección de fecha
        btnFechaInicio.setOnClickListener(v -> mostrarDatePickerDialog(fechaInicio, btnFechaInicio));
        btnFechaFinal.setOnClickListener(v -> mostrarDatePickerDialog(fechaFinal, btnFechaFinal));

        // Configurar botón de cálculo
        btnCalcular.setOnClickListener(v -> calcularDiasAdicionales());
    }

    private void configurarSpinners() {
        // Opciones para días rojos (0 a 9)
        ArrayAdapter<Integer> adapterDiasRojos = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getDiasArray(0, 9));
        adapterDiasRojos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDiasRojos.setAdapter(adapterDiasRojos);

        // Opciones para días de descanso (1 o 2)
        ArrayAdapter<Integer> adapterDiasDescanso = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new Integer[]{1, 2});
        adapterDiasDescanso.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDiasDescanso.setAdapter(adapterDiasDescanso);
    }

    private Integer[] getDiasArray(int start, int end) {
        Integer[] array = new Integer[end - start + 1];
        for (int i = start; i <= end; i++) {
            array[i - start] = i;
        }
        return array;
    }

    private void mostrarDatePickerDialog(Calendar fecha, Button boton) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    fecha.set(Calendar.YEAR, year);
                    fecha.set(Calendar.MONTH, month);
                    fecha.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    boton.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                fecha.get(Calendar.YEAR),
                fecha.get(Calendar.MONTH),
                fecha.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void calcularDiasAdicionales() {
        String nombre = etNombre.getText().toString();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce el nombre del camarero", Toast.LENGTH_SHORT).show();
            return;
        }

        int diasRojos = (int) spDiasRojos.getSelectedItem();
        int diasDescanso = (int) spDiasDescanso.getSelectedItem();

        // Calcular los días trabajados
        long diffInMillis = fechaFinal.getTimeInMillis() - fechaInicio.getTimeInMillis();
        if (diffInMillis < 0) {
            Toast.makeText(this, "La fecha final debe ser posterior a la fecha de inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        long diasTrabajados = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        // Calcular días de vacaciones (redondeado hacia arriba)
        double diasVacaciones = Math.ceil((diasTrabajados / 30.0) * 2.5);

        // Calcular días adicionales por descanso semanal
        long diasAdicionalesPorDescanso = 0;
        if (diasDescanso == 1) {
            diasAdicionalesPorDescanso = diasTrabajados / 7; // Redondeo hacia abajo
        }

        // Sumar días adicionales totales
        long diasAdicionalesTotales = diasAdicionalesPorDescanso + diasRojos + (long) diasVacaciones;

        // Calcular la fecha de baja
        Calendar fechaBaja = (Calendar) fechaFinal.clone();
        fechaBaja.add(Calendar.DAY_OF_MONTH, (int) diasAdicionalesTotales + 1); // Sumar un día adicional

        // Mostrar resultado
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String resultado = "El camarero " + nombre +
                " ha generado " + diasAdicionalesPorDescanso + " días adicionales por haber librado " + diasDescanso + " día semanal. " +
                "Tiene " + (int) diasVacaciones + " días de vacaciones, " +
                "así que en total este camarero estará de baja el día " +
                sdf.format(fechaBaja.getTime()) + ".";

        tvResultado.setText(resultado);
    }
}
