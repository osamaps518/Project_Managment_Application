package com.hfad2.projectmanagmentapplication.activities.authentication;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etPassword, etConfirmPassword;
    private Spinner spinnerUserType;
    private Button btnRegister;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.edtname);
        etPassword = findViewById(R.id.edtPassword);
        etConfirmPassword = findViewById(R.id.edtPassword2);
        spinnerUserType = findViewById(R.id.spnType);
        btnRegister = findViewById(R.id.btnRegister);

        requestQueue = Volley.newRequestQueue(this);
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String userType = spinnerUserType.getSelectedItem().toString();

        if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put(APIConfig.PARAM_USERNAME, name);
            params.put(APIConfig.PARAM_PASSWORD, password);
            params.put(APIConfig.PARAM_USER_TYPE, userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, APIConfig.REGISTER, params,
                response -> {
                    try {
                        if (response.getBoolean(APIConfig.RESPONSE_SUCCESS)) {
                            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, response.getString(APIConfig.RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(this, APIConfig.ERROR_NETWORK + ": " + error.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }
}