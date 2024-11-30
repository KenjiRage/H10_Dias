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
            array[i] = i;
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

        // Calcular días de vacaciones
        double diasVacaciones = (diasTrabajados / 30.0) * 2.5;

        // Sumar días rojos
        double diasAdicionales = Math.ceil(diasVacaciones) + diasRojos;

        // Calcular días adicionales por descanso semanal
        if (diasDescanso == 1) {
            long semanas = diasTrabajados / 7;
            diasAdicionales += semanas;
        }

        // Calcular la fecha de baja
        Calendar fechaBaja = (Calendar) fechaFinal.clone();
        fechaBaja.add(Calendar.DAY_OF_MONTH, (int) Math.ceil(diasAdicionales));

        // Mostrar resultado
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

// Condición para mostrar los días adicionales dependiendo del descanso semanal
        String descansoInfo;
        if (diasDescanso == 1) {
            descansoInfo = diasAdicionales + " días adicionales por haber librado 1 día semanal.";
        } else {
            descansoInfo = "0 días adicionales por haber librado 2 días semanales.";
        }

// Mensaje final con todos los detalles
        String resultado = "El camarero " + nombre +
                " ha generado " + descansoInfo +
                " Tiene " + Math.ceil(diasVacaciones) + " días de vacaciones, " +
                "así que en total este camarero estará de baja el día " +
                sdf.format(fechaBaja.getTime()) + ".";

// Mostrar resultado en el TextView
        tvResultado.setText(resultado);
    }
}
