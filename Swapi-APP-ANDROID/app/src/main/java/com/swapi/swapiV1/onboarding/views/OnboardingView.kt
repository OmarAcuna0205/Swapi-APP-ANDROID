@file:OptIn(ExperimentalFoundationApi::class)

package com.swapi.swapiV1.onboarding.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.swapi.swapiV1.onboarding.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

/**
 * Vista principal del Onboarding. Orquesta el carrusel de páginas, los indicadores
 * y la navegación, sincronizando todo con el ViewModel.
 *
 * @param viewModel Maneja el estado de la página actual y la lista de contenido.
 * @param onFinish Callback que se ejecuta cuando el usuario completa el onboarding.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingView(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit = {}
) {
    // Recolectamos el estado del ViewModel para que la UI reaccione a cambios.
    val pages by viewModel.pages.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()

    // Configuración del Pager de Jetpack Compose.
    // initialPage asegura que si rotamos la pantalla, volvamos a la página correcta.
    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { pages.size }
    )

    // Scope para lanzar corrutinas de UI (como animaciones de scroll)
    val scope = rememberCoroutineScope()

    // Sincronización 1 (UI -> ViewModel):
    // Cuando el usuario desliza manualmente, actualizamos el ViewModel.
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setPage(pagerState.currentPage)
    }

    // Sincronización 2 (ViewModel -> UI):
    // Si el ViewModel cambia la página (ej. botones Next/Prev), forzamos al Pager a moverse.
    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.scrollToPage(currentPage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Componente de paginación horizontal (Carrusel)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Renderizamos cada página reutilizando la vista genérica
            OnboardingPageView(
                pageModel = pages[page],
                selected = page == pagerState.currentPage
            )
        }

        // Controles inferiores (Indicadores y Botones) superpuestos en la parte baja
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Indicador de puntos (dots)
            DotsIndicatorView(
                totalDots = pages.size,
                selectedIndex = currentPage
            )

            // Barra de navegación (Anterior / Siguiente / Empezar)
            BottomBarView(
                isLastPage = viewModel.isLastPage(),
                page = currentPage,
                total = pages.size,
                onPrev = {
                    if (currentPage > 0) {
                        // Usamos una corrutina para animar el scroll suavemente hacia atrás
                        scope.launch { pagerState.animateScrollToPage(currentPage - 1) }
                    }
                },
                onNext = {
                    if (!viewModel.isLastPage()) {
                        // Animamos el scroll hacia adelante
                        scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                    } else {
                        // Si es la última página, finalizamos el onboarding
                        onFinish()
                    }
                }
            )
        }
    }
}