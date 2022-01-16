package ir.itoll.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import ir.itoll.home.ui.theme.HomeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ) {
        val (header, list) = createRefs()
        val scrollState = rememberLazyListState()

        var lastVisibleIndex by remember {
            mutableStateOf(0)
        }

        var isScrollingDown by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = scrollState.firstVisibleItemIndex ){
            isScrollingDown = lastVisibleIndex < scrollState.firstVisibleItemIndex
            lastVisibleIndex = scrollState.firstVisibleItemIndex
        }

        val headerHeight by animateDpAsState(
            targetValue = if (isScrollingDown) 100.dp else 340.dp
        )

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
            // baner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .align(alignment = Alignment.TopCenter),
                color = Color.Blue
            ) {}

            //license box
            Card(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(alignment = Alignment.BottomCenter),
                backgroundColor = Color.White,
                shape = RoundedCornerShape(6.dp),
                elevation = 4.dp

            ) {
                Text(text = "Sample")
            }
        }

        // rest of contents
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HomeTheme {
        HomeScreen()
    }
}