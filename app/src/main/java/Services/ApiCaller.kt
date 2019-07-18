package Services

import Models.*
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import android.util.Base64.NO_WRAP
import com.beust.klaxon.Json
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer



interface ApiCaller {
    //User
    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("users") fun getUsers(): Observable<UserList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("users") fun addUser(@Body user: User): Call<JsonResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @DELETE("users/{id}") fun deleteUser(@Path("id") id: Int) : Completable

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("users/{id}") fun updateUser(@Path("id") id: Int, @Body user: User) : Call<JsonResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("users/{id}") fun getUser(@Path("id") id: Int) : Call<User>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("users/login") fun loginUser(@Body json: JsonObject) : Call<JsonResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("users/search") fun getUserByMail(@Body json: JsonObject): Call<JsonResponse>

    //Trainer

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("trainers") fun getTrainers(): Observable<TrainerList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("trainers/{id}") fun getTrainer(@Path("id") id: Int): Call<Trainer>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("trainers") fun addTrainer(@Body trainer: Trainer): Call<JsonTrainerResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("trainers/user/{id}") fun getUserTrainer(@Path("id") id: Int): Call<Trainer>

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("trainers/{id}") fun updateTrainer(@Path("id") id: Int, @Body trainer: Trainer) : Call<JsonResponse>

    //Workout
    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("workouts/{id}") fun getWorkout(@Path("id") id: Int): Call<Workout>

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("workouts/{id}") fun editWorkout(@Path("id") id: Int, @Body workout: Workout): Call<Workout>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("workouts/copy/{id}") fun copyWorkout(@Path("id") id: Int): Call<Workout>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("workouts/user/{id}") fun getUserWorkouts(@Path("id") user_id: Int): Call<WorkoutList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("workouts/user/{id}") fun getObservableUserWorkouts(@Path("id") user_id: Int): Observable<WorkoutList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("workouts") fun addWorkout(@Body workout: Workout): Call<JsonWorkoutResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @DELETE("workouts/{id}") fun deleteWorkout(@Path("id") workout_id: Int): Call<JsonResponse>

    //Exercises
    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("exercises") fun getExercises(): Observable<ExerciseList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("exercises") fun addExercise(@Body exercise: Exercise): Call<JsonResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("exercises/{id}") fun getExercise(@Path("id") exercise_id: Int): Call<Exercise>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("exercises/search") fun searchExercises(@Body json: JsonObject): Observable<ExerciseList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("exercises-request") fun getRequests(): Observable<ExerciseList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("exercises/{id}") fun approveRequest(@Path("id") exercise_id: Int): Call<JsonResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @DELETE("exercises/{id}") fun deleteExercise(@Path("id") exercise_id: Int) : Call<JsonResponse>

    //WorkoutExercises
    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("workouts/exercises") fun addWorkoutExercise(@Body json: JsonObject): Call<JsonResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("workouts/{id}/exercises") fun getWorkoutExercises(@Path("id") workout_id: Int): Observable<WorkoutExerciseList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @DELETE("workouts/{id}/exercises/{eid}") fun deleteWorkoutExercise(@Path("id") workout_id: Int, @Path("eid") exercise_id: Int): Call<JsonResponse>

    //ExerciseSets
    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("workouts/{id}/exercises/{eid}/sets") fun getWorkoutExerciseSets(@Path("id") workout_id: Int, @Path("eid") exercise_id: Int): Observable<ExerciseSetList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("workouts/exercises/sets") fun addWorkoutExerciseSet(@Body json: JsonObject):Call<JsonResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("workouts/exercises/sets/edit") fun editWorkoutExerciseSet(@Body json: JsonObject) : Call<JsonResponse>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("workouts/exercises/sets/del") fun deleteWorkoutExerciseSet(@Body json: JsonObject) : Call<JsonResponse>

    //muscle, type
    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("muscles") fun getMuscles(): Call<MuscleList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("muscles") fun getMusclesO(): Observable<MuscleList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("muscles") fun addMuscle(@Body muscle: Muscle): Call<Muscle>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("types") fun addType(@Body type: Type): Call<Type>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("types") fun getTypes(): Call<TypeList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("types") fun getTypesO(): Observable<TypeList>

    //TrainerClient
    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("trainer_clients") fun getAllTrainerClient(): Call<TrainerClientList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("trainer_clients/user/{id}") fun getUserTrainerC(@Path("id") user_id: Int): Call<TrainerClient>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("trainer_clients/user_c/{id}") fun getUserTrainers(@Path("id") user_id: Int): Call<TrainerClientList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("trainer_clients/trainer/{id}") fun getTrainerClients(@Path("id") trainer_id: Int): Observable<TrainerClientList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("trainer_clients") fun addTrainerClient(@Body trainerClient: TrainerClient): Call<TrainerClient>

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("trainer_clients/{id}") fun editTrainerClient(@Path("id") id: Int, @Body trainerClient: TrainerClient): Call<JsonResponse>

    //Notifications
    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("notifications/user/{id}") fun getUserNotifications(@Path("id") user_id: Int): Observable<NotificationList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("notifications/{id}") fun editNotification(@Path("id") id: Int, @Body notification: Notification): Call<Notification>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("notifications") fun addNotification(@Body notification: Notification): Call<Notification>

    //WorkoutNote
    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("notes") fun addNewNote(@Body note: WorkoutNote): Call<WorkoutNote>

    @Headers("Accept: application/json", "Content-type:application/json")
    @PUT("notes/{id}") fun editNote(@Path("id") id: Int, @Body note: WorkoutNote): Call<WorkoutNote>

    //TrainerReview
    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("reviews/trainer/{id}") fun getTrainerReviews(@Path("id") id: Int): Observable<TrainerReviewList>

    @Headers("Accept: application/json", "Content-type:application/json")
    @GET("reviews/{id}") fun getReview(@Path("id") id: Int): Call<TrainerReview>

    @Headers("Accept: application/json", "Content-type:application/json")
    @POST("reviews") fun addNewReview(@Body review: TrainerReview): Call<TrainerReview>


    companion object {
        fun create(): ApiCaller {
            val gsonbuild = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .registerTypeHierarchyAdapter(ByteArray::class.java, ByteArrayToBase64TypeAdapter()).create()
            val retrofit = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gsonbuild))
                .baseUrl("https://strength-app.herokuapp.com/api/")
                .build()

            return retrofit.create(ApiCaller::class.java)
        }
    }


    // Using Android's base64 libraries. This can be replaced with any base64 library.
    private class ByteArrayToBase64TypeAdapter : JsonSerializer<ByteArray>, JsonDeserializer<ByteArray> {
        override fun serialize(
            src: ByteArray?,
            typeOfSrc: java.lang.reflect.Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP))
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: java.lang.reflect.Type?,
            context: JsonDeserializationContext?
        ): ByteArray {
            return Base64.decode(json?.asString, Base64.NO_WRAP)
        }
    }
}