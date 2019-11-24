# telegram-bots-with-kotlin

<p align="center">
<img src="https://github.com/vjgarciag96/telegram-bots-with-kotlin/blob/master/doc/telegram-bot-father-kotlin.png" width="539" height="284"/>
</p>


In this repo you'll see the the source code for the talk **Telegram bots with Kotlin**

You can find the slides of the talk on [SpeakerDeck](https://speakerdeck.com/vjgarcia/telegram-bots-with-kotlin)

The repo contains two main things:

* A simple Telegram Bot implemented from zero without using libraries for the communication with Telegram Bot API. You can find it on [ManuallyImplementedBot.kt](https://github.com/vjgarciag96/telegram-bots-with-kotlin/blob/master/src/main/kotlin/ManuallyImplementedBot.kt) file.
* The implemention of a simple bot to showcase how we can build Telegram bots with Java/Kotlin and how we can use Kotlin DSLs to make bots code readable, concise and clear. The bot consists of:

  * Saying something when **/padrino** command is receiving
  
<p align="center">
<img src="https://github.com/vjgarciag96/telegram-bots-with-kotlin/blob/master/doc/padrino-command.png"/>
</p>

  * Answering to user's inline queries with Github repos search results
  
<p align="center">
<img src="https://github.com/vjgarciag96/telegram-bots-with-kotlin/blob/master/doc/inline-query.png"/>
</p>

There are different implementations for the last one:
 
* Java implementation using https://github.com/pengrad/java-telegram-bot-api library. You can find it on [TalkBot](https://github.com/vjgarciag96/telegram-bots-with-kotlin/blob/master/src/main/java/TalkBot.java) class
* Kotlin implementation with the same java library used by the previous one, but using [this Kotlin DSL](https://github.com/vjgarciag96/telegram-bots-with-kotlin/blob/master/src/main/kotlin/BotDsl.kt). You can check the bot code in [TalkBotWithDsl.kt](https://github.com/vjgarciag96/telegram-bots-with-kotlin/blob/master/src/main/kotlin/TalkBotWithDsl.kt) file
