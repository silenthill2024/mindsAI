package com.example.proyectodesdisint.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.proyectodesdisint.model.Task

@Composable
fun TaskEditorDialog(
    task: Task,
    isEditing: Boolean,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var titulo by remember(task.id, task.documentId) {
        mutableStateOf(task.titulo)
    }

    var descripcion by remember(task.id, task.documentId) {
        mutableStateOf(task.descripcion)
    }

    var fecha by remember(task.id, task.documentId) {
        mutableStateOf(task.fecha)
    }

    var hora by remember(task.id, task.documentId) {
        mutableStateOf(task.hora)
    }

    var prioridad by remember(task.id, task.documentId) {
        mutableStateOf(
            task.prioridad.ifBlank { "Media" }
        )
    }

    var completada by remember(task.id, task.documentId) {
        mutableStateOf(task.completado)
    }

    val puedeGuardar =
        titulo.isNotBlank() &&
        fecha.isNotBlank() &&
        hora.isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(24.dp),
                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isEditing) {
                            "Editar tarea"
                        } else {
                            "Nueva tarea"
                        },
                        style =
                            MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color =
                            MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint =
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }

                OutlinedTextField(
                    value = titulo,
                    onValueChange = {
                        titulo = it
                    },
                    label = {
                        Text("Título")
                    },
                    placeholder = {
                        Text(
                            "Escribe el título de la tarea"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = it
                    },
                    label = {
                        Text("Descripción")
                    },
                    placeholder = {
                        Text(
                            "Escribe una descripción opcional"
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fecha,
                    onValueChange = {
                        fecha = it
                    },
                    label = {
                        Text("Fecha")
                    },
                    placeholder = {
                        Text("AAAA-MM-DD")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hora,
                    onValueChange = {
                        hora = it
                    },
                    label = {
                        Text("Hora")
                    },
                    placeholder = {
                        Text("HH:MM")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Prioridad",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color =
                        MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    PriorityChip(
                        text = "Alta",
                        symbol = "↑",
                        selected = prioridad == "Alta",
                        accentColor = Color(0xFFFF5252),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            prioridad = "Alta"
                        }
                    )

                    PriorityChip(
                        text = "Media",
                        symbol = "−",
                        selected = prioridad == "Media",
                        accentColor = Color(0xFFFFC107),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            prioridad = "Media"
                        }
                    )

                    PriorityChip(
                        text = "Baja",
                        symbol = "↓",
                        selected = prioridad == "Baja",
                        accentColor = Color(0xFF44D17A),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            prioridad = "Baja"
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Marcar como completada al guardar",
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme.onSurface
                    )

                    Switch(
                        checked = completada,
                        onCheckedChange = {
                            completada = it
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.End,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            onSave(
                                task.copy(
                                    titulo = titulo.trim(),
                                    descripcion =
                                        descripcion.trim(),
                                    fecha = fecha.trim(),
                                    hora = hora.trim(),
                                    prioridad = prioridad,
                                    completado = completada,
                                    completionTime =
                                        if (completada) {
                                            task.completionTime
                                                ?: System.currentTimeMillis()
                                        } else {
                                            null
                                        }
                                )
                            )
                        },
                        enabled = puedeGuardar,
                        shape = RoundedCornerShape(20.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    MaterialTheme.colorScheme.primary
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )

                        Text(
                            text = "Guardar",
                            modifier =
                                Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityChip(
    text: String,
    symbol: String,
    selected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(5.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = symbol,
                    color = accentColor,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(text)
            }
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor =
                MaterialTheme.colorScheme.primary,
            selectedLabelColor =
                MaterialTheme.colorScheme.onPrimary
        )
    )
}