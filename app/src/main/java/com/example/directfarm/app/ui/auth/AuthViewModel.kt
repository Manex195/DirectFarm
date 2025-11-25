package com.example.directfarm.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.directfarm.app.data.model.User
import com.example.directfarm.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableLiveData<Result<User>>()
    val loginState: LiveData<Result<User>> = _loginState

    private val _registerState = MutableLiveData<Result<User>>()
    val registerState: LiveData<Result<User>> = _registerState

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun login(email: String, password: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginState.value = result
            _loading.value = false
        }
    }

    fun register(email: String, password: String, user: User) {
        _loading.value = true
        viewModelScope.launch {
            val result = repository.register(email, password, user)
            _registerState.value = result
            _loading.value = false
        }
    }
}