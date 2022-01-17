package ir.itoll.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ir.itoll.home.ui.theme.HomeTheme

class MainActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HomeTheme {
                ProvideWindowInsets() {
                    HomeScreen()
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun HomeScreen() {
    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight

    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )

//         setStatusBarsColor() and setNavigationBarsColor() also exist
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (header, list) = createRefs()
        val scrollState = rememberLazyListState()

        var currentHeaderState by remember {
            mutableStateOf(HeaderState.Expanded)
        }

        val transition = updateTransition(currentHeaderState, label = "transition")

        val headerHeight by transition.animateDp(
            label = "headerHeight",
            transitionSpec =
            {
                spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
            }
        ) { state ->
            when (state) {
                HeaderState.Expanded -> 350.dp
                HeaderState.Collapsed -> 200.dp
            }
        }

//        val imageHeight = 170.dp
        val cardHeight by transition.animateDp(label = "cardHeight") { state ->
            when (state) {
                HeaderState.Expanded -> 200.dp
                HeaderState.Collapsed -> 80.dp
            }
        }

        /* val imageHeight by transition.animateDp(label = "imageMask") { state ->
             when (state) {
                 HeaderState.Expanded -> 250.dp
                 HeaderState.Collapsed -> 150.dp
             }
         }*/

        val imageHeight by transition.animateFloat(label = "imageMask",
            transitionSpec =
            {
                spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
            }
        ) { state ->
            when (state) {
                HeaderState.Expanded -> with(LocalDensity.current) { 280.dp.toPx() }
                HeaderState.Collapsed -> with(LocalDensity.current) { 150.dp.toPx() }
            }
        }

        val cardSidePadding by transition.animateDp(label = "cardSidePadding") { state ->
            when (state) {
                HeaderState.Expanded -> 24.dp
                HeaderState.Collapsed -> 8.dp
            }
        }


        var lastVisibleIndex by remember {
            mutableStateOf(0)
        }

        var isScrollingDown by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = scrollState.firstVisibleItemIndex) {
            isScrollingDown = lastVisibleIndex < scrollState.firstVisibleItemIndex
            lastVisibleIndex = scrollState.firstVisibleItemIndex
            currentHeaderState =
                if (isScrollingDown) HeaderState.Collapsed else HeaderState.Expanded
        }


        // header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(color = Color.Transparent)
                .zIndex(1f)
                .constrainAs(header) {
                    top.linkTo(parent.top, 0.dp)
                    start.linkTo(parent.start, 0.dp)
                    end.linkTo(parent.end, 0.dp)
                }
        ) {

            Image(
                painter = painterResource(id = R.drawable.header),
                contentDescription = "header",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(CustomShape(imageHeight)),
//                    .height(imageHeight)
//                    .clip(CustomShape(imageHeight)),
                contentScale = ContentScale.Fit,
                alignment = Alignment.TopCenter
            )
            // banner
            /* Surface(
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(imageMaskHeight)
                     .align(alignment = Alignment.BottomCenter)
                     .padding(bottom = 16.dp),
                 color = Color.Blue
             ) {}*/

            //license box
            Card(
                modifier = Modifier
                    .padding(start = cardSidePadding, end = cardSidePadding, bottom = 16.dp)
                    .fillMaxWidth()
                    .height(cardHeight)
                    .align(alignment = Alignment.BottomCenter),
//                    .zIndex(1f),
                backgroundColor = Color.White,
                shape = RoundedCornerShape(6.dp),
                elevation = 8.dp

            ) {
                Text(text = "Sample")
            }
        }

        // rest of contents

        CompositionLocalProvider(LocalOverScrollConfiguration provides null) {
            LazyColumn(
                modifier = Modifier
                    .constrainAs(list) {
                        top.linkTo(header.bottom, (-30).dp)
                        start.linkTo(parent.start, 0.dp)
                        end.linkTo(parent.end, 0.dp)
                        bottom.linkTo(parent.bottom, 8.dp)
                    },
                state = scrollState,
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 200.dp,
                    bottom = 200.dp,
                )
            ) {
                items(100) {
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        color = Color.LightGray,
                    ) {
                        Text(
                            text = it.toString(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HomeTheme {
        HomeScreen()
    }
}

enum class HeaderState {
    Expanded,
    Collapsed
}

class CustomShape(private val imageHeight: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {


        val path = Path().apply {

            lineTo(size.width, 0f)
            lineTo(size.width, imageHeight)
            lineTo(0f, imageHeight)

            close()
        }
        return Outline.Generic(path = path)
    }
}
