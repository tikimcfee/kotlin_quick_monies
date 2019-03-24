package kotlin_quick_monies.visual_interfaces.web

import kotlin_quick_monies.functionality.commands.CommandHistorian
import kotlin_quick_monies.functionality.json.JsonTools.jsonParser
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

typealias THE_TEST_TYPE = YNABBudgetDetail.BudgetDetailResponse

object YNABIntegration {
    
    private val temporaryApiToken: String
    private val mainBudgetId: String
    
    init {
        with(CommandHistorian()) {
            with(readYnabIntegrationData()) {
                temporaryApiToken = first()
                mainBudgetId = last()
                
                println("--- YNAB integration started ---")
                println("--- token = $temporaryApiToken")
                println("--- budgetId = $mainBudgetId")
            }
        }
    }
    
    
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
        .addConverterFactory(MoshiConverterFactory.create(jsonParser))
        .build()
    
    private val mainService = mainRetrofit.create(YNABService::class.java)
    
    
    fun testFetch() {
        mainService.getBudgetById(mainBudgetId).enqueue(
            object : Callback<THE_TEST_TYPE> {
                override fun onFailure(call: Call<THE_TEST_TYPE>, t: Throwable) {
                
                }
                
                override fun onResponse(call: Call<THE_TEST_TYPE>, response: Response<THE_TEST_TYPE>) {
                    response.body()?.data?.budget?.categories?.forEach {
                        println(it)
                    }
                }
                
            }
        )
    }
    
}

interface YNABService {
    @GET("user")
    fun getUser(): Call<YNABUser.UserResponse>
    
    @GET("budgets")
    fun getAllBudgets(): Call<YNABBudget.BudgetSummaryResponse>
    
    @GET("budgets/{budgetId}")
    fun getBudgetById(@Path("budgetId") budgetId: String): Call<YNABBudgetDetail.BudgetDetailResponse>
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
        val last_modified_on: String
    )
}

object YNABBudgetDetail {
    
    data class BudgetDetailResponse(
        val data: BudgetDetailWrapper
    )
    
    data class BudgetDetailWrapper(
        val budget: BudgetDetail
    )
    
    data class BudgetDetail(
        val categories: List<Category>
    )
    
    data class Category(
        val name: String,
        val hidden: Boolean,
        val goal_target: Int
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


