package com.maggie.rapidsync.model.network

import com.maggie.rapidsync.model.Constants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {
    @Headers("Authorization: Bearer " + Constants.CHAT_GPT_API_KEY)
    @POST("/v1/chat/completions")
    fun createCompletion(@Body request: ChatRequest): Call<ChatResponse>
}

data class ChatRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>
)

    data class Message(
        val role: String,
        val content: String
    )


data class ChatResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val system_fingerprint: String,
    val usage: Usage
)

data class Choice(
    val finish_reason: String,
    val index: Int,
    val logprobs: Any,
    val message: Message
)

data class Usage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int
)