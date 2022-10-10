package org.liamjd.amber.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun MainMenu(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxHeight(0.2f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Amber Electric",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

        }
        Row(
            modifier = Modifier
                .fillMaxHeight(0.7f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Record Charge")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text("Record Journey")
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxHeight(0.1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Charge History")
            Text("Journey History")
        }

    }
}

@Preview
@Composable
fun MainMenuPreview() {
    MainMenu(navController = rememberNavController())
}