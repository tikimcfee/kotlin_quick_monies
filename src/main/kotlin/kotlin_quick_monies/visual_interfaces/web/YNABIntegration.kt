package kotlin_quick_monies.visual_interfaces.web

import com.beust.klaxon.Klaxon
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

object YNABIntegration {
    
    
    
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestWithTokenHeader = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $temporaryApiToken")
                .build()
            chain.proceed(requestWithTokenHeader)
        }
        .build()
    
    private val mainRetrofit = Retrofit.Builder()
        .baseUrl("https://api.youneedabudget.com/v1/")
        .client(httpClient)
        .build()
    
    private val mainService = mainRetrofit.create(YNABService::class.java)
    
    private val jsonParser = Klaxon()
    
    fun testFetch() {
        mainService.getUser().enqueue(
            object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    println(call)
                    println(t)
                }
                
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    println(call)
                    println(response)
                    
                    response.body()?.let {
                        with(it.string()) {
                            jsonParser.parse<UserResponse>(this).let {
                                println(it)
                            }
                        }
                        
                        it.close()
                    }
                }
                
            }
        )
    }
    
}

interface YNABService {
    @GET("user")
    fun getUser(): Call<ResponseBody>
}

data class UserResponse(
    val data: UserWrapper
)

data class UserWrapper(
    val user: User
)

data class User(
    val id: String
)
