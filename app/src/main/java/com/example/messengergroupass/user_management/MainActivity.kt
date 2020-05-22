package com.example.messengergroupass.user_management

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.messengergroupass.messages_management.MessengerActivity
import com.example.messengergroupass.R
import com.example.messengergroupass.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRegister.setOnClickListener {
            performRegister()
        }

        tvAlreadyRegistered.setOnClickListener{
         val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister(){
        //val username=etUsername.text.toString()
        val password=etPassword.text.toString()
        val email= etEmail.text.toString()

        if(email.isEmpty()|| password.isEmpty())
        {
            Toast.makeText(this,"Please enter text on all the fields",Toast.LENGTH_SHORT).show()
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)//Hacemos Auth para que nada mas registrar al usuario se cree un uid al que luego podremos acceder para registrar a los usuarios en DataBase
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("MainActivityRegister","Successfully created user(uid): ${it.result?.user?.uid}")

                saveUserToFirebaseDataBase()
            }
            .addOnFailureListener{
                Log.d("MainActivityRegister","Failed to create user: ${it.message}")
            }

    }

    private fun saveUserToFirebaseDataBase()//Conectamos con Firebase Database para pasarle la informacion que se ha registrado
    //Al usar antes el FirebaseAuth, accedemos directamente al uid del usuario ya creado al registrarse el usuario
    {
        val uid= FirebaseAuth.getInstance().uid?: ""
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user= User(uid,  etUsername.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("MainActivitySaveUser","Successfully created user on Firebase")
                val intent= Intent(this,
                    MessengerActivity::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)//al darle atras te sale de la aplicacion en vez de a registrarte
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("MainActivitySaveUser","Failed to create user on Firebase")
            }
    }





}
