package com.swimgear.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
import com.swimgear.app.repository.EquipamentoRepository
import com.swimgear.app.ui.detalhes.DetalhesScreen
import com.swimgear.app.ui.formulario.FormularioScreen
import com.swimgear.app.ui.lista.ListaScreen
import com.swimgear.app.viewmodel.*
import kotlinx.serialization.Serializable

@Serializable data object ListaRoute : NavKey
@Serializable data class DetalhesRoute(val id: String) : NavKey
@Serializable data class FormularioRoute(val id: String? = null) : NavKey

@Composable
fun SwimGearNav(repository: EquipamentoRepository, abrirXml: () -> Unit) {
    val backStack = rememberNavBackStack(ListaRoute)
    fun voltar() { if (backStack.size > 1) backStack.removeLastOrNull() }
    NavDisplay(
        backStack = backStack,
        onBack = { voltar() },
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()),
        entryProvider = entryProvider {
            entry<ListaRoute> {
                val vm: ListaViewModel = viewModel(factory = viewModelFactory {
                    initializer { ListaViewModel(repository, createSavedStateHandle()) }
                })
                ListaScreen(vm,
                    abrir = { id -> if (backStack.lastOrNull() == ListaRoute) backStack.add(DetalhesRoute(id)) },
                    adicionar = { if (backStack.lastOrNull() == ListaRoute) backStack.add(FormularioRoute()) },
                    abrirXml = abrirXml)
            }
            entry<DetalhesRoute> { route ->
                val vm: DetalhesViewModel = viewModel(factory = viewModelFactory {
                    initializer { DetalhesViewModel(route.id, repository, createSavedStateHandle()) }
                })
                DetalhesScreen(vm, voltar = { if (backStack.lastOrNull() == route) voltar() },
                    editar = { if (backStack.lastOrNull() == route) backStack.add(FormularioRoute(route.id)) })
            }
            entry<FormularioRoute> { route ->
                val vm: FormularioViewModel = viewModel(factory = viewModelFactory {
                    initializer { FormularioViewModel(route.id, repository, createSavedStateHandle()) }
                })
                FormularioScreen(vm, route.id != null, voltar = { if (backStack.lastOrNull() == route) voltar() })
            }
        },
    )
}
