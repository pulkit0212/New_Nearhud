package common.neighbour.nearhud.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthorizationInterceptor(private val token : String) : Interceptor {

    private val TAG = "Interceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().signedRequest()
        return chain.proceed(newRequest)
    }


    private fun Request.signedRequest(): Request {
        return newBuilder()
            .header("Authorization", "Bearer ${token}")
            .build()
    }
}