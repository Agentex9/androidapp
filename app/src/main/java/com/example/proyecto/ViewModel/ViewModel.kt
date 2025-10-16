package com.example.proyecto.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.models.*
import com.example.proyecto.services.Services
import com.example.proyecto.utilities.TokenManager
import com.example.proyecto.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.proyecto.models.LoginResponse
import com.example.proyecto.models.APIToken


class GeneralViewModel : ViewModel() {

    // --------------------
    // StateFlows
    // --------------------
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter

    private val _phoneNumber = MutableStateFlow("")
    private val _otp = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber
    val otp: StateFlow<String> = _otp

    // Nuevo estado para cuando solo pedimos el OTP (LoginResponse)
    private val _otpRequestState = MutableStateFlow<ResultState<LoginResponse>>(ResultState.Idle)
    val otpRequestState: StateFlow<ResultState<LoginResponse>> = _otpRequestState

    // Estado de login real (TokenResponse)
    private val _loginState = MutableStateFlow<ResultState<TokenResponse>>(ResultState.Idle)
    val loginState: StateFlow<ResultState<TokenResponse>> = _loginState

    // Estado para OTP verification que devuelve APIToken
    private val _otpState = MutableStateFlow<ResultState<APIToken>>(ResultState.Idle)
    val otpState: StateFlow<ResultState<APIToken>> = _otpState

    private val _hostelListState = MutableStateFlow<HostelListState>(ResultState.Idle)
    val hostelListState: StateFlow<HostelListState> = _hostelListState

    private val _hostelServicesState = MutableStateFlow<HostelServicesState>(ResultState.Idle)
    val hostelServicesState: StateFlow<HostelServicesState> = _hostelServicesState

    private val _myHostelReservationsState = MutableStateFlow<MyReservationsState>(ResultState.Idle)
    val myHostelReservationsState: StateFlow<MyReservationsState> = _myHostelReservationsState

    private val _myServiceReservationsState =
        MutableStateFlow<ServiceReservationsState>(ResultState.Idle)
    val myServiceReservationsState: StateFlow<ServiceReservationsState> =
        _myServiceReservationsState

    private val _myUpcomingReservationsState =
        MutableStateFlow<myUpcomingReservationsState>(ResultState.Idle)
    val myUpcomingReservationsState: StateFlow<myUpcomingReservationsState> =
        _myUpcomingReservationsState

    private val _preRegistroState = MutableStateFlow<PreRegistroState>(ResultState.Idle)
    val preRegistroState: StateFlow<PreRegistroState> = _preRegistroState

    // --------------------
    // New Reservation States
    // --------------------
    private val _newHostelReservationState =
        MutableStateFlow<NewHostelReservationState>(ResultState.Idle)
    val newHostelReservationState: StateFlow<NewHostelReservationState> = _newHostelReservationState

    private val _newServiceReservationState =
        MutableStateFlow<NewServiceReservationState>(ResultState.Idle)
    val newServiceReservationState: StateFlow<NewServiceReservationState> =
        _newServiceReservationState


    private val _cancelReservationState = MutableStateFlow<ResultState<Unit>>(ResultState.Idle)
    val cancelReservationState: StateFlow<ResultState<Unit>> = _cancelReservationState


    // --------------------
    // Update fields
    // --------------------
    fun updatePhoneNumber(phone: String) {
        _phoneNumber.value = phone
    }

    fun updateOTP(otp: String) {
        _otp.value = otp
    }

    fun setHostelReservationState(){
        _newHostelReservationState.value = ResultState.Idle
    }
    fun setServiceReservationState(){
        _newServiceReservationState.value = ResultState.Idle
    }

    // functions for fetch once for loading menu screen
    private var _hasCheckedSession = false

    fun resetsession(){
        _hasCheckedSession = false
    }
    fun checkSessionOnce() {
        if (!_hasCheckedSession) {
            _hasCheckedSession = true
            fetchHostels()
        }
    }

