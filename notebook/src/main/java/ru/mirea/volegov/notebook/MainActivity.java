package ru.mirea.volegov.notebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import ru.mirea.volegov.notebook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isExternalStorageWritable()) {
                    Snackbar.make(MainActivity.this.getCurrentFocus(),"доступ запрещён", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            writeFileToExternalStorage();
                        }
                    }).start();
                }
                Snackbar.make(MainActivity.this.getCurrentFocus(), "сохранено", Snackbar.LENGTH_SHORT).show();
            }
        });
        binding.buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isExternalStorageReadable())
                {
                    Snackbar.make(MainActivity.this.getCurrentFocus(),"доступ запрещён", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            readFileFromExternalStorage();
                        }
                    }).start();
                }
                Snackbar.make(MainActivity.this.getCurrentFocus(),"открыто", Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    public void writeFileToExternalStorage() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, String.format("%s.txt", binding.textName.getText().toString()));
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsoluteFile());
            OutputStreamWriter output = new OutputStreamWriter(fileOutputStream);
            output.write(binding.textInputText.getText().toString());
            output.close();
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }
    public void readFileFromExternalStorage() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, String.format("%s.txt",binding.textName.getText().toString()));
        try {
            FileInputStream fileInputStream = new FileInputStream(file.getAbsoluteFile());
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            List<String> lines = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.textInputText.setText(lines.toString());
                }
            });

            Log.w("ExternalStorage", String.format("Read from file %s successful", lines.toString()));
        } catch (Exception e) {
            Log.w("ExternalStorage", String.format("Read from file %s failed", e.getMessage()));
        }
    }



    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}