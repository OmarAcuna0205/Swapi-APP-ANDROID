package com.swapi.androidClassMp1.onboarding.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.swapi.androidClassMp1.R
import com.swapi.androidClassMp1.onboarding.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

@Composable
fun OnboardingView(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit = {}
) {
    val pages by viewModel.pages.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { pages.size }
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Sincronización entre ViewModel y PagerState
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setPage(pagerState.currentPage)
    }
    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.scrollToPage(currentPage)
        }
    }

    // CAMBIO: Usamos un Box para superponer los elementos.
    Box(modifier = Modifier.fillMaxSize()) {
        // FONDO: El Pager ocupa toda la pantalla.
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Le pasamos si la página está seleccionada para la animación
            OnboardingPageView(pageModel = pages[page], selected = page == pagerState.currentPage)
        }

        // CONTROLES: Una columna en la parte inferior para los puntos y botones.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Alinear en la parte inferior del Box
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            DotsIndicatorView(
                totalDots = pages.size,
                selectedIndex = currentPage
            )

            BottomBarView(
                isLastPage = viewModel.isLastPage(),
                page = currentPage,
                total = pages.size,
                onPrev = {
                    if (currentPage > 0) {
                        scope.launch { pagerState.animateScrollToPage(currentPage - 1) }
                    }
                },
                onNext = {
                    if (!viewModel.isLastPage()) {
                        scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                    } else {
                        onFinish()
                        Toast.makeText(
                            context,
                            context.getString(R.string.onboarding_finished_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }
}