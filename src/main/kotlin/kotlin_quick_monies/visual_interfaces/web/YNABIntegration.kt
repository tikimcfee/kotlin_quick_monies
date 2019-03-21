package kotlin_quick_monies.visual_interfaces.web

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

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
    
    private val jsonParser = Moshi.Builder().build()
    
    private val ynabUserAdapter = jsonParser.adapter(YNABUser.UserResponse::class.java)
    private val ynabBudgetAdapter = jsonParser.adapter(YNABBudget.BudgetSummaryResponse::class.java)
    
    
    fun testFetch() {
        mainService.getAllBudgets().enqueue(
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
                            println(ynabBudgetAdapter.fromJson(this))
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
    
    @GET("budgets")
    fun getAllBudgets(): Call<ResponseBody>
    
    @GET("budgets/{budgetId}")
    fun getBudgetById(@Path("budgetId") budgetId: String): Call<ResponseBody>
}

object YNABBudget {
    
    data class BudgetSummaryResponse(
        val data: BudgetSummaryWrapper
    )
    
    data class BudgetSummaryWrapper(
        val budgets: List<BudgetSummary>
    )
    
    data class BudgetSummary(
        val id: String,
        val name: String,
        val last_modified_on: String,
        val date_format: String?
    )
    
}

object YNABUser {
    data class UserResponse(
        val data: UserWrapper
    )
    
    data class UserWrapper(
        val user: User
    )
    
    data class User(
        val id: String
    )
}


