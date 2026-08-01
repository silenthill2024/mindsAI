package com.example.proyectodesdisint.data

import com.example.proyectodesdisint.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun loadProfile(
        onSuccess: (UserProfile) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            onFailure(IllegalStateException("No existe una sesión activa"))
            return
        }

        firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val profile = document.toObject(UserProfile::class.java)

                if (profile != null) {
                    onSuccess(
                        profile.copy(
                            uid = firebaseUser.uid,
                            email = profile.email.ifBlank {
                                firebaseUser.email.orEmpty()
                            },
                            nombre = profile.nombre.ifBlank {
                                firebaseUser.displayName.orEmpty()
                            }
                        )
                    )
                } else {
                    onSuccess(
                        UserProfile(
                            uid = firebaseUser.uid,
                            nombre = firebaseUser.displayName.orEmpty(),
                            email = firebaseUser.email.orEmpty()
                        )
                    )
                }
            }
            .addOnFailureListener(onFailure)
    }

    fun saveProfile(
        profile: UserProfile,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            onFailure(IllegalStateException("No existe una sesión activa"))
            return
        }

        val finalProfile = profile.copy(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: profile.email,
            perfilCompleto = calculateCompletion(profile)
        )

        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(finalProfile, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener(onFailure)
    }

    private fun calculateCompletion(profile: UserProfile): Int {
        val values = listOf(
            profile.nombre,
            profile.username,
            profile.universidad,
            profile.carrera,
            profile.gradoEstudio,
            profile.semestre,
            profile.grupo,
            profile.matricula,
            profile.ciudad,
            profile.biografia,
            profile.github,
            profile.linkedin
        )

        val completedSimpleFields = values.count { it.isNotBlank() }

        val completedLists = listOf(
            profile.materiasInteres,
            profile.materiasActuales,
            profile.asesorias,
            profile.objetivos
        ).count { it.isNotEmpty() }

        val totalFields = values.size + 4
        val completedFields = completedSimpleFields + completedLists

        return ((completedFields.toFloat() / totalFields) * 100)
            .toInt()
            .coerceIn(0, 100)
    }
}
