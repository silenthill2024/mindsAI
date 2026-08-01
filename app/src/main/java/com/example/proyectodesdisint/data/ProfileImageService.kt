package com.example.proyectodesdisint.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileImageService {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun loadProfileImage(
        onSuccess: (String?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onSuccess(null)
            return
        }

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val firestoreUrl = document.getString("photoUrl")
                val authUrl = auth.currentUser?.photoUrl?.toString()
                onSuccess(firestoreUrl ?: authUrl)
            }
            .addOnFailureListener(onFailure)
    }

    fun uploadProfileImage(
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser

        if (user == null) {
            onFailure(IllegalStateException("No existe una sesión activa"))
            return
        }

        val imageReference = storage.reference
            .child("profile_images")
            .child(user.uid)
            .child("profile.jpg")

        imageReference.putFile(imageUri)
            .continueWithTask { uploadTask ->
                if (!uploadTask.isSuccessful) {
                    throw uploadTask.exception
                        ?: IllegalStateException("No fue posible subir la imagen")
                }

                imageReference.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                val photoUrl = downloadUri.toString()

                val profileData = mapOf(
                    "uid" to user.uid,
                    "nombre" to (user.displayName ?: "Usuario MindsAI"),
                    "email" to (user.email ?: ""),
                    "photoUrl" to photoUrl
                )

                firestore.collection("users")
                    .document(user.uid)
                    .set(profileData, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener {
                        val request = UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri)
                            .build()

                        user.updateProfile(request)
                            .addOnSuccessListener {
                                onSuccess(photoUrl)
                            }
                            .addOnFailureListener(onFailure)
                    }
                    .addOnFailureListener(onFailure)
            }
            .addOnFailureListener(onFailure)
    }
}
