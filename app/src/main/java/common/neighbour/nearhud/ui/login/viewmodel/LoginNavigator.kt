package common.neighbour.nearhud.ui.login.viewmodel

interface LoginNavigator {
    fun onLoading(isLoading:Boolean)
    fun OnErrorMessage(message:String)
}