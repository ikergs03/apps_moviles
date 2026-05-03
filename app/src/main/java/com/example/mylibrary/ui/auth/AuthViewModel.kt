package com.example.mylibrary.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylibrary.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>(repository.currentUser())
    val user: LiveData<FirebaseUser?> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun register(email: String, password: String) {
        viewModelScope.launch {
            repository.register(email, password)
                .onSuccess { _user.postValue(it) }
                .onFailure { _error.postValue(it.message ?: "Error al registrar") }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password)
                .onSuccess { _user.postValue(it) }
                .onFailure { _error.postValue(it.message ?: "Error al iniciar sesión") }
        }
    }

    fun logout() {
        repository.logout()
        _user.value = null
    }
}