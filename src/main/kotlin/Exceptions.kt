import java.lang.RuntimeException

class Exceptions {
}

class ChatAleradyExists(idUser1: Int, idUser2: Int): RuntimeException("Ошибка, чат между пользователями c id $idUser1 и $idUser2 уже создан")

class ChatNotFound(idChat: Int): RuntimeException("Ошибка,чат с id $idChat отсуствует")

class NoMessageFound(idChat: Int): RuntimeException("Ошибка,сообщения в чате с id $idChat отсуствуют")

class NumberOfMessageOutOfLimit(quantity: Int, maxQuantity: Int): RuntimeException("Ошибка, такое количество ($quantity) сообщений в чате отсуствует. Максимальное количество $maxQuantity")

class IndexOfMessageOutOfLimit(idMessage: Int): RuntimeException("Ошибка, сообщения с индексом $idMessage в чате отсуствует")