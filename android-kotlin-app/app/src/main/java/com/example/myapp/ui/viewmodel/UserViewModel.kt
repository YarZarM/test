package com.example.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.model.User
import com.example.myapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null,
    val savedUserName: String = "",
    val savedUserEmail: String = ""
)

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadUsers()
        observeSavedData()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getUsers()
                .onSuccess { users ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        users = users
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
        }
    }

    fun refreshUsers() {
        loadUsers()
    }

    fun saveUserData(name: String, email: String) {
        viewModelScope.launch {
            repository.saveUserName(name)
            repository.saveUserEmail(email)
        }
    }

    private fun observeSavedData() {
        viewModelScope.launch {
            repository.getUserName().collect { name ->
                _uiState.value = _uiState.value.copy(savedUserName = name)
            }
        }
        viewModelScope.launch {
            repository.getUserEmail().collect { email ->
                _uiState.value = _uiState.value.copy(savedUserEmail = email)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

