package com.habitquest.domain.model

data class HabitTemplate(
    val name: String,
    val icon: String,
    val category: String
)

data class TaskTemplate(
    val name: String,
    val category: String
)

object HabitTemplates {
    val byCategory: Map<String, List<HabitTemplate>> = mapOf(
        "productividad" to listOf(
            HabitTemplate("Estudiar 30 min", "", "productividad"),
            HabitTemplate("Estudiar 1 hora", "", "productividad"),
            HabitTemplate("Realizar tareas", "", "productividad"),
            HabitTemplate("Avanzar proyecto", "", "productividad"),
            HabitTemplate("Repasar materia", "", "productividad"),
            HabitTemplate("Completar curso online", "", "productividad"),
            HabitTemplate("Ver clase", "", "productividad"),
            HabitTemplate("Preparar examen", "", "productividad"),
            HabitTemplate("Organizar semana", "", "productividad"),
        ),
        "salud_fisica" to listOf(
            HabitTemplate("Hacer ejercicio", "", "salud_fisica"),
            HabitTemplate("Caminar", "", "salud_fisica"),
            HabitTemplate("Correr", "", "salud_fisica"),
            HabitTemplate("Estiramientos", "", "salud_fisica"),
            HabitTemplate("Gimnasio", "", "salud_fisica"),
            HabitTemplate("Yoga", "", "salud_fisica"),
            HabitTemplate("Beber agua", "", "salud_fisica"),
            HabitTemplate("Comer saludable", "", "salud_fisica"),
        ),
        "salud_mental" to listOf(
            HabitTemplate("Meditar", "", "salud_mental"),
            HabitTemplate("Respiracion consciente", "", "salud_mental"),
            HabitTemplate("Escribir diario", "", "salud_mental"),
            HabitTemplate("Gratitud", "", "salud_mental"),
            HabitTemplate("Desconexion digital", "", "salud_mental"),
            HabitTemplate("Dormir 7-8h", "", "salud_mental"),
        ),
        "vida_diaria" to listOf(
            HabitTemplate("Hacer la cama", "", "vida_diaria"),
            HabitTemplate("Ordenar habitacion", "", "vida_diaria"),
            HabitTemplate("Limpiar espacio", "", "vida_diaria"),
            HabitTemplate("Cocinar", "", "vida_diaria"),
            HabitTemplate("Planificar dia", "", "vida_diaria"),
        ),
        "desarrollo" to listOf(
            HabitTemplate("Leer", "", "desarrollo"),
            HabitTemplate("Aprender algo nuevo", "", "desarrollo"),
            HabitTemplate("Practicar habilidad", "", "desarrollo"),
            HabitTemplate("Definir metas", "", "desarrollo"),
            HabitTemplate("Revisar progreso", "", "desarrollo"),
        ),
        "disciplina_digital" to listOf(
            HabitTemplate("Limitar redes sociales", "", "disciplina_digital"),
            HabitTemplate("Modo enfoque", "", "disciplina_digital"),
            HabitTemplate("Evitar distracciones", "", "disciplina_digital"),
            HabitTemplate("No celular de noche", "", "disciplina_digital"),
        ),
        "gamificacion" to listOf(
            HabitTemplate("Completar 3 habitos", "", "gamificacion"),
            HabitTemplate("Mantener racha", "", "gamificacion"),
            HabitTemplate("Subir nivel", "", "gamificacion"),
            HabitTemplate("Objetivo semanal", "", "gamificacion"),
        ),
        "espiritualidad" to listOf(
            HabitTemplate("Orar o meditar", "", "espiritualidad"),
            HabitTemplate("Reflexion diaria", "", "espiritualidad"),
        ),
        "autocuidado" to listOf(
            HabitTemplate("Skincare", "", "autocuidado"),
            HabitTemplate("Caminar", "", "autocuidado"),
            HabitTemplate("Tomar agua", "", "autocuidado"),
        ),
        "familia_corazon" to listOf(
            HabitTemplate("Llamar a mis tatas", "", "familia_corazon"),
            HabitTemplate("Visitar amigo", "", "familia_corazon"),
            HabitTemplate("Escribir a alguien", "", "familia_corazon"),
        ),
        "mama_colegio" to listOf(
            HabitTemplate("Agenda escolar", "", "mama_colegio"),
            HabitTemplate("Mochila", "", "mama_colegio"),
            HabitTemplate("Reunion apoderados", "", "mama_colegio"),
        ),
        "salud" to listOf(
            HabitTemplate("Control medico", "", "salud"),
            HabitTemplate("Medicamento", "", "salud"),
            HabitTemplate("Examenes", "", "salud"),
        ),
    )

    val all: List<HabitTemplate> = byCategory.values.flatten()

    val categoryLabels: Map<String, String> = mapOf(
        "productividad" to "Productividad",
        "salud_fisica" to "Salud Fisica",
        "salud_mental" to "Salud Mental",
        "vida_diaria" to "Vida Diaria",
        "desarrollo" to "Desarrollo",
        "disciplina_digital" to "Disciplina Digital",
        "gamificacion" to "Gamificacion",
        "espiritualidad" to "Espiritualidad",
        "autocuidado" to "Autocuidado",
        "familia_corazon" to "Familia / Corazon",
        "mama_colegio" to "Mama / Colegio",
        "salud" to "Salud",
    )
}

object TaskTemplates {
    val all: List<TaskTemplate> = listOf(
        TaskTemplate("Entregar tarea universidad", "productividad"),
        TaskTemplate("Estudiar prueba especifica", "productividad"),
        TaskTemplate("Asistir a clase", "productividad"),
        TaskTemplate("Reunion grupal", "productividad"),
        TaskTemplate("Pagar cuentas", "vida_diaria"),
        TaskTemplate("Hacer tramite", "vida_diaria"),
        TaskTemplate("Comprar insumos", "vida_diaria"),
        TaskTemplate("Ir al medico", "salud_fisica"),
        TaskTemplate("Orar/meditar", "espiritualidad"),
        TaskTemplate("Reflexion diaria", "espiritualidad"),
        TaskTemplate("Skincare", "autocuidado"),
        TaskTemplate("Caminar", "autocuidado"),
        TaskTemplate("Tomar agua", "autocuidado"),
        TaskTemplate("Llamar a mis tatas", "familia_corazon"),
        TaskTemplate("Visitar amigo", "familia_corazon"),
        TaskTemplate("Escribir a alguien", "familia_corazon"),
        TaskTemplate("Agenda escolar", "mama_colegio"),
        TaskTemplate("Mochila", "mama_colegio"),
        TaskTemplate("Reunion apoderados", "mama_colegio"),
        TaskTemplate("Control medico", "salud"),
        TaskTemplate("Medicamento", "salud"),
        TaskTemplate("Examenes", "salud"),
    )
}
