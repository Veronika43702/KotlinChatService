import org.junit.Test

import org.junit.Assert.*
import kotlin.math.exp

class ChatServiceTest {

    @Test
    fun createChatSuccess() {
        val service = ChatService()
        assertEquals(1,service.createChat(1, 2, "Hello"))
    }

    @Test(expected = ChatAleradyExists::class)
    fun createChatFailChatAlreadyExists() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        service.createChat(1, 2, "Hi")
    }

    @Test
    fun getUnreadChatsCountSuccess() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        service.createChat(3, 1, "Hello")
        assertEquals(1, service.getUnreadChatsCount(1))
    }

    @Test
    fun getChatsSuccess() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        service.createChat(3, 1, "Hello")
        assertEquals("["+service.getAllChats()[1]+"]", service.getChats(3).toString())
    }

    @Test
    fun getLastMessagesFromChatsSuccess() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        service.createMessage(1,1,2, "Hi")
        service.createChat(2, 3, "hi")
        assertEquals("[Hi, hi]", service.getLastMessagesFromChats(2).toString())
    }

    @Test()
    fun getLastMessagesFromChatsNoMessage() {
        val service = ChatService()
        assertEquals("[Нет сообщений]", service.getLastMessagesFromChats(1).toString())
    }

    @Test
    fun getSomeMessagesFromChatsSuccess() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        service.createMessage(1,1,2, "Hi")
        assertEquals("["+service.getAllChats()[0].messages[1]+"]", service.getSomeMessagesFromChats(1,1,2,1).toString())
    }

    @Test(expected = ChatNotFound::class)
    fun getSomeMessagesFromChatsFailNoChat() {
        val service = ChatService()
        service.getSomeMessagesFromChats(1,1,2,1)
    }

    @Test(expected = IndexOfMessageOutOfLimit::class)
    fun getSomeMessagesFromChatsFailLastMessageOut() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        service.getSomeMessagesFromChats(1,1,2,1)
    }

    @Test(expected = NumberOfMessageOutOfLimit::class)
    fun getSomeMessagesFromChatsFailQuantityOut() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        service.getSomeMessagesFromChats(1,1,1,3)
    }

    @Test
    fun createMessageSuccess() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        assertEquals(1, service.createMessage(1,1,2, "Hi"))
    }

    @Test(expected = ChatNotFound::class)
    fun createMessageFailNoChat() {
        val service = ChatService()
        assertEquals(1, service.createMessage(1,1,2, "Hi"))
    }

    @Test
    fun deleteChatSuccess() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        assertEquals(1, service.deleteChat(1))
    }

    @Test(expected = ChatNotFound::class)
    fun deleteChatFailNoChat() {
        val service = ChatService()
        service.deleteChat(1)
    }

    @Test
    fun deleteMessageSuccess() {
        val service = ChatService()
        service.createChat(1, 2, "Hello")
        service.createMessage(1,1,2, "Hi")
        assertEquals(1, service.deleteMessage(1,1))
    }

    @Test(expected = ChatNotFound::class)
    fun deleteMessageFailNoChat() {
        val service = ChatService()
        service.deleteMessage(1,1)
    }
}