package com.example.proyecto.data


import com.example.proyecto.models.*


sealed class ResultState<out T> {
    object Idle : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val message: String) : ResultState<Nothing>()
}

typealias LoginState = ResultState<LoginResponse>
typealias OTPTState = ResultState<APIToken>
typealias HostelListState = ResultState<HostelList>
typealias MyReservationsState = ResultState<MyHostelReservationList>
typealias HostelServicesState = ResultState<HostelServicesList>
typealias ServiceReservationsState = ResultState<MyServiceReservationList>
typealias NewHostelReservationState = ResultState<NewHostelReservation>
typealias NewServiceReservationState = ResultState<NewServiceReservation>
typealias PreRegistroState = ResultState<PreRegResponse>
typealias myUpcomingReservationsState = ResultState<Myupcomingreservations>

