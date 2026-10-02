package com.swimgear.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.swimgear.app.ui.legacy.EquipamentosXmlActivity
import com.swimgear.app.ui.navigation.SwimGearNav
import com.swimgear.app.ui.theme.SwimGearTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as SwimGearApplication).repository
        setContent {
            SwimGearTheme {
                SwimGearNav(repository, abrirXml = {
                    startActivity(Intent(this, EquipamentosXmlActivity::class.java))
                })
            }
        }
    }
}
