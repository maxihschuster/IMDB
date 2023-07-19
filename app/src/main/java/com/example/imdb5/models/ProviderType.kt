package com.example.imdb5.models

enum class ProviderType (val text : String) { //en vez de x ejemplo  BASIC.name pongo BASIC.text me da el texto
        BASIC("login basico"),
        GOOGLE("login con google"),
        FACEBOOK("login con facebook");
}