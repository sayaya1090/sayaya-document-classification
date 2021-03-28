package net.sayaya.document.data

data class MessageSample(val type: MessageType, val data: Sample) {
    enum class MessageType {
        CREATE, PROCESSING, ANALYZED, DELETE
    }
}