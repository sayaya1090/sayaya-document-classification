package net.sayaya.document.data

data class MessageModel(val type: MessageType, val data: Model) {
    enum class MessageType {
        CREATE, LEARNING, LEARNED, DELETE
    }
}