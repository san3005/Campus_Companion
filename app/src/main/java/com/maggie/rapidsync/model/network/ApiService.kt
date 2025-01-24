package com.maggie.rapidsync.model.network

import com.maggie.rapidsync.model.pojo.Appointment
import com.maggie.rapidsync.model.pojo.AppointmentSchedule
import com.maggie.rapidsync.model.pojo.Course
import com.maggie.rapidsync.model.pojo.CourseEnrollment
import com.maggie.rapidsync.model.pojo.Department
import com.maggie.rapidsync.model.pojo.LoginRequest
import com.maggie.rapidsync.model.pojo.LoginResponse
import com.maggie.rapidsync.model.pojo.MapLocation
import com.maggie.rapidsync.model.pojo.MealPlan
import com.maggie.rapidsync.model.pojo.ParkingPlan
import com.maggie.rapidsync.model.pojo.ParkingSlot
import com.maggie.rapidsync.model.pojo.Restaurant
import com.maggie.rapidsync.model.pojo.SaveFcmRequest
import com.maggie.rapidsync.model.pojo.Semester
import com.maggie.rapidsync.model.pojo.StripeConfigRequest
import com.maggie.rapidsync.model.pojo.StripeConfigResponse
import com.maggie.rapidsync.model.pojo.Subject
import com.maggie.rapidsync.model.pojo.University
import com.maggie.rapidsync.model.pojo.UniversityEvent
import com.maggie.rapidsync.model.pojo.User
import com.maggie.rapidsync.model.pojo.UserMealPlan
import com.maggie.rapidsync.model.pojo.UserParkingPlan
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): Response<User>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): Response<User>

    @GET("parking-slots")
    suspend fun getParkingSlots(): Response<List<ParkingSlot>>

    @PUT("parking-slots/{id}")
    suspend fun updateParkingSlot(
        @Path("id") id: String,
        @Body parkingSlot: ParkingSlot
    ): Response<ParkingSlot>

    @GET("parking-plans")
    suspend fun getParkingPlans(): Response<List<ParkingPlan>>

    @POST("user-parking-plans")
    suspend fun bookParkingPlan(@Body userParkingPlan: UserParkingPlan): Response<UserParkingPlan>

    @GET("user-parking-plans/user/{id}")
    suspend fun getUserParkingPlans(@Path("id") id: String): Response<UserParkingPlan>

    @DELETE("user-parking-plans/{id}")
    suspend fun cancelParkingPlan(@Path("id") id: String): Response<UserParkingPlan>


    @GET("university-events")
    suspend fun getUniversityEvents(): Response<List<UniversityEvent>>

    @GET("restaurants")
    suspend fun getRestaurants(): Response<List<Restaurant>>

    @GET("meal-plans")
    suspend fun getMealPlans(): Response<List<MealPlan>>

    @GET("user-meal-plans/user/{id}")
    suspend fun getUserMealPlans(@Path("id") id: String): Response<UserMealPlan>

    @POST("user-meal-plans")
    suspend fun bookMealPlan(@Body userMealPlan: UserMealPlan): Response<UserMealPlan>

    @DELETE("user-meal-plans/{id}")
    suspend fun cancelMealPlan(@Path("id") id: String): Response<UserMealPlan>

    @GET("subjects")
    suspend fun getSubjects(): Response<List<Subject>>

    @GET("semesters")
    suspend fun getSemesters(): Response<List<Semester>>

    @GET("departments")
    suspend fun getDepartments(): Response<List<Department>>

    @GET("university")
    suspend fun getUniversity(): Response<University>

    @GET("courses")
    suspend fun getCourses(): Response<List<Course>>

    @GET("courses/semester/{id}")
    suspend fun getCoursesBySemester(@Path("id") id: String): Response<List<Course>>

    @GET("courses/department/{id}")
    suspend fun getCoursesByDepartment(@Path("id") id: String): Response<List<Course>>

    @GET("course-enrollments/student/{id}")
    suspend fun getCourseEnrollments(@Path("id") id: String): Response<List<CourseEnrollment>>

    @POST("course-enrollments")
    suspend fun enrollCourse(@Body courseEnrollment: CourseEnrollment): Response<CourseEnrollment>

    @DELETE("course-enrollments/{id}")
    suspend fun cancelCourseEnrollment(@Path("id") id: String): Response<CourseEnrollment>

    @GET("locations")
    suspend fun getLocations(): Response<List<MapLocation>>

    @GET("appointment_schedule/student/{id}")
    suspend fun getAppointmentSchedules(@Path("id") id: String): Response<List<Appointment>>

    @GET("appointment_schedule/teacher/{id}")
    suspend fun getTeacherAppointmentSchedules(@Path("id") id: String): Response<List<Appointment>>

    @GET("appointment_schedule")
    suspend fun getAppointmentSchedules(): Response<List<Appointment>>

    @PUT("appointment_schedule/{id}")
    suspend fun updateAppointmentSchedule(
        @Path("id") id: String,
        @Body appointmentSchedule: AppointmentSchedule
    ): Response<AppointmentSchedule>


    @PUT("appointment_schedule/{id}")
    suspend fun updateAppointment(@Path("id") id: String, @Body appointmentSchedule: AppointmentSchedule): Response<AppointmentSchedule>

    @GET("users/teachers")
    suspend fun getTeachers(): Response<List<User>>

    @POST("users/fcm-token")
    suspend fun saveFcmToken(@Body saveFcmRequest: SaveFcmRequest): Response<User>


    @POST("/stripe")
    suspend fun createPaymentIntent(@Body stripeConfigRequest: StripeConfigRequest): Response<StripeConfigResponse>


}