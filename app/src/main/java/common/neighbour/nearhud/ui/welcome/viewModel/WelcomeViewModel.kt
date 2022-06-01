package common.neighbour.nearhud.ui.welcome.viewModel

import android.app.Application
import common.neighbour.nearhud.application.MyApplication
import common.neighbour.nearhud.core.BaseViewModel

class WelcomeViewModel(app: Application) : BaseViewModel(app) {

    fun getLaunchScreen() = MyApplication.repository.getLaunchScreen()
}