package common.neighbour.nearhud.repositories.methods

interface FirbaseAuthActions {

    fun VerificationCodeSent()
    fun startActivity(uid: String, phoneNumber: String?)
    fun Error(message : String )
    fun TimeOut()
}