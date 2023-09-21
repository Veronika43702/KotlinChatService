import java.lang.RuntimeException


class ChatAleradyExists(idUser1: Int, idUser2: Int): RuntimeException("Ошибка, чат между пользователями c id $idUser1 и $idUser2 уже создан")

class ChatNotFound(idChat: Int): RuntimeException("Ошибка,чат с id $idChat отсуствует")

class ChatNotFoundForUser(idChat: Int, idUser: Int): RuntimeException("Ошибка,чат с id $idChat для пользователя $idUser отсуствует")