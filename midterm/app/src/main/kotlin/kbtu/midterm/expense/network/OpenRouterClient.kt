package kbtu.midterm.expense.network

import kbtu.midterm.expense.BuildConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class OpenRouterClient(
    private val httpClient: OkHttpClient = defaultClient(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    fun hasApiKey(): Boolean = BuildConfig.OPENROUTER_API_KEY.isNotBlank()

    suspend fun chatCompletion(request: ChatCompletionRequest): Result<String> = runCatching {
        val body = json.encodeToString(request)
            .toRequestBody("application/json".toMediaType())
        val httpRequest = Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENROUTER_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()
        httpClient.newCall(httpRequest).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("OpenRouter error: HTTP ${response.code}")
            }
            val payload = response.body?.string()
                ?: throw IllegalStateException("Empty response from OpenRouter")
            val parsed = json.decodeFromString<ChatCompletionResponse>(payload)
            parsed.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("No completion content")
        }
    }

    companion object {
        private fun defaultClient(): OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
    }
}
