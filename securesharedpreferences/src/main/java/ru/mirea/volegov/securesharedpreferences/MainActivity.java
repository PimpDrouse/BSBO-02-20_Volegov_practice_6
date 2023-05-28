package ru.mirea.volegov.securesharedpreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import ru.mirea.volegov.securesharedpreferences.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String imagePixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 101);
            }
        });


        String mainKeyAlias;
        try {
            mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }


        SharedPreferences secureSharedPreferences = null;
        try {
            secureSharedPreferences = EncryptedSharedPreferences.create(
                    "volegov_secret_shared_prefs",
                    mainKeyAlias,
                    getBaseContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        if(!secureSharedPreferences.getString("writer", "null").equals("null")
                && !secureSharedPreferences.getString("photo", "null").equals("null"))
        {
            binding.editTextText.setText(secureSharedPreferences.getString("writer", "null"));

            byte[] b = Base64.decode(secureSharedPreferences.getString("photo", "null"), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            binding.imageView.setImageBitmap(bitmap);

        }


        SharedPreferences okSecureSharedPreferences = secureSharedPreferences;
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                okSecureSharedPreferences.edit().putString("writer", binding.editTextText.getText().toString()).apply();
                okSecureSharedPreferences.edit().putString("photo", imagePixels).apply();

                Snackbar.make(MainActivity.this.getCurrentFocus(), "сохранено успешно", Snackbar.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && null != data)
        {
            Uri imageUri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String img_path = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bm = BitmapFactory.decodeFile(img_path);
            binding.imageView.setImageBitmap(bm);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            imagePixels = Base64.encodeToString(b, Base64.DEFAULT);
        }
    }
}