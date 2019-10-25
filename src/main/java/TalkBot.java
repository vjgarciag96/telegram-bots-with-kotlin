import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TalkBot {

    public static void main(String[] args) {
        TelegramBot telegramBot = new TelegramBot("976951878:AAETlyNbC0ocEilDj...");
        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                if (update.message() != null && update.message().text().equals("/sayHi")) {
                    sayHi(telegramBot, update.message());
                    return;
                }

                if (update.inlineQuery() != null) {
                    showGithubReposSearchResults(telegramBot, update.inlineQuery());
                    return;
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private static void sayHi(TelegramBot telegramBot, Message message) {
        long chatId = message.chat().id();
        telegramBot.execute(new SendMessage(chatId, "Hello Tuenti!!"));
    }

    private static void showGithubReposSearchResults(TelegramBot telegramBot, InlineQuery inlineQuery) {
        String queryText = inlineQuery.query();

        if (queryText.trim().isEmpty()) return;

        GithubApi.search(queryText, githubRepos -> {
            List<InlineQueryResultArticle> queryResults = githubRepos.stream()
                    .map(repo -> new InlineQueryResultArticle(UUID.randomUUID().toString(),
                            repo.getName(),
                            new InputTextMessageContent(repo.getUrl()))
                            .description(repo.getDescription())
                            .thumbUrl(repo.getOwner().getAvatarUrl()))
                    .collect(Collectors.toList());

            telegramBot.execute(new AnswerInlineQuery(inlineQuery.id(),
                    queryResults.toArray(new InlineQueryResult[queryResults.size()])));
        });
    }
}
