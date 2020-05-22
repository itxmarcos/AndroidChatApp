package com.example.messengergroupass.user_management

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.messengergroupass.messages_management.MessengerActivity
import com.example.messengergroupass.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener{
            performLogin()
        }

        tvNeedAccount.setOnClickListener{
            finish()
        }
    }
    //Logeamos un usuario para poder comprobar con un solo emulador la llegada y la respuesta de los mensajes que se  envian dentro de la aplicacion
    private fun performLogin(){
        val mail=etEmailLogin.text.toString()
        val password=etPasswordLogin.text.toString()

        if (mail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                val intent = Intent(this, MessengerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

    }
}
