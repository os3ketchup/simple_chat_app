package uz.os3ketchup.mychatapp.moduls

data class ChatMessage(
    var text:String = "",var fromUID:String = "",var toUID:String = "",var timeSent:String = "",var pushKey:String = ""
)