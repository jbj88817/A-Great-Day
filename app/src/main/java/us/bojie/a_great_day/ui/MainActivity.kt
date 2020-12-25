package us.bojie.a_great_day.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import us.bojie.a_great_day.R
import us.bojie.a_great_day.ui.time_task.TimeTaskFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userUID = pref.getString(USER_UID, "-1") ?: "-1"
        if (userUID == "-1" || FirebaseAuth.getInstance().currentUser == null) {
            signIn()
        }
    }

    fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.todo)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK && response != null) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                pref.edit().putString(USER_UID, user?.uid).apply()
                val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                navHost?.let { navFragment ->
                    navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                        (fragment as TimeTaskFragment).refreshTask()
                    }
                }
            } else {
                finish()
            }
        }
    }

    companion object {
        const val RC_SIGN_IN = 100
        const val USER_UID = "user_uid"
        const val TOTAL_HOURS_TEXT = "total_hours_text"
        const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
        const val EDIT_TASK_RESULT_OK = Activity.RESULT_FIRST_USER + 1
    }
}