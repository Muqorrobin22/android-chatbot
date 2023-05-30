package com.codepalace.chatbot.controller

import com.codepalace.chatbot.model.DiseaseSymptomsModelItem
import retrofit2.Call
import retrofit2.http.GET

interface DiseaseService {
    @GET("disease/batuk")
    fun getDiseases(): Call<List<DiseaseSymptomsModelItem>>
}