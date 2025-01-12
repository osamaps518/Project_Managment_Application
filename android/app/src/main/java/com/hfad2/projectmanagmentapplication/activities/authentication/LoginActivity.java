package com.hfad2.projectmanagmentapplication.activities.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.employee.EmployeeDashboardActivity;
import com.hfad2.projectmanagmentapplication.activities.manager.ManagerDashboardActivity;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtusername);
        edtPassword = findViewById(R.id.edtpassword);
        btnLogin = findViewById(R.id.btnlogin);
        btnCreate = findViewById(R.id.btncreate);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(username, password);
                }
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(final String username, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            String status = jsonResponse.getString(APIConfig.RESPONSE_STATUS);
                            if (status.equals(APIConfig.STATUS_SUCCESS)) {
                                String userType = jsonResponse.getString(APIConfig.RESPONSE_TYPE);
                                if (userType.equals(APIConfig.USER_TYPE_MANAGER)) {
                                    String userId = jsonResponse.getString(APIConfig.RESPONSE_USER_ID);
                                    SessionManager.initializeSession(
                                            LoginActivity.this,
                                            userId,      // from login response
                                            userType,    // "MANAGER" or "EMPLOYEE"
                                            username     // from login response
                                    );
                                    Intent intent = new Intent(LoginActivity.this, ManagerDashboardActivity.class);
                                    intent.putExtra(APIConfig.PARAM_MANAGER_ID, userId);
                                    startActivity(intent);
                                } else if (userType.equals(APIConfig.USER_TYPE_EMPLOYEE)) {
                                    String userId = jsonResponse.getString(APIConfig.RESPONSE_USER_ID);
                                    // Initialize session for employee too
                                    SessionManager.initializeSession(
                                            LoginActivity.this,
                                            userId,      // from login response
                                            userType,    // "MANAGER" or "EMPLOYEE"
                                            username     // from login response
                                    );
                                    Intent intent = new Intent(LoginActivity.this, EmployeeDashboardActivity.class);
                                    intent.putExtra(APIConfig.PARAM_EMPLOYEE_ID, userId);  // Add the employee ID
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not correct username or password ", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        Toast.makeText(LoginActivity.this, APIConfig.ERROR_NETWORK + ": " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APIConfig.PARAM_USERNAME, username);
                params.put(APIConfig.PARAM_PASSWORD, password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}