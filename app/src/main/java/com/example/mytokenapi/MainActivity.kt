package com.example.mytokenapi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mytokenapi.databinding.ActivityMainBinding
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonToken.setOnClickListener {
            // Создание экземпляра Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("https://hh.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Создание интерфейса для API
            val apiService = retrofit.create(ApiService::class.java)

            // Выполнение POST-запроса и обработка ответа
            val call = apiService.getToken(
                "client_credentials",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG"
            )

            call.enqueue(object : Callback<TokenResponse> {
                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.accessToken
                        // Обновление текста в текстовом поле
                        binding.textToken.text = token
                        Log.d("Token", "$token")
                    } else {
                        // Обработка ошибки
                        binding.textToken.text = "Ошибка получения токена"
                    }
                }

                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    // Обработка ошибки
                    binding.textToken.text = "Ошибка получения токена"
                }

            })

        }


    }
}

interface ApiService {
    @FormUrlEncoded
    @POST("oauth/token")
    fun getToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Call<TokenResponse>
}

data class TokenResponse(
    @SerializedName("access_token")
    val accessToken: String?,
    // Другие необходимые поля из ответа сервера
)