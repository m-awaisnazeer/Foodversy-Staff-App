package com.example.multiplerestaurantsstaffapp.ui.RegisteruserDialogFagment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.multiplerestaurantsstaffapp.Common.Common
import com.example.multiplerestaurantsstaffapp.EventBus.OwnerRegisterEvent
import com.example.multiplerestaurantsstaffapp.HomeActivity
import com.example.multiplerestaurantsstaffapp.Model.RestaurantOwnerModel
import com.example.multiplerestaurantsstaffapp.Model.UpdateRestaurantOwnerModel
import com.example.multiplerestaurantsstaffapp.R
import com.example.multiplerestaurantsstaffapp.Retrofit.IMyRestaurantAPI
import com.example.multiplerestaurantsstaffapp.Retrofit.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus


class RegisterUserDialogFragment : DialogFragment() {

    var myRestaurantAPI: IMyRestaurantAPI? = null
    var disposable = CompositeDisposable()
    lateinit var editUserPhone: EditText
    lateinit var editUserName: EditText
    lateinit var editUserAddress: EditText
    lateinit var button_continue: Button
    var dialog: AlertDialog? = null


//    override fun onStart() {
//        super.onStart()
//        val dialog: Dialog? = dialog
//        if (dialog != null) {
//            val width = ViewGroup.LayoutParams.MATCH_PARENT
//            val height = ViewGroup.LayoutParams.WRAP_CONTENT
//            dialog.getWindow()!!.setLayout(width, height)
//        }
//    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_user_register_dialog, container, false)
        getDialog()!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI::class.java)
        dialog = SpotsDialog.Builder().setCancelable(false).setContext(requireContext()).build()


        editUserName = view.findViewById(R.id.edit_name)
        editUserPhone = view.findViewById(R.id.edit_phone)
        editUserAddress = view.findViewById(R.id.txt_user_address)

        button_continue = view.findViewById(R.id.button_continue)

        button_continue.setOnClickListener {
            dialog!!.show()

            val current_user_id: String = FirebaseAuth.getInstance().currentUser!!.uid

            if (current_user_id == null || TextUtils.isEmpty(current_user_id)) {

            } else {
                FirebaseInstanceId.getInstance().instanceId.addOnFailureListener {
                    Toast.makeText(requireContext(), "" + it.message, Toast.LENGTH_SHORT).show()

                }.addOnCompleteListener {
                    it.getResult()!!.token
                    Toast.makeText(requireContext(), "" + it.result?.token, Toast.LENGTH_SHORT).show()

                    disposable.add(myRestaurantAPI!!.updateTokenToServer(
                            Common.API_KEY,
                            current_user_id,
                            it.getResult()?.token
                    ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({


                            }, {
                                Toast.makeText(requireContext(), "[UPDATE TOKEN]" + it.message, Toast.LENGTH_SHORT).show()

                            }))





                    disposable.add(myRestaurantAPI!!.updateRestaurantOwnerModel(Common.API_KEY,
                            editUserPhone.getText().toString(),
                            editUserName.getText().toString(),
                            current_user_id!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ updateRestaurantOwnerModel: UpdateRestaurantOwnerModel ->
                                if (updateRestaurantOwnerModel.isSuccess) {

                                    disposable.add(myRestaurantAPI!!.getRestaurantOwner(Common.API_KEY,
                                            current_user_id!!)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({ restaurantOwnerModel: RestaurantOwnerModel ->
                                                if (restaurantOwnerModel.isSuccess) {

                                                    getDialog()!!.dismiss()
                                                    Common.currentRestaurantOwner = restaurantOwnerModel.result[0]

                                                    EventBus.getDefault().postSticky(OwnerRegisterEvent(true))
                                                } else {
                                                    getDialog()!!.dismiss()
                                                    EventBus.getDefault().postSticky(OwnerRegisterEvent(false))
                                                    Toast.makeText(requireContext(), "Success: Not registered", Toast.LENGTH_SHORT).show()

                                                }

                                                dialog!!.dismiss()
                                            }, { throwable: Throwable? ->
                                                dialog!!.dismiss()
                                                Toast.makeText(requireContext(), "[GET USER API] " + throwable!!.message, Toast.LENGTH_LONG).show()
                                            }))

                                } else {
                                    Toast.makeText(requireContext(), "[GET USER RESULT]" + updateRestaurantOwnerModel.message, Toast.LENGTH_SHORT).show()
                                }
                                dialog!!.dismiss()

                            }, { throwable: Throwable ->
                                dialog!!.dismiss()
                                Toast.makeText(requireContext(), "[Update User API]" + throwable.message, Toast.LENGTH_SHORT).show()
                            }))
                }


            }
        }



        dialog!!.setTitle("title")
        return view
    }

    companion object {
        fun newInstance(title: String?): RegisterUserDialogFragment? {
            val frag = RegisterUserDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            frag.setArguments(args)
            return frag
        }
    }


}