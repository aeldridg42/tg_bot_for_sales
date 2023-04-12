# Telegram bot with webapp

## About:
<img align=right width=150 src="https://github.com/aeldridg42/tg_bot_for_sales/blob/main/telegrambot/src/main/resources/demo.gif" />
Always dreamed of an online store? This telegram bot will make your dream come true 😁. 

<br/>Project uses `Spring Boot`,`Spring MVC` and `Spring Data`.
<br/>`Thymeleaf` is using as template engine.
<br/><br/><br/><br/><br/><br/><br/><br/><br/>
## Before compile:
There are several things to do before running this bot.

**Step 1:** you must register your telegram bot. There are a lot of guides in the internet, find any and do it!

**Step 2:** you should prepare 2 dedicated IP addresses: first is for telegram webhooks and second is for webapp.
<br/>I used [ngrok](https://ngrok.com/download) and [localtunnel](https://theboroer.github.io/localtunnel-www/) services for testing. 

**Step 3:** you must fill [application.properties](telegrambot/src/main/resources/application.properties) file.
| Key                               | Explanation                                  |
| --------------------------------- |:--------------------------------------------:|
|server.port                        |port number on which your application will run|
|telegrambot.botUsername            |telegram bot username, starts with '@'        |
|telegrambot.botToken               |token from [BotFather](https://t.me/botfather)|
|telegrambot.webHookPath            |first dedicated IP address                    |
|telegrambot.adminKey               |password for admins in your shop              |
|telegrambot.webappUrl              |second dedicated IP address                   |
|images.folder                      |absolute path to images storage               |
|spring.datasource.url              |url to your database                          |
|spring.datasource.username         |database username                             |
|spring.datasource.password         |database password                             |
|spring.datasource.driver-class-name|name of the JDBC driver                       |

**Step 4:** you should create 3 empty tables - product, users, image. Hibernate will create columns by itself.
<br/>Preparations of bot is ended at this step. 
## How to run:
