package com.example.kmp_template.shared_client.app

import androidx.lifecycle.ViewModel
import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.config.model.LogLevel
import com.example.kmp_template.core.config.model.LoggingConfig
import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.shared_client.config.AppConfigProvider
import com.example.kmp_template.shared_client.core.StringValue
import com.example.kmp_template.shared_client.person.domain.model.Person
import com.example.kmp_template.shared_client.person.domain.repository.PersonRepository
import com.example.kmp_template.shared_client.core.toast.ToastService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlin.uuid.Uuid

class AppStartupViewModel(
    private val appConfigProvider: AppConfigProvider,
    private val toastService: ToastService,
    private val systemDirs: SystemAppDirectories,
    private val personRepo: PersonRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "AppStartupViewModel"
    }

    private val _state = MutableStateFlow<AppStartupState>(AppStartupState.Initializing)
    val state = _state.asStateFlow()

    init {
        initializeApp()
    }

    fun initializeApp() {
        toastService.showInfo(StringValue.Raw("Initializing application..."))

        // Do here any startup initialization tasks if needed
        initLogger()
        initPeople()

        _state.update { AppStartupState.Ready }
        toastService.showSuccess(StringValue.Raw("Application is ready."))
    }

    private fun initLogger() {
        val config = appConfigProvider.config.value
        Log.initialize(config.logging, systemDirs)
        Log.tag(TAG).d { "Logger initialized" }
    }

    private fun initPeople() {
        // INFO:
        //  Blocking call here is fine since we are still in the startup phase
        //  we want to ensure this is done before proceeding.
        //  consider adding a progress indicator if this takes too long.
        runBlocking {
            personRepo.deleteAllPeople()

            val person1 = Person(id = Uuid.random(), name = "John Doe", local = true)
            personRepo.savePerson(person1)

            val person2 = Person(id = Uuid.random(), name = "Jane Smith", local = false)
            personRepo.savePerson(person2)

            Log.tag(TAG).d { "People initialized" }
        }
    }
}