package com.cameraSecurity.geotechappevo;

import android.Manifest;
import android.app.MediaRouteButton;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.camera.view.PreviewView;

import com.cameraSecurity.geotechappevo.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private ActivityMainBinding binding;
    private Button cameraButton;
    private Spinner cameraSpinner;

    private PreviewView cameraView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cameraButton = findViewById(R.id.cameraButton);
        cameraSpinner = findViewById(R.id.cameraSpinner);

        cameraButton.setOnClickListener((View.OnClickListener) this);
        cameraSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // Configura lo spinner per visualizzare le opzioni delle telecamere
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.camera_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cameraSpinner.setAdapter(adapter);

        // Inizializza il provider della fotocamera
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

    }

    @Override
    public void onClick(View view) {
        if (view == cameraButton) {
            // Apri la vista della telecamera corrispondente al pulsante premuto
            //int selectedCameraIndex = cameraSpinner.getSelectedItemPosition();
           // openCameraView(selectedCameraIndex);
            startCamera();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Aggiorna l'interfaccia o gestisci altre azioni quando viene selezionata una telecamera diversa
        Toast.makeText(this, "Telecamera selezionata: " + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this, "Nessuna telecamera selezionata", Toast.LENGTH_SHORT).show();
        // Aggiungi qui la logica per gestire il caso in cui non viene selezionata nessuna telecamera
        // Ad esempio, puoi nascondere la vista della telecamera o visualizzare un messaggio di avviso all'utente
        // In questo esempio, reimpostiamo la vista della telecamera su un valore predefinito o la nascondiamo
        resetCameraView();
    }

    private void resetCameraView() {
        // Aggiungi qui la logica per reimpostare la vista della telecamera su un valore predefinito o nasconderla
        // Ad esempio:
        cameraView.setVisibility(View.GONE);
        // oppure
        // cameraView.setCameraIndex(DEFAULT_CAMERA_INDEX);
    }


   /* private void openCameraView(int cameraIndex) {
        // Implementa la logica per aprire la vista della telecamera Dauha corrispondente all'indice fornito

        // Esempio di implementazione generica per una telecamera Dauha
        Camera dauhaCamera = CameraManager.getCamera(cameraIndex); // Ottenere l'oggetto DauhaCamera corrispondente all'indice
        if (dauhaCamera != null) {
            // Apri la vista della telecamera Dauha
            cameraView.setVisibility(View.VISIBLE);
            cameraView.setDauhaCamera(dauhaCamera);
            dauhaCamera.startPreview();
        } else {
            // Gestisci il caso in cui la telecamera Dauha non è disponibile o non può essere aperta
            Toast.makeText(this, "Impossibile aprire la telecamera Dauha selezionata", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void startCamera(int cameraIndex) {

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Errore nell'avvio della fotocamera", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
        preview.setSurfaceProvider(cameraView.getSurfaceProvider());
    }



}