package com.example.pal_grad.api

data class StarGANResult(
    var img: String = ""
)

data class StarGANPost(
    var style: String = "스타일 선택",
    var file: String = ""
)