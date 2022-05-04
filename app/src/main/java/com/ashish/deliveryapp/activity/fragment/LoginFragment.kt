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
import android.widget.*
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
class LoginFragment(val contextParam: Context) : Fragment() {

    lateinit var etPhoneNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegister: TextView




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_login, container, false)
        etPhoneNumber=view.findViewById(R.id.etPhoneNumber)
        etPassword=view.findViewById(R.id.etPassword)
        btnLogin=view.findViewById(R.id.btnLogin)
        txtForgotPassword=view.findViewById(R.id.txtForgotPassword)
        txtRegister=view.findViewById(R.id.txtRegister)

        btnLogin.setOnClickListener {
            if (etPhoneNumber.text.isBlank() || etPhoneNumber.text.length!=10)
            {
                etPhoneNumber.error="Enter Valid Mobile Number"
            }
            else
            {
                if (etPassword.text.isBlank() || etPassword.text.length<=4)
                {
                    etPassword.error="Enter Valid Password"
                }
                else
                {
                    loginUserFun()
                }
            }
        }

        txtForgotPassword.setOnClickListener {
            openForgotPasswordInputFragment()
        }

        txtRegister.setOnClickListener {
            openRegisterFragment()
        }

        return view
    }

    private fun openRegisterFragment() {
        val transaction = fragmentManager?.beginTransaction()

        transaction?.replace(
            R.id.frameLayout,
            RegisterFragment(contextParam)
        )
        transaction?.commit()
    }

    private fun openForgotPasswordInputFragment() {
        val transaction = fragmentManager?.beginTransaction()

        transaction?.replace(
            R.id.frameLayout,
            ForgotPasswordInputFragment(contextParam)
        )
        transaction?.commit()
    }

    fun loginUserFun() {
//         this is done to keep the user logged in after first log in try
         val sharedPreferences = contextParam.getSharedPreferences(
             getString(R.string.shared_preferences),
             Context.MODE_PRIVATE
         )

         if (ConnectionManager().checkConnectivity(activity as Context))
         {
             try {
                 val loginUser=JSONObject()
//                 Params:
                 loginUser.put("mobile_number", etPhoneNumber.text)
                 loginUser.put("password", etPassword.text)

                 val queue= Volley.newRequestQueue(activity as Context)
                 val url="http://13.235.250.119/v2/login/fetch_result/"

                 val jsonObjectRequest=object: JsonObjectRequest(Method.POST,url,loginUser,
                     Response.Listener {
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
                                 "Welcome " + data.getString("name"),
                                 Toast.LENGTH_SHORT
                             ).show()

                             userSuccessfullyLoggedIn()

                         } else {

                             val responseMessageServer = response.getString("errorMessage")
                             Toast.makeText(
                                 contextParam,
                                 responseMessageServer.toString(),
                                 Toast.LENGTH_SHORT
                             ).show()
                         }
                     },Response.ErrorListener {
                         Toast.makeText(
                             contextParam,
                             "Some Error occurred!!",
                             Toast.LENGTH_SHORT
                         ).show()

                     })
                 {
                     override fun getHeaders(): MutableMap<String, String> {
                         val headers = HashMap<String, String>()
                         headers["Content-type"] = "application/json"
                         headers["token"] = "42c590ad1c0b24"
                         return headers
                     }
                 }
                 queue.add(jsonObjectRequest)
             }catch (e:JSONException)
             {
                 Toast.makeText(
                     contextParam,
                     "Some unexpected error occurred!!",
                     Toast.LENGTH_SHORT
                 ).show()
             }
         }
         else
         {
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

    private fun userSuccessfullyLoggedIn() {

        val intent = Intent(activity as Context, DashboardActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onResume() {
        if (!ConnectionManager().checkConnectivity(activity as Context)) {
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
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }

        super.onResume()
    }

}