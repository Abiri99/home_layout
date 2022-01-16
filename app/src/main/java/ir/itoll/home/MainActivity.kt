package ir.itoll.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

        // setStatusBarsColor() and setNavigationBarsColor() also exist
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ) {
        val (header, list) = createRefs()
        val scrollState = rememberLazyListState()

        var currentState by remember {
            mutableStateOf(HeaderState.Expanded)
        }

        val transition = updateTransition(currentState, label = "transition")

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

        val imageHeight = 170.dp
        val cardHeight  by transition.animateDp(label = "cardHeight") { state->
            when (state) {
                HeaderState.Expanded -> 200.dp
                HeaderState.Collapsed -> 80.dp
            }
        }

        val imageMaskHeight by transition.animateDp(label = "imageMask") { state ->
            when (state) {
                HeaderState.Expanded -> 180.dp
                HeaderState.Collapsed -> 50.dp
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
            currentState = if (isScrollingDown) HeaderState.Collapsed else HeaderState.Expanded
        }

        /*val headerHeight by animateDpAsState(
            targetValue = if (isScrollingDown) 100.dp else 340.dp,
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow)
        )*/


        // header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(color = Color.Transparent)
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
                    .height(imageHeight),
                contentScale = ContentScale.Crop
            )
            // banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageMaskHeight)
                    .align(alignment = Alignment.BottomCenter),
                color = Color.Blue
            ) {}

            //license box
            Card(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .height(cardHeight)
                    .align(alignment = Alignment.BottomCenter),
                backgroundColor = Color.White,
                shape = RoundedCornerShape(6.dp),
                elevation = 4.dp

            ) {
                Text(text = "Sample")
            }
        }

        // rest of contents

        CompositionLocalProvider(LocalOverScrollConfiguration provides null) {
            LazyColumn(
                modifier = Modifier.constrainAs(list) {
                    top.linkTo(header.bottom, (-16).dp)
                    start.linkTo(parent.start, 0.dp)
                    end.linkTo(parent.end, 0.dp)
                },
                state = scrollState,
            ) {
                items(100) {
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        color = Color.Red,
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