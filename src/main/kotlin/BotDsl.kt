import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResult
import com.pengrad.telegrambot.request.AnswerInlineQuery
import com.pengrad.telegrambot.request.SendMessage
import okhttp3.internal.toImmutableList

interface UpdateHandler {
    fun checkUpdate(update: Update): Boolean
    fun handle(bot: Bot, update: Update)
}

class InlineQueryHandler(
    private val handleInlineQuery: (Bot, InlineQuery) -> Unit
) : UpdateHandler {

    override fun checkUpdate(update: Update): Boolean = update.inlineQuery() != null

    override fun handle(bot: Bot, update: Update) {
        val inlineQuery = update.inlineQuery()
        checkNotNull(inlineQuery)
        handleInlineQuery(bot, inlineQuery)
    }
}

class CommandHandler(
    private val commandName: String,
    private val handleCommand: (Bot, Message) -> Unit
) : UpdateHandler {

    override fun checkUpdate(update: Update): Boolean =
        update.message() != null && update.message().text().startsWith("/$commandName")

    override fun handle(bot: Bot, update: Update) {
        val command = update.message()
        checkNotNull(command)
        handleCommand(bot, command)
    }
}

class BotBuilder {

    private var handlers = mutableListOf<UpdateHandler>()

    fun inlineQuery(handler: (Bot, InlineQuery) -> Unit): BotBuilder = apply {
        handlers.add(InlineQueryHandler(handler))
    }

    fun command(commandName: String, handler: (Bot, Message) -> Unit): BotBuilder = apply {
        handlers.add(CommandHandler(commandName, handler))
    }

    fun build(token: String, body: BotBuilder.() -> Unit): Bot {
        body()
        return Bot(token, handlers.toImmutableList())
    }
}

class Bot(token: String, private val handlers: List<UpdateHandler>) {

    private val telegramBot = TelegramBot(token)

    fun answerInlineQuery(inlineQueryId: String, inlineQueryResults: List<InlineQueryResult<*>>) {
        telegramBot.execute(AnswerInlineQuery(inlineQueryId, *inlineQueryResults.toTypedArray()))
    }

    fun sendMessage(chatId: Long, text: String) {
        telegramBot.execute(SendMessage(chatId, text))
    }

    fun startPolling() {
        telegramBot.setUpdatesListener { updates ->
            updates.forEach { update ->
                handlers
                    .filter { handler -> handler.checkUpdate(update) }
                    .forEach { handler -> handler.handle(this, update) }
            }
            return@setUpdatesListener UpdatesListener.CONFIRMED_UPDATES_ALL
        }
    }
}

fun bot(botToken: String, body: BotBuilder.() -> Unit): Bot = BotBuilder().build(botToken, body)





