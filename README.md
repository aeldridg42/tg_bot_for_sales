# Telegram bot with webapp

## About:
Always dreamed of an online store? This telegram bot will make your dream come true 😁. 

gif

Project uses `Spring Boot`,`Spring MVC` and `Spring Data`. A database management system is `PostgreSQL`.
<br/>`Thymeleaf` is using as template engine.

## Before compile:
There are several things to do before running this bot.

Firstly, you must register your telegram bot. There are a lot of guides in the internet, find any and do it!

Secondly, you should prepare 2 dedicated IP addresses: first is for telegram webhooks and second is for webapp.
<br/>I used [ngrok](https://ngrok.com/download) and [localtunnel](https://theboroer.github.io/localtunnel-www/) services for testing. 

Thirdly, you must fill [application.properties](telegrambot/src/main/resources/application.properties) file. I'll explain every key except database connection.
| Key                   | Explanation                                  |
| ----------------------|:--------------------------------------------:|
|server.port            |port number on which your application will run|
|telegrambot.botUsername|telegram bot username, starts with @          |
|telegrambot.botToken   |token from [BotFather](https://t.me/botfather)|
|telegrambot.webHookPath|first dedicated IP address                    |
|telegrambot.adminKey   |password for admins in your shop              |
|telegrambot.webappUrl  |second dedicated IP address                   |
|images.folder          |absolute path to images storage               |

## How to run:
