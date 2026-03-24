package com.cronos.app.data.repository

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

private const val API_KEY = "sk-jrq7lVLEEGZtLQ8OUjKde3bwSdnxWMxK6r7DAMD6kKLdaRPfct0CgW5xiR7v"
private const val BASE_URL = "https://proxy.gen-api.ru/v1/chat/completions"
private const val MODEL = "qwen-3-5"
private const val TAG = "AiRepository"

@Serializable
data class ChatMessage(val role: String, val content: String)

@Serializable
private data class ChatRequest(
    val model: String = MODEL,
    val messages: List<ChatMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 1500,
    val temperature: Double = 0.7
)

private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

@Singleton
class AiRepository @Inject constructor() {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun ask(systemPrompt: String, userPrompt: String): Result<String> = runCatching {
        val requestBody = json.encodeToString(
            ChatRequest.serializer(),
            ChatRequest(
                messages = listOf(
                    ChatMessage("system", systemPrompt),
                    ChatMessage("user", userPrompt)
                )
            )
        )
        val response: HttpResponse = client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $API_KEY")
            setBody(requestBody)
        }

        val rawBody = response.bodyAsText()
        Log.d(TAG, "Status: ${response.status}, Raw: $rawBody")

        extractContent(rawBody) ?: error("Не удалось извлечь ответ AI. Тело: ${rawBody.take(500)}")
    }

    private fun extractContent(raw: String): String? {
        return try {
            val obj = json.parseToJsonElement(raw).jsonObject

            // OpenAI format: choices[0].message.content (строка)
            val choiceContent = obj["choices"]?.jsonArray
                ?.firstOrNull()?.jsonObject
                ?.get("message")?.jsonObject
                ?.get("content")

            if (choiceContent != null) {
                // content может быть строкой
                try {
                    val s = choiceContent.jsonPrimitive.content
                    if (s.isNotBlank() && s != "null") return s
                } catch (_: Exception) {}

                // content может быть массивом [{type:"text", text:"..."}]
                try {
                    val arr = choiceContent.jsonArray
                    val text = arr.firstOrNull()?.jsonObject?.get("text")?.jsonPrimitive?.content
                    if (!text.isNullOrBlank() && text != "null") return text
                } catch (_: Exception) {}
            }

            // Qwen thinking mode: reasoning_content
            val message = obj["choices"]?.jsonArray?.firstOrNull()?.jsonObject?.get("message")?.jsonObject
            message?.get("reasoning_content")?.jsonPrimitive?.content
                ?.takeIf { it.isNotBlank() && it != "null" }
                // gen-api native: { "output": "..." }
                ?: obj["output"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
                ?: obj["response"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message}, raw: ${raw.take(300)}")
            null
        }
    }
}
