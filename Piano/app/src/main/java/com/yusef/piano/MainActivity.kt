package com.yusef.piano

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.yusef.piano.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG:String = "Piano:MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var piano: PianoLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        signInAnonymously()

        piano = supportFragmentManager.findFragmentById(binding.piano.id) as PianoLayout

        piano.onSave = {
            this.upload(it)
        }
    }

    private fun upload(file: Uri) {
        val ref = FirebaseStorage.getInstance().reference.child("melodies/${file.lastPathSegment}")
        var uploadTask = ref.putFile(file)

        uploadTask.addOnSuccessListener {
            Log.d(TAG, "Uploaded successfully! ${it.storage}")
        }.addOnFailureListener {
            Log.d(TAG, "Uploading failed!")
        }
    }

    private fun signInAnonymously() {
        auth.signInAnonymously().addOnSuccessListener {
            Log.d(TAG, "Login success ${it.user.toString()}")
        }.addOnFailureListener {
            Log.d(TAG, "Login failed ${it}")
        }
    }
}
