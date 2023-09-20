data class Chat(
    val idUser1: Int,
    val idUser2: Int,
    val id_chat: Int = 0,
    val messages: MutableList<Message> = mutableListOf(),
    var isReadByUser1: Boolean = false,
    var isReadByUser2: Boolean = false
) {
    data class Message(
        val id: Int,
        val id_chat: Int,
        val idUserFrom: Int,
        val idUserTo: Int,
        var text: String,
        var isReadByUser1: Boolean = false,
        var isReadByUser2: Boolean = false
    )
}

class ChatService {
    private var id_chat: Int = 1
    private var id_message: Int = 1
    private var chats: MutableList<Chat> = mutableListOf()

    // создание чата
    fun createChat(id_user_from: Int, id_user_to: Int, textOfFirstMessage: String): Int {
        // проверка наличия чата между двумя пользователями. Если чат есть - ошибка о наличии чата
        if (chats.contains(chats.find {
                (it.idUser1 == id_user_to && it.idUser2 == id_user_from)
                        || (it.idUser1 == id_user_from && it.idUser2 == id_user_to)
            })) {
            throw ChatAleradyExists(id_user_to, id_user_from)
        }
        // создание первого сообщения с пометкой "прочитано" у отправляющего (начинающего чат)
        val message = Chat.Message(
            id_message,
            id_chat,
            id_user_from,
            id_user_to,
            textOfFirstMessage,
            isReadByUser1 = true
        )
        // новый id для последующих сообщений
        id_message += 1
        // добавление чата в общий список чатов с начальным сообщений и пометкой "прочитано" у отправляющего (начинающего чат)
        chats.add(Chat(id_user_from, id_user_to, id_chat, mutableListOf(message), isReadByUser1 = true))
        // новый id для последующих чатов
        id_chat += 1
        return 1
    }

    // получение количества непрочитанных чатов (id фигурирует в пользователях чата User1/2 и есть отсутствует пометка "прочитано")
    fun getUnreadChatsCount(idUser: Int): Int {
        return chats.count { it.idUser1 == idUser && it.isReadByUser1 == false || it.idUser2 == idUser && it.isReadByUser2 == false }
    }

    // получение списка чатов (id фигурирует в пользователях чата User1/2)
    fun getChats(idUser: Int): List<Chat> {
        return chats.filter { it.idUser1 == idUser || it.idUser2 == idUser }
    }

    // получение списка последних сообщений в чатах у пользователя
    fun getLastMessagesFromChats(idUser: Int): ArrayList<String> {
        // пустой список для необходимых сообщений (текста сообщений)
        val listOfLastMessages = ArrayList<String>()
        // если нет чатов у пользователя
        if (chats.find { it.idUser1 == idUser || it.idUser2 == idUser } == null) {
            listOfLastMessages.add("Нет сообщений")
        }
        // поиск в чатах пользователя среди User1/User2
        for (chat in chats) {
            if (chat.idUser1 == idUser || chat.idUser2 == idUser) {
                // добавление текста последнего сообщения в список
                listOfLastMessages.add(chat.messages[chat.messages.lastIndex].text)
            }
        }
        return listOfLastMessages
    }

    // получение списка сообщений из чата (по id пользователя, чата, id сообщения с которого показывать и количество сообщений для вывода
    fun getSomeMessagesFromChats(
        idUser: Int,
        idChat: Int,
        idLastMessage: Int,
        quantity: Int
    ): MutableList<Chat.Message> {
        // создание пустого списка для необходимых сообщений
        val messagesOfChat: MutableList<Chat.Message>

        // поиск чата по id и запоминание индекса в списке чатов
        val index = chats.indexOfFirst { it.id_chat == idChat }
        // если чата с id не нашлось
        if (index == -1) {
            throw ChatNotFound(idChat)
        } else {
            val allMessagesChats: MutableList<Chat.Message> = chats[index].messages

            val firstIndex = allMessagesChats.indexOfFirst { it.id == idLastMessage }
            // если сообщение с индексом idLastMessage сотсутствует
            if (firstIndex == -1) {
                throw IndexOfMessageOutOfLimit(idLastMessage)
            }
            // если диапазон указанных сообщений (количество) больше существующих сообщений
            if ((firstIndex + quantity) > (allMessagesChats.size)) {
                throw NumberOfMessageOutOfLimit(quantity, allMessagesChats.size - firstIndex)
            }
            // извлечение необходиммых сообщений
            messagesOfChat = allMessagesChats.subList(firstIndex, firstIndex + quantity)
            // присвоение пометки "прочитано" сообщениям в диапазоне
            for (message in chats[index].messages) {
                for (messageOfChat in messagesOfChat) {
                    if (message == messageOfChat && chats[index].idUser2 == idUser) {
                        message.isReadByUser2 = true
                        messageOfChat.isReadByUser2 = true
                    } else if (message == messageOfChat && chats[index].idUser1 == idUser) {
                        message.isReadByUser1 = true
                        messageOfChat.isReadByUser1 = true
                    }
                }
            }
            // присвоение пометки "прочитано", если после диапазона нет непрочитанных сообщений
            if (chats[index].messages.find { it.isReadByUser2 == false } == null) {
                chats[index].isReadByUser2 = true
            } else if (chats[index].messages.find { it.isReadByUser1 == false } == null) {
                chats[index].isReadByUser1 = true
            }
        }
        return messagesOfChat
    }

