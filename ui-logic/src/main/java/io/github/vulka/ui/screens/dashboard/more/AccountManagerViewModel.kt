package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vulka.database.Credentials
import io.github.vulka.database.Repository
import io.github.vulka.ui.screens.Welcome
import io.github.vulka.ui.screens.dashboard.Home
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AccountManagerViewModel @Inject constructor(
    // TODO: Fix hilt/dagger errors
    private val repository: Repository
) : ViewModel() {
    val credentials = mutableStateListOf(*repository.credentials.getAll().toTypedArray())

    private var selfDelete by mutableStateOf(false)

    fun onDeleteClick(args: AccountManager, credentials: Credentials, navController: NavController) {
        clearAccountData(credentials)

        selfDelete = UUID.fromString(args.userId) == credentials.id
        this.credentials.remove(credentials)

        check(navController)
    }

    private fun clearAccountData(credentials: Credentials) {
        repository.grades.deleteByCredentialsId(credentials.id)
        repository.timetable.deleteByCredentialsId(credentials.id)
        repository.semesters.deleteByCredentialsId(credentials.id)
        repository.luckyNumber.deleteByCredentialsId(credentials.id)

        repository.credentials.delete(credentials)
    }

    fun check(navController: NavController) {
        if (repository.credentials.count() == 0) {
            navController.navigate(Welcome) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = false
                    inclusive = true
                }
            }
            return
        }

        // Navigate to first credential if current selected was deleted
        if (selfDelete) {
            val firstCredential = repository.credentials.get()!!

            navController.navigate(
                Home(
                    userId = firstCredential.id.toString(),
                    credentials = firstCredential.data,
                    platform = firstCredential.platform,
                    firstSync = false
                )
            ) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = false
                    inclusive = true
                }
            }
        }
    }
}