    private var hasLoadedHostels = false
    private var hasLoadedServices = false

    fun fetchHostelsOnce() {
        if (!hasLoadedHostels) {
            hasLoadedHostels = true
            fetchHostels()
        }
    }

    fun fetchHostelServicesOnce() {
        if (!hasLoadedServices) {
            hasLoadedServices = true
            fetchHostelServices()
        }
    }

    // --------------------
    // API Calls
    // --------------------

    // Paso 1: Enviar teléfono → pedir OTP
    fun verifyLogin() {
        viewModelScope.launch {
            _otpRequestState.value = ResultState.Loading
            try {
                val request = VerificationLogin(phone_number = _phoneNumber.value)
                val response = Services.instance.verifyLogin(request)
                if (response.isSuccessful && response.body() != null) {
                    _otpRequestState.value = ResultState.Success(response.body()!!)
                } else {
                    _otpRequestState.value = ResultState.Error(response.message())
                }
            } catch (e: Exception) {
                _otpRequestState.value = ResultState.Error("Network error: ${e.message}")
            }
        }
    }

    // Paso 2: Validar OTP → obtener APIToken
    fun verifyOTP(context: Context) {
        viewModelScope.launch {
            _otpState.value = ResultState.Loading
            try {
                val request = VerificationOTP(
                    phone_number = _phoneNumber.value,
                    code = _otp.value
                )
                val response = Services.instance.verifyOtp(request)
                if (response.isSuccessful && response.body() != null) {
                    val apiToken = response.body()!!
                    // Guarda el access token
                    TokenManager(context).saveToken(apiToken.access)
                    _otpState.value = ResultState.Success(apiToken)
                } else {
                    _otpState.value = ResultState.Error(response.message())
                }
            } catch (e: Exception) {
                _otpState.value = ResultState.Error("Network error: ${e.message}")
            }
        }
    }

    // Paso 3 (opcional): login directo con phone + code → TokenResponse
    fun loginUser(context: Context) {
        viewModelScope.launch {
            _loginState.value = ResultState.Loading
            try {
                val request = PhoneVerificationRequest(
                    phone_number = _phoneNumber.value,
                    code = _otp.value
                )

                val response = Services.instance.userLogin(request)

                // Guardar el token
                TokenManager(context).saveToken(response.token)
                TokenManager(context).saveUser(response.user)

                _loginState.value = ResultState.Success(response)
            } catch (e: Exception) {
                _loginState.value = ResultState.Error("Network error: ${e.message}")
            }
        }
    }

