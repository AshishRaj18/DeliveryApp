package com.ashish.deliveryapp.activity.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ashish.deliveryapp.R
import com.ashish.deliveryapp.activity.fragment.LoginFragment


//This activity is important because this activity will make sure that after splash screen either the
// dashboard activity will open or the login fragment.


class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        val sharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        //If already logged in once, directly open Dashboard
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            openLoginFragment()
        }
    }

    fun openLoginFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.frameLayout,
            LoginFragment(this)
        ).commit()

        supportActionBar?.title = "DashBoard"
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            !is LoginFragment -> openLoginFragment()
            else -> super.onBackPressed()
        }
    }
}