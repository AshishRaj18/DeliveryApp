package com.ashish.deliveryapp.activity.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ashish.deliveryapp.R
import com.ashish.deliveryapp.activity.activity.DashboardActivity
import com.ashish.deliveryapp.activity.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment(val contextParam:Context ) : Fragment() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etPhoneNumber: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var toolBar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_register, container, false)

        etName=view.findViewById(R.id.etName)
        etEmail=view.findViewById(R.id.etEmail)
        etPhoneNumber=view.findViewById(R.id.etPhoneNumber)
        etAddress=view.findViewById(R.id.etAddress)
        etPassword=view.findViewById(R.id.etPassword)
        etConfirmPassword=view.findViewById(R.id.etConfirmPassword)
        btnRegister=view.findViewById(R.id.btnRegister)
        toolBar=view.findViewById(R.id.toolbar)


        setToolBar()

        btnRegister.setOnClickListener {
            val sharedPreferences = contextParam.getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )

            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

            if (ConnectionManager().checkConnectivity(activity as Context)) {

                if (checkForError()) {


                    try {

                        val registerUser = JSONObject()
                        registerUser.put("name", etName.text)
                        registerUser.put("mobile_number", etPhoneNumber.text)
                        registerUser.put("password", etPassword.text)
                        registerUser.put("address", etAddress.text)
                        registerUser.put("email", etEmail.text)

                        val queue = Volley.newRequestQueue(activity as Context)
                        val url = "http://13.235.250.119/v2/register/fetch_result/"

                        val jsonObjectRequest = object : JsonObjectRequest(
                            Method.POST,
                            url,
                            registerUser,
                            Response.Listener {
                                println("Response12 is $it")

                                val response = it.getJSONObject("data")
                                val success = response.getBoolean("success")

                                if (success) {
                                    val data = response.getJSONObject("data")
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    sharedPreferences.edit().putString("user_id", data.getString("user_id")).apply()
                                    sharedPreferences.edit().putString("name", data.getString("name")).apply()
                                    sharedPreferences.edit().putString("email", data.getString("email")).apply()
                                    sharedPreferences.edit().putString("mobile_number", data.getString("mobile_number")).apply()
                                    sharedPreferences.edit().putString("address", data.getString("address")).apply()

                                    Toast.makeText(
                                        contextParam,
                                        "Registered successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    userSuccessfullyRegistered()

                                } else {
                                    val responseMessageServer =
                                        response.getString("errorMessage")
                                    Toast.makeText(
                                        contextParam,
                                        responseMessageServer.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            },
                            Response.ErrorListener {
                                println("Error12 is $it")


                                Toast.makeText(
                                    contextParam,
                                    "Some Error occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] ="42c590ad1c0b24"
                                return headers
                            }
                        }
                        queue.add(jsonObjectRequest)

                    } catch (e: JSONException) {
                        Toast.makeText(
                            contextParam,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                alterDialog.setTitle("No Internet")
                alterDialog.setMessage("Check Internet Connection!")
                alterDialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                    startActivity(settingsIntent)
                }
                alterDialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(activity as Activity)
                }
                alterDialog.create()
                alterDialog.show()
            }
        }

        return view
    }



     fun userSuccessfullyRegistered() {
        val intent = Intent(activity as Context, DashboardActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    fun checkForError(): Boolean {
        var noErrors = 0
        if (etName.text.isBlank()) {
            etName.error = "Name Missing!"
        } else {
            noErrors++
        }

        if (etPhoneNumber.text.isBlank() || etPhoneNumber.text.length != 10) {
            etPhoneNumber.error = "Invalid Mobile Number!"
        } else {
            noErrors++
        }

        if (etEmail.text.isBlank()) {
            etEmail.error = "Email Missing!"
        } else {
            noErrors++
        }

        if (etAddress.text.isBlank()) {
            etAddress.error = "Address Missing!"
        } else {
            noErrors++
        }

        if (etConfirmPassword.text.isBlank()) {
            etConfirmPassword.error = "Field Missing!"
        } else {
            noErrors++
        }

        if (etPassword.text.isBlank() || etPassword.text.length <= 4) {
            etPassword.error = "Invalid Password!"
        } else {
            noErrors++
        }

        if (etPassword.text.isNotBlank() && etConfirmPassword.text.isNotBlank()) {
            if (etPassword.text.toString().toInt() == etConfirmPassword.text.toString().toInt()
            ) {
                noErrors++
            } else {
                etConfirmPassword.error = "Password don't match"
            }
        }

        return noErrors == 7
    }


     fun setToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        (activity as AppCompatActivity).supportActionBar?.title = "Register Yourself"
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }

}