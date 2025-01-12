package com.hfad2.projectmanagmentapplication.project_members.muna;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
    private Spinner spinnerUserType, spinnerRole;
    private TextView tvRole;
    private Button btnRegister;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupUserTypeSpinner();
        setupRegisterButton();
    }

    private void initializeViews() {
        etName = findViewById(R.id.edtname);
        etPassword = findViewById(R.id.edtPassword);
        etConfirmPassword = findViewById(R.id.edtPassword2);
        spinnerUserType = findViewById(R.id.spnType);
        spinnerRole = findViewById(R.id.spnRole);
        tvRole = findViewById(R.id.textView6);
        btnRegister = findViewById(R.id.btnRegister);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupUserTypeSpinner() {
        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString().toLowerCase();
                boolean isEmployee = selectedType.equals("employee");
                tvRole.setVisibility(isEmployee ? View.VISIBLE : View.GONE);
                spinnerRole.setVisibility(isEmployee ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRegisterButton() {
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String userType = spinnerUserType.getSelectedItem().toString().toLowerCase();
        String role = userType.equals("employee") ?
                spinnerRole.getSelectedItem().toString() : "";

        if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userType.equals("employee") && role.equals("All Roles")) {
            Toast.makeText(this, "Please select a specific role", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put(APIConfig.PARAM_USERNAME, name);
            params.put(APIConfig.PARAM_PASSWORD, password);
            params.put(APIConfig.PARAM_USER_TYPE, userType);
            if (userType.equals("employee")) {
                params.put(APIConfig.PARAM_ROLE, role);
            }
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