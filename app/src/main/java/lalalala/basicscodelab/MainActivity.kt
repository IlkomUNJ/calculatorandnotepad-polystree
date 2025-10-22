package lalalala.basicscodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import lalalala.basicscodelab.ui.theme.BasicsCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicsCodelabTheme { val navController = rememberNavController()
                Scaffold { innerPadding ->
                    AppNav(
                        modifier = Modifier
                            .padding(innerPadding),
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun AppNav(modifier: Modifier ,navController: NavHostController) {
    NavHost(navController, startDestination = "mainMenu") {
        composable("mainMenu") {
            MainMenu(onNavigate = { navController.navigate(it) })
        }
        composable("calculator") {
            ScientificCalculatorScreen()
        }
        composable("editor") {
            TextScreen()
        }
    }
}

@Composable
fun MainMenu(
    onNavigate: (String) -> Unit = { }
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Main Menu")
        Row(Modifier
            .padding(vertical = 24.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onNavigate("calculator") }
            ) {
                Text("Calculator")
            }
            Button(
                onClick = { onNavigate("editor") }
            ) {
                Text("Text Editor")
            }
        }
    }
}


//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    var expanded by rememberSaveable { mutableStateOf(false) }
//    val extraPadding by animateDpAsState(
//        if (expanded) 48.dp else 0.dp,
//        animationSpec = spring(
//            dampingRatio = Spring.DampingRatioMediumBouncy,
//            stiffness = Spring.StiffnessLow
//        )
//    )
//    Surface(
//        color = MaterialTheme.colorScheme.primary,
//        modifier = modifier
//            .padding(vertical = 4.dp, horizontal = 8.dp)
//            .clip(RoundedCornerShape(12.dp))
//    ) {
//        Row(modifier = modifier
//            .padding(vertical = 12.dp, horizontal = 24.dp)
//            .fillMaxWidth())
//        {
//            Column(modifier = Modifier
//                .weight(1f)
//                .padding(bottom = extraPadding.coerceAtLeast(0.dp))) {
//                Text(text = "Hello ")
//                Text(text = name)
//            }
//            ElevatedButton(
//                modifier = modifier
//                    .clip(RoundedCornerShape(16.dp)),
//                onClick = { expanded = !expanded}
//            ) {
//                Text(if (expanded) "Show less" else "Show more")
//            }
//        }
//    }
//}
//
//@Composable
//fun MyApp(
//    modifier: Modifier = Modifier,
//    names: List<String> = List(1000) { "$it" }
//) {
//    Surface(
//        modifier = modifier,
//        color = MaterialTheme.colorScheme.background
//
//    ) {
//        Column(
//            modifier = modifier.fillMaxSize(1f)
//        ) {
//            var shouldShowOnboarding by rememberSaveable { mutableStateOf(true)}
//            if (shouldShowOnboarding) {
//                OnboardingScreen(onCLick = {shouldShowOnboarding = false})
//            } else {
//                LazyColumn(modifier = modifier
//                    .padding(vertical = 4.dp)) {
//                    items(items = names) { name ->
//                        Greeting(name = name)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun OnboardingScreen(
//    modifier: Modifier = Modifier,
//    onCLick: () -> Unit) {
//    Column(
//        modifier = modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Welcome to the Basics Codelab!")
//        Button(
//            modifier = Modifier.padding(vertical = 24.dp),
//            onClick = onCLick
//        ) {
//            Text("Continue")
//        }
//    }
//}
//
//@Preview
//@Composable
//fun MyAppPreview(modifier: Modifier = Modifier) {
//    MyApp()
//}
