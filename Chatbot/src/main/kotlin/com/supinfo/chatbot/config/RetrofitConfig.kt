package com.supinfo.chatbot.config

import com.supinfo.chatbot.data.api.VpicService
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory


@Configuration
class RetrofitConfig {

    companion object {
        const val VPIC_API_ENDPOINT = "https://vpic.nhtsa.dot.gov/"
    }

    private fun buildRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(defaultConverter())
                .addCallAdapterFactory(defaultCallAdapter())
                .client(client)
                .baseUrl(baseUrl)
                .build()
    }

    private fun defaultConverter(): retrofit2.Converter.Factory {
        return JacksonConverterFactory.create()
    }

    private fun defaultCallAdapter(): retrofit2.CallAdapter.Factory {
        return RxJava2CallAdapterFactory.create()
    }

    @Bean
    fun vpicService(): VpicService {
        val logger = LoggerFactory.getLogger(VpicService::class.java)
        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    // We need to add format = json at the end of the request
                    val url = chain.request().url().newBuilder().addQueryParameter("format", "json").build()
                    val request = chain.request().newBuilder().url(url).build()
                    logger.info("Sending request to url: {}", request.url())

                    // Log and send the request
                    val response = chain.proceed(request)
                    logger.info("Received response for call: {}", request.url())
                    response
                }
                .build()
        val retrofit = buildRetrofit(VPIC_API_ENDPOINT, okHttpClient)

        return retrofit.create(VpicService::class.java!!)
    }

}
