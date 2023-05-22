package com.hasan.jetfasthub.data.model

open class AuthModel(
     private var clientId: String? = null,
     private var clientSecret: String? = null,
     private var redirectUri: String? = null,
     private var scopes: List<String>? = null,
     private var state: String? = null,
     private var note: String? = null,
     private var noteUrl: String? = null
)
