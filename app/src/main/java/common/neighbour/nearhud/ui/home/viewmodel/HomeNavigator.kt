package common.neighbour.nearhud.ui.home.viewmodel

interface HomeNavigator {
    fun onLoading(isLoading:Boolean)
    fun OnErrorMessage(message:String)
    fun Logout()


}