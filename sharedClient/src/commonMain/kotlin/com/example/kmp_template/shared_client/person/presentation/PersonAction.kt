package com.example.kmp_template.shared_client.person.presentation

import androidx.compose.runtime.Immutable

@Immutable
sealed interface PersonAction {
    data object OnBackClicked : PersonAction

    data object OnLoadRemotePeopleClicked: PersonAction

    sealed interface LoadedPerson : PersonAction {
        data class OnNameChanged(val form: PersonState.PersonForm) : LoadedPerson
        data class OnDeletePerson(val form: PersonState.PersonForm) : LoadedPerson
        data class OnSavePerson(val form: PersonState.PersonForm) : LoadedPerson
    }

    sealed interface NewPerson : PersonAction {
        data class OnNameChanged(val name: String) : NewPerson
        data class OnLocalChanged(val local: Boolean) : NewPerson
        data object OnSavePerson : NewPerson
    }
}
