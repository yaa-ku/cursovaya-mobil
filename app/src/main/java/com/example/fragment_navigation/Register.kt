package com.example.fragment_navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fragment_navigation.databinding.ActivityMainBinding
import com.example.fragment_navigation.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var user: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance()

        checkIfUserIsLogged()


        binding.btnLogin.setOnClickListener {
            registerUser()
        }
    }

    private fun checkIfUserIsLogged(){
        if (user.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun registerUser(){
        val email = binding.etMail.text.toString()
        val password = binding.etPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            /*  user.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity()) {
                      task ->
                  if (task.isSuccessful){
                      Toast.makeText(
                          this,
                          "Пользователь успешно добавлен",
                          Toast.LENGTH_SHORT
                      ).show()
                      startActivity(Intent(this, SecondActivity::class.java))
                      finish()
                  } else{*/

            user.signInWithEmailAndPassword(email, password).addOnCompleteListener { mTask ->
                if (mTask.isSuccessful){
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    /*  Toast.makeText(
                          this,
                          task.exception!!.message,
                          Toast.LENGTH_SHORT
                      ).show()*/
                    Toast.makeText(
                        this,
                        "Данный пользователь не активирован, либо вы ввели не верные данные",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                //  }

                //  }
            }
        }else {
            Toast.makeText(this, "Ввведите почту и пароль", Toast.LENGTH_SHORT).show()
        }
    }
}