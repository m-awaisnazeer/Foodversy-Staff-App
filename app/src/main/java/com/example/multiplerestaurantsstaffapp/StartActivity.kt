package com.example.multiplerestaurantsstaffapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.multiplerestaurantsstaffapp.Common.Common
import com.example.multiplerestaurantsstaffapp.EventBus.OwnerRegisterEvent
import com.example.multiplerestaurantsstaffapp.Model.RestaurantOwnerModel
import com.example.multiplerestaurantsstaffapp.Retrofit.IMyRestaurantAPI
import com.example.multiplerestaurantsstaffapp.Retrofit.RetrofitClient
import com.example.multiplerestaurantsstaffapp.ui.RegisteruserDialogFagment.RegisterUserDialogFragment
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dmax.dialog.SpotsDialog
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class StartActivity : AppCompatActivity() {
    lateinit var fm: FragmentManager
    lateinit var registerUserDialogFragment: RegisterUserDialogFragment
    var myRestaurantAPI: IMyRestaurantAPI? = null
    var compositeDisposable = CompositeDisposable()
    var dialog: AlertDialog? = null


    companion object {
        private val LOGIN_REQUEST_CODE = 7171
        private const val TAG = "StartActivity"
        private val SPLASH_TIME_OUT: Long = 3000 // 1 sec

    }

    lateinit var providers: List<AuthUI.IdpConfig>
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var listner: FirebaseAuth.AuthStateListener

    override fun onStart() {
        super.onStart()
        delaySplashScreen()
        EventBus.getDefault().register(this)
    }


    private fun delaySplashScreen() {
        Handler().postDelayed({

            firebaseAuth.addAuthStateListener(listner)

        }, SPLASH_TIME_OUT)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        init()

    }

    private fun init() {
        Paper.init(this)

        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI::class.java)

        fm = getSupportFragmentManager()
        registerUserDialogFragment =
                RegisterUserDialogFragment.newInstance("Some Title")!!
        providers = Arrays.asList(
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        firebaseAuth = FirebaseAuth.getInstance()
        listner = FirebaseAuth.AuthStateListener { myFirebaseAuth ->
            val user = myFirebaseAuth.currentUser
            if (user != null) {

                checkUserFromDatabase()
            } else {
                showLogInLayout()
            }
        }
    }

    private fun checkUserFromDatabase() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {

                        FirebaseInstanceId.getInstance().instanceId
                                .addOnFailureListener {
                                    Toast.makeText(this@StartActivity, "Get Token" + it.message, Toast.LENGTH_SHORT).show()

                                }.addOnCompleteListener {

                                    Common.yourToken = it.result?.token
                                    if (it.isSuccessful) {
                                        Log.d(TAG, "onPermissionGranted: "+it.getResult()?.token)
                                        val user: FirebaseUser?
                                        user = FirebaseAuth.getInstance().currentUser
                                        if (user != null) {
                                            //dialog!!.show()
                                            Paper.book().write(Common.REMEMBER_FBID, user.uid)
                                            showRegisterLayout(user, it)
                                        } else {
//                            startActivity(Intent(this@StartActivity, MainActivity::class.java))
//                            finish()
                                        }
                                    }
                                }

                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                        Toast.makeText(this@StartActivity, "Permission Required to Use this App", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(permissionRequest: PermissionRequest, permissionToken: PermissionToken) {}
                }).check()


    }

    private fun showRegisterLayout(user: FirebaseUser, task: Task<InstanceIdResult>) {
        Paper.book().write(Common.REMEMBER_FBID, user.uid)

        Toast.makeText(this@StartActivity, "" + task.result?.token, Toast.LENGTH_SHORT).show()

        compositeDisposable.add(myRestaurantAPI!!.updateTokenToServer(
                Common.API_KEY,
                user.uid,
                task.getResult()?.token
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    compositeDisposable.add(myRestaurantAPI!!.getRestaurantOwner(
                            Common.API_KEY,
                            FirebaseAuth.getInstance().uid)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ restaurantOwnerModel: RestaurantOwnerModel ->
                                if (restaurantOwnerModel.isSuccess) {
                                    Common.currentRestaurantOwner = restaurantOwnerModel.result[0]

                                    if (Common.currentRestaurantOwner.isStatus) {
                                        val intent = Intent(this@StartActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@StartActivity, "You Don't have permission to Sign In", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this@StartActivity, "Success: Not registered", Toast.LENGTH_SHORT).show()
                                    registerUserDialogFragment.show(fm, "fragment_edit_name")
                                    registerUserDialogFragment.isCancelable = false
                                }

                                dialog!!.dismiss()
                            }, { throwable: Throwable? ->
                                dialog!!.dismiss()
                                Toast.makeText(this@StartActivity, "[GET USER API] " + throwable!!.message, Toast.LENGTH_LONG).show()
                                Log.d(TAG, "onPermissionGranted: " + throwable.message)
                            }))
                }, {
                    Toast.makeText(this@StartActivity, "[UPDATE TOKEN] " + it!!.message, Toast.LENGTH_LONG).show()

                }))


    }

    private fun showLogInLayout() {


        val authMethodPickerLayout =
                AuthMethodPickerLayout.Builder(R.layout.login_methods_firebase_ui)
                        .setPhoneButtonId(R.id.sign_with_phone)
                        .setGoogleButtonId(R.id.sign_with_google)
                        .build();
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setTheme(R.style.LogInTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build()
                , LOGIN_REQUEST_CODE
        )

    }

    override fun onStop() {
        if (firebaseAuth != null && listner != null) firebaseAuth.removeAuthStateListener(listner)
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                Toast.makeText(this, "Error:\n" + response!!.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun userRegistrationEvent(event: OwnerRegisterEvent) {
        if (event.isOwnerRegistration) {
            if (Common.currentRestaurantOwner.isStatus) {
                registerUserDialogFragment.dismiss()
                val intent = Intent(this@StartActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                registerUserDialogFragment.dismiss()
                Toast.makeText(this@StartActivity, "You Don't have permission to Sign In", Toast.LENGTH_SHORT).show()
            }
        } else {
            registerUserDialogFragment.dismiss()

        }
    }

}