    // --------------------
    // Hostels y Services
    // --------------------
    fun fetchHostels() {
        viewModelScope.launch {
            _hostelListState.value = ResultState.Loading
            try {
                val response = Services.instance.getHostels()
                if (response.isSuccessful && response.body() != null) {
                    _hostelListState.value = ResultState.Success(response.body()!!)
                } else {
                    _hostelListState.value = ResultState.Error(response.message())
                }
            } catch (e: Exception) {
                _hostelListState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchHostelServices() {
        viewModelScope.launch {
            _hostelServicesState.value = ResultState.Loading
            try {
                val response = Services.instance.getHostelServices()
                if (response.isSuccessful && response.body() != null) {
                    _hostelServicesState.value = ResultState.Success(response.body()!!)
                } else {
                    _hostelServicesState.value = ResultState.Error(response.message())
                }
            } catch (e: Exception) {
                _hostelServicesState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchMyHostelReservations() {
        viewModelScope.launch {
            _myHostelReservationsState.value = ResultState.Loading
            try {
                val response = Services.instance.getMyReservations()
                if (response.isSuccessful && response.body() != null) {
                    _myHostelReservationsState.value = ResultState.Success(response.body()!!)
                } else {
                    _myHostelReservationsState.value = ResultState.Error(response.message())
                }
            } catch (e: Exception) {
                _myHostelReservationsState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchMyServiceReservations() {
        viewModelScope.launch {
            _myServiceReservationsState.value = ResultState.Loading
            try {
                val response = Services.instance.getMyServiceReservations()
                if (response.isSuccessful && response.body() != null) {
                    _myServiceReservationsState.value = ResultState.Success(response.body()!!)
                } else {
                    _myServiceReservationsState.value = ResultState.Error(response.message())
                }
            } catch (e: Exception) {
                _myServiceReservationsState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchMyUpcomingServiceReservations() {
        viewModelScope.launch {
            _myUpcomingReservationsState.value = ResultState.Loading
            try {
                val response = Services.instance.getMyUpcomingServiceReservations()
                if (response.isSuccessful && response.body() != null) {
                    _myUpcomingReservationsState.value = ResultState.Success(response.body()!!)
                } else {
                    _myUpcomingReservationsState.value = ResultState.Error(response.message())
                }
            } catch (e: Exception) {
                _myUpcomingReservationsState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createHostelReservation(request: NewHostelReservation) {
        viewModelScope.launch {
            _newHostelReservationState.value = ResultState.Loading
            try {
                val response = Services.instance.createHostelReservation(request)
                if (response.isSuccessful && response.body() != null) {
                    _newHostelReservationState.value = ResultState.Success(response.body()!!)
                } else {
                    _newHostelReservationState.value = ResultState.Error(response.message())
                    val errorBody = response.errorBody()?.string()
                    Log.e("Reservation", "Error ${response.code()}: $errorBody")
                }
            } catch (e: Exception) {
                _newHostelReservationState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createServiceReservation(request: NewServiceReservation) {
        viewModelScope.launch {
            _newServiceReservationState.value = ResultState.Loading
            try {
                val response = Services.instance.createServiceReservation(request)
                if (response.isSuccessful && response.body() != null) {
                    _newServiceReservationState.value = ResultState.Success(response.body()!!)
                } else {
                    _newServiceReservationState.value = ResultState.Error(response.message())
                }
            } catch (e: Exception) {
                _newServiceReservationState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun submitPreRegistro(request: PreRegForm) {
        viewModelScope.launch {
            _preRegistroState.value = ResultState.Loading
            try {
                val response = Services.instance.enviarPreRegistro(request)
                if (response.isSuccessful && response.body() != null) {
                    _preRegistroState.value = ResultState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        response.message() ?: "Error desconocido (${response.code()})"
                    } else {
                        errorBody
                    }
                    _preRegistroState.value = ResultState.Error(errorMessage)
                    println("Error en API pre-registro: ${response.code()} - $errorMessage")
                }
            } catch (e: Exception) {
                _preRegistroState.value =
                    ResultState.Error(e.message ?: "Error de red o desconocido")
                println("Excepción en pre-registro: ${e.message}")
            }
        }
    }

    fun resetCancelState() {
        _cancelReservationState.value = ResultState.Idle
    }

    fun cancelHostelReservation(reservationId: String) {
        viewModelScope.launch {
            _cancelReservationState.value = ResultState.Loading
            try {
                val response = Services.instance.cancelReservation(
                    reservationId,
                    Status("cancelled")
                )

                if (response.isSuccessful) {
                    _cancelReservationState.value = ResultState.Success(Unit)
                    fetchMyHostelReservations()
                } else {
                    _cancelReservationState.value = ResultState.Error(
                        "Error ${response.code()}: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _cancelReservationState.value =
                    ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun cancelServiceReservation(reservationId: String) {
        viewModelScope.launch {
            _cancelReservationState.value = ResultState.Loading
            try {
                val response = Services.instance.cancelServiceReservation(
                    reservationId,
                    Status("cancelled")
                )

                if (response.isSuccessful) {
                    _cancelReservationState.value = ResultState.Success(Unit)
                    fetchMyServiceReservations()
                    fetchMyUpcomingServiceReservations()
                } else {
                    _cancelReservationState.value = ResultState.Error(
                        "Error ${response.code()}: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _cancelReservationState.value =
                    ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
