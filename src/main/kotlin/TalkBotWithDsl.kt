import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle
import com.pengrad.telegrambot.model.request.InputTextMessageContent
import java.util.*

fun main() {
    bot("976951878:AAETlyNbC0ocEilDjq09h...") {
        command("sayHi") { bot, message ->
            bot.sayHi(message.chat().id())
        }
        inlineQuery { bot, inlineQuery ->
            bot.showGithubReposSearchResult(inlineQuery)
        }
    }.startPolling()
}

private fun Bot.sayHi(chatId: Long) {
    sendMessage(chatId, "Hello Tuenti!!")
}

private fun Bot.showGithubReposSearchResult(inlineQuery: InlineQuery) {
    val queryText = inlineQuery.query()

    if (queryText.isBlank() or queryText.isEmpty()) return

    GithubApi.search(queryText) { repos ->
        val reposInlineResult = repos.map(GithubRepo::toInlineResult)
        answerInlineQuery(inlineQuery.id(), reposInlineResult)
    }
}

private fun GithubRepo.toInlineResult(): InlineQueryResultArticle =
    InlineQueryResultArticle(
        UUID.randomUUID().toString(),
        name,
        InputTextMessageContent(url)
    ).description(description).thumbUrl(owner.avatarUrl)
