package com.apprestaurante

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apprestaurante.databinding.ActivitySignBinding
import com.apprestaurante.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {

    private lateinit var userName : String
    private lateinit var nameOfRestaurant : String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var address : String
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference

    private val binding : ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // inicialización de firebase Auth
        auth = Firebase.auth

        // Inicialización de firebase database
        database = Firebase.database.reference

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.createUserButton.setOnClickListener {
            //obtener texto de los editTexts
            userName = binding.name.text.toString().trim()
            nameOfRestaurant = binding.restaurantName.text.toString().trim()
            email = binding.emailOrPhone.text.toString().trim()
            password = binding.password.text.toString().trim()

            if(userName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank() || binding.listOfLocation.text.toString().equals("Elegir ubicación")){
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            } else{
                createAccount(email, password)
            }
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)

        }
        binding.btnTengoCuenta.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val locationList = arrayOf("Zacapa","Rio Hondo", "Estanzuela", "Gualán", "Teculután","Usumatlán","Cabañas","San Diego", "La Unión", "Huité")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView=binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { 
            task ->
            if(task.isSuccessful){
                Toast.makeText(this, "cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show()

                if(task.exception.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."){
                    Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show()
                }

                if(task.exception.toString() == "com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]"){
                    Toast.makeText(this, "La contraseña debe ser de al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //guardar información en el database
    private fun saveUserData() {
        //obtener texto de los editTexts
        userName = binding.name.text.toString().trim()
        nameOfRestaurant = binding.restaurantName.text.toString().trim()
        email = binding.emailOrPhone.text.toString().trim()
        password = binding.password.text.toString().trim()
        address = binding.listOfLocation.text.toString().trim()

        val user = UserModel(userName, nameOfRestaurant, email, password, "", address)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        //guardar la información del usuario en firebase database
        database.child("user").child(userId).setValue(user)
    }
}
