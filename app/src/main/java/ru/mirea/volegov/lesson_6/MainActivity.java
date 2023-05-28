package ru.mirea.volegov.lesson_6;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import ru.mirea.volegov.lesson_6.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPref = getSharedPreferences("sp_volegvo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("first_param", binding.editTextText.getText().toString());
                editor.putString("second_param", binding.editTextText2.getText().toString());
                editor.putString("third_param", binding.editTextText3.getText().toString());
                editor.apply();
            }
        });

        if(!sharedPref.getString("first_param", "unknown").equals("unknown")
                && !sharedPref.getString("second_param", "unknown").equals("unknown")
                && !sharedPref.getString("third_param", "unknown").equals("unknown"))
        {
            binding.editTextText.setText(sharedPref.getString("first_param", "unknown"));
            binding.editTextText2.setText(sharedPref.getString("second_param", "unknown"));
            binding.editTextText3.setText(sharedPref.getString("third_param", "unknown"));
        }
    }
}