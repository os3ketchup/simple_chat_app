package uz.os3ketchup.mychatapp.moduls

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
     val condition = MutableLiveData("home")






    fun goSettings(){
        condition.value = "settings"
    }
    fun goHome(){
        condition.value = "home"
    }

    fun goChat(){
        condition.value = "chat"
    }

    fun goGroup(){
        condition.value = "group"
    }
}