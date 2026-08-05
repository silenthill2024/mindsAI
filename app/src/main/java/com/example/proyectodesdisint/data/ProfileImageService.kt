package com.example.proyectodesdisint.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProfileImageService(private val context: Context? = null) {

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

        // Optimized approach: Compress and store as Base64 in Firestore to avoid Storage costs/limits
        if (context != null) {
            try {
                val base64Image = compressImageToBase64(imageUri)
                
                val updateData = mapOf("photoUrl" to base64Image)

                firestore.collection("users")
                    .document(user.uid)
                    .set(updateData, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener {
                        onSuccess(base64Image)
                    }
                    .addOnFailureListener(onFailure)
                return
            } catch (e: Exception) {
                // Fallback to storage if compression fails
            }
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

    private fun compressImageToBase64(uri: Uri): String {
        val inputStream: InputStream? = context?.contentResolver?.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        
        // Resize to a small thumbnail
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 250, 250, true)
        
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        
        val byteArray = outputStream.toByteArray()
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
