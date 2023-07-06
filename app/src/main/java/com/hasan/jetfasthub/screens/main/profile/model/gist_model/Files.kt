package com.hasan.jetfasthub.screens.main.profile.model.gist_model

import com.google.gson.annotations.SerializedName

data class Files(
    @SerializedName("hello_world.rb")
    val hello_world_rb: HelloWorldRb
)