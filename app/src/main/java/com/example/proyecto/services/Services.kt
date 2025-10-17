package com.example.proyecto.services

import com.example.proyecto.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import com.example.proyecto.models.PreRegForm
import com.example.proyecto.models.PreRegResponse
import com.example.proyecto.models.AdminLoginRequest
import com.example.proyecto.models.PhoneVerificationRequest
import com.example.proyecto.models.TokenResponse
import retrofit2.http.PATCH
import retrofit2.http.Path


interface Services {

    // Send phone number for user verification and receive OTP
    @POST("users/phone-verification/send/")
    @Headers("No-Auth: true")
    suspend fun verifyLogin(@Body request: VerificationLogin): Response<LoginResponse>

    // Verify OTP
    @POST("users/phone-verification/verify/")
    @Headers("No-Auth: true")
    suspend fun verifyOtp(@Body request: VerificationOTP): Response<APIToken>

    // Fetch hostels
    @GET("albergues/hostels/")
    @Headers("No-Auth: false")
    suspend fun getHostels(): Response<HostelList>

    // Fetch user's hostel reservations
    @GET("albergues/reservations/my_reservations/")
    @Headers("No-Auth: false")
    suspend fun getMyReservations(): Response<MyHostelReservationList>

    // Create a new hostel reservation
    @POST("albergues/reservations/")
    @Headers("No-Auth: false")
    suspend fun createHostelReservation(@Body request: NewHostelReservation): Response<NewHostelReservation>

    // Update Status enpoint
    @PATCH("/api/albergues/reservations/{id}/")
    @Headers("No-Auth: false")
    suspend fun cancelReservation(@Path("id") id: String, @Body request: Status): Response<Unit>

    // Fetch hostel services
    @GET("services/hostel-services/")
    @Headers("No-Auth: false")
    suspend fun getHostelServices(): Response<HostelServicesList>

    // Fetch user's service reservations
    @GET("services/reservations/my_reservations/")
    @Headers("No-Auth: false")
    suspend fun getMyServiceReservations(): Response<MyServiceReservationList>

    // Fetch user's upcoming service reservations 24 hours
    @GET("services/reservations/upcoming/")
    @Headers("No-Auth: false")
    suspend fun getMyUpcomingServiceReservations(): Response<Myupcomingreservations>

    // Create a new service reservation
    @POST("services/reservations/")
    @Headers("No-Auth: false")
    suspend fun createServiceReservation(@Body request: NewServiceReservation): Response<NewServiceReservation>

    // Pre-register user
    @POST("users/pre-register/") // <-- REEMPLAZA con tu endpoint real para el pre-registro
    suspend fun enviarPreRegistro(@Body preRegData: PreRegForm): Response<PreRegResponse>

    @POST("users/auth/admin-login/")
    suspend fun adminLogin(@Body req: AdminLoginRequest): TokenResponse

    @PATCH("/api/services/reservations/{id}/")
    @Headers("No-Auth: false")
    suspend fun cancelServiceReservation(@Path("id") id: String, @Body request: Status): Response<Unit>
    @POST("users/phone-verification/verify/")
    suspend fun userLogin(@Body req: PhoneVerificationRequest): TokenResponse
    companion object {
        private val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        private val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val original = chain.request()
                val noAuthHeader = original.header("No-Auth")
                val requestBuilder = original.newBuilder()

                if (noAuthHeader == "false") {
                    val token = com.example.proyecto.models.MyApp.tokenManager.getToken()
                    // uncomment and add your default token if needed for testing fb4b170b600554835658869245a1e12b74f1f9bf
                    requestBuilder.addHeader("Authorization", "Token 79d53b87d4e3823f9af4088eaa44d611bbf4d1db")

                    // 79d53b87d4e3823f9af4088eaa44d611bbf4d1db
                    if (token != null) {
                        requestBuilder.addHeader("Authorization", "Token $token")
                    }
                }

                requestBuilder.removeHeader("No-Auth")
                chain.proceed(requestBuilder.build())
            }
            .build()

        val instance: Services = Retrofit.Builder()
                .baseUrl("http://20.246.91.21:8001/api/") // replace with LAN IP if using emulator/device
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(Services::class.java)
    }
}
