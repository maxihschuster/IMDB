package com.example.imdb5.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.imdb5.MainActivity
import com.example.imdb5.R
import com.example.imdb5.databinding.ActivityLoginBinding
import com.example.imdb5.models.GlobalUser
import com.example.imdb5.models.ProviderType
import com.example.imdb5.models.User
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()


    private lateinit var binding : ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (GlobalUser.getUser(this)?.provider != "" && GlobalUser.getUser(this)?.provider != null){
            showHome()
        }
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = View.VISIBLE
    }


    private fun setupButtons() {

        binding.signUpButton.setOnClickListener{
            //TODO mostrar iconito cargando o grisear pantalla
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        GlobalUser.setUser(this,User(it.result.user?.email?: "",ProviderType.BASIC.name))
                        showHome()
                    }else{
                        showAlert(it.exception!!.message.toString())
                    }
                }
            }
        }

        binding.loginButton.setOnClickListener{
            //TODO mostrar iconito cargando o grisear pantalla
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        GlobalUser.setUser(this,User(it.result.user?.email?: "",ProviderType.BASIC.name))
                        showHome()
                    }else{
                        showAlert(it.exception!!.message.toString())
                    }
                }
            }
        }

        binding.googleBtnRounded.setOnClickListener{
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut() // por si tenemos mas de 1 cuenta de google en el celu, poder elegir
            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }

        binding.facebookBtnRounded.setOnClickListener{
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))  //TODO ver que mas puedo traer de FB   nombre????
            LoginManager.getInstance().registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        val token = it.accessToken
                        val credential = FacebookAuthProvider.getCredential(token.token)
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                            if (it.isSuccessful) {
                                GlobalUser.setUser(this@LoginActivity,User(it.result.user?.email?: "",ProviderType.FACEBOOK.name))
                                showHome()
                            } else {
                                showAlert(it.exception!!.message.toString())
                            }
                        }
                    }
                }
                override fun onError(error: FacebookException?) {
                    showAlert(error.toString())
                }
                override fun onCancel() {}
            })
        }
    }

    private fun showAlert(msg : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando ususario \n \n $msg")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Acceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(){
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==GOOGLE_SIGN_IN){ // true-> signed by google
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)                //esta puede tirar error si no puede recuperar la cuenta, tiene que ir adentro de try catch
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            GlobalUser.setUser(this,User(it.result.user?.email?: "", ProviderType.GOOGLE.name))
                            showHome()
                        } else {
                            showAlert(it.exception!!.message.toString())
                        }
                    }
                }
            }catch (e: ApiException){
                showAlert(e.toString())
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed();
        finish()
    }
}