    // создание нового сообщения
    fun createMessage(idChat: Int, idUserFrom: Int, idUserTo: Int, text: String): Int {
        // нахождение индекса чата в списке чатов
        val index = chats.indexOfFirst { it.id_chat == idChat }
        if (index == -1) {
            throw ChatNotFound(idChat)
        }
        chats[index].messages.add(
            Chat.Message(
                id_message,
                idChat,
                idUserFrom,
                idUserTo,
                text,
                idUserFrom == chats[index].idUser1 ?: true,
                idUserFrom == chats[index].idUser2 ?: true
            )
        )
        id_message += 1
        // присваиваем пометку "прочитано" всем сообщениям отправителю (если он пишет, значит всё прочел)
        for (message in chats[index].messages) {
            if (idUserFrom == chats[index].idUser1) {
                message.isReadByUser1 = true
                chats[index].isReadByUser1 = true
                chats[index].isReadByUser2 = false
            } else if (idUserFrom == chats[index].idUser2) {
                message.isReadByUser2 = true
                chats[index].isReadByUser2 = true
                chats[index].isReadByUser1 = false
            }
        }
        return 1
    }

    // удаление чата
    fun deleteChat(idChat: Int): Int {
        chats.remove(chats.find { it.id_chat == idChat } ?: throw ChatNotFound(idChat))
        return 1
    }

    // удаление сообщения
    fun deleteMessage(idChat: Int, idMessage: Int): Int {
        val index = chats.indexOfFirst { it.id_chat == idChat }
        if (index == -1) {
            throw ChatNotFound(idChat)
        }
        chats[index].messages.removeIf { it.id == idMessage }
        // удаление чата при отсутствии сообщений
        if (chats[index].messages.isEmpty()) {
            deleteChat(idChat)
        }
        return 1
    }


    fun getAllChats(): MutableList<Chat> {
        return chats
    }
}

fun main(args: Array<String>) {
    val service = ChatService()
//
//    service.createChat(2, 1, "Hello")
//    service.createMessage(1, 2, 1, "How Are you211?")
//    service.createMessage(1, 2, 1, "How Are you212?")
//
//    service.createChat(3, 2, "Hello32")
//    service.createMessage(2, 3, 2, "How Are you231?")
//    service.createMessage(2, 3, 2, "How Are you232?")
//    service.createChat(4, 3, "Hello32")
//    service.createMessage(3, 4, 2, "How Are you42?")
//
//    println("количество непрочитанных чатов у юзера 1: " + service.getUnreadChatsCount(1))
//    println("количество непрочитанных чатов у юзера 2: " + service.getUnreadChatsCount(2))
//    println("количество непрочитанных чатов у юзера 3: " + service.getUnreadChatsCount(3))
//    println("количество непрочитанных чатов у юзера 4: " + service.getUnreadChatsCount(4))
//    //println(service.getChats(3))
//    service.deleteChat(1)
//    println(service.getAllChats())
//    service.deleteMessage(3, 7)
//    println(service.getAllChats())
//    service.deleteMessage(3, 8)
//    println(service.getAllChats())
//    println("последние сообщения юзера 5: " + service.getLastMessagesFromChats(5))
//    println("последние сообщения юзера 2: " + service.getLastMessagesFromChats(2))
    //println("диапазон сообщений чата 1: " + service.getSomeMessagesFromChats(2, 3, 7, 1))
//    println("Сообщения чата 1: " + service.getAllMessages(1))
//    println("чаты юзера 1: " + service.getChats(2))
}