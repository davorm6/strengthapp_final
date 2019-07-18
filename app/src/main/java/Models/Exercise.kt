package Models

import com.google.gson.annotations.SerializedName

data class Type (
    @SerializedName("id")
    val id: Int,
    val name: String
)

data class TypeList (
    @SerializedName("types")
    val types: List<Type>
)

data class Muscle (
    @SerializedName("id")
    val id: Int,
    val name: String
)

data class MuscleList (
    @SerializedName("muscles")
    val muscles: List<Muscle>
)

data class Exercise (
    @SerializedName("id")
    val id: Int,
    val type_id: Int,
    val name: String,
    val information: String,
    val instructions: String,
    val type: Type? = null,
    val muscles: List<Muscle>,
    val request: Int = 0
)

data class ExerciseList (
    @SerializedName("exercises")
    val exercises: List<Exercise>
)