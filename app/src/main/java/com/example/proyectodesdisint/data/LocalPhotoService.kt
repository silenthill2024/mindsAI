package com.example.proyectodesdisint.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class LocalPhotoService(private val context: Context) {

    private val photoFileName = "profile_photo_local.jpg"

    /**
     * Guarda la imagen en el almacenamiento interno privado de la app.
     * @param uid El ID único del usuario para que la foto sea personal.
     * @return El path absoluto de la imagen guardada.
     */
    fun savePhotoLocally(uri: Uri, uid: String): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 250, 250, true)
            
            val timestamp = System.currentTimeMillis()
            val newFileName = "profile_${uid}_$timestamp.jpg"
            
            // Borrar fotos viejas de ESTE usuario específico
            context.filesDir.listFiles()?.filter { it.name.startsWith("profile_${uid}_") }?.forEach { it.delete() }
            
            val file = File(context.filesDir, newFileName)
            val outputStream = FileOutputStream(file)
            
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            
            outputStream.flush()
            outputStream.close()
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene el path de la foto local de un usuario específico.
     */
    fun getLocalPhotoPath(uid: String): String? {
        val file = context.filesDir.listFiles()?.find { it.name.startsWith("profile_${uid}_") }
        return file?.absolutePath
    }

    /**
     * Borra la foto local de un usuario específico.
     */
    fun deleteLocalPhoto(uid: String) {
        context.filesDir.listFiles()?.filter { it.name.startsWith("profile_${uid}_") }?.forEach { it.delete() }
    }
}
