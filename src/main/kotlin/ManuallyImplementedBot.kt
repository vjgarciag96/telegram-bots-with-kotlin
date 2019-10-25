import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException

data class BotApiSuccessResponse<T>(
    val ok: Boolean,
    val result: T
)

typealias UpdatesDto = List<UpdateDto>

data class UpdateDto(
    @Json(name = "update_id") val updateId: Int,
    val message: MessageDto
)

data class MessageDto(
    val chat: ChatDto,
    val text: String?
)

data class ChatDto(
    val id: String
)

object BotApiClient {

    private const val BOT_TOKEN = "976951878:AAETlyNb..."
    private const val BOT_API_ENDPOINT = "https://api.telegram.org/bot$BOT_TOKEN"

    private val client by lazy { OkHttpClient() }
    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    fun getUpdates(offset: Int? = null, callback: (List<UpdateDto>) -> Unit) {
        val getUpdatesUrl = if (offset == null) {
            "${BOT_API_ENDPOINT}/getUpdates"
        } else {
            "${BOT_API_ENDPOINT}/getUpdates?offset=$offset"
        }

        val getUpdatesRequest = Request.Builder().url(getUpdatesUrl).build()

        client.newCall(getUpdatesRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson: String = response.body?.string() ?: return

                val updatesType = Types.newParameterizedType(List::class.java, UpdateDto::class.java)
                val getUpdatesResponseType = Types.newParameterizedType(BotApiSuccessResponse::class.java, updatesType)
                val getUpdatesResponseAdapter = moshi.adapter<BotApiSuccessResponse<UpdatesDto>>(getUpdatesResponseType)

                val getUpdatesResponse = getUpdatesResponseAdapter.fromJson(responseJson) ?: return

                if (getUpdatesResponse.ok) {
                    callback.invoke(getUpdatesResponse.result)
                }
            }
        })
    }

    fun sendTextMessage(chatId: String, text: String) {
        val sendTextMessageBody = """
            {
                "chat_id":"$chatId",
                "text":"$text"
            }    
        """.trimIndent().toRequestBody("application/json; charset=utf-8".toMediaType())

        val sendTextMessageRequest = Request.Builder()
            .url("${BOT_API_ENDPOINT}/sendMessage")
            .post(sendTextMessageBody)
            .build()

        client.newCall(sendTextMessageRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {}
            override fun onFailure(call: Call, e: IOException) {}
        })
    }
}

fun main() {
    fun onGetUpdatesSuccess(updates: List<UpdateDto>) {
        updates.forEach { update ->
            if (update.message.text?.contains("/start") == true) {
                BotApiClient.sendTextMessage(update.message.chat.id, "Hello world!!")
            } else {
                BotApiClient.getUpdates(offset = update.updateId.inc()) { onGetUpdatesSuccess(it) }
            }
        }
    }

    BotApiClient.getUpdates { onGetUpdatesSuccess(it) }
}