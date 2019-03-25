package com.etech.kidsstuffdemo.models

//  user/login
data class User(
    val id: String,
    val accessToken: String,
    val name: String,
    val mobile: String,
    val email: String,
    val isVarrifird: Boolean
)


//{
//    "flag": 1,
//    "message": "Login Successful",
//    "data": {
//    "userDetail": {
//        "_id": "5c98b2130e480d0bbd3cf41f",
//        "name": "Ronak Suchak",
//        "email": "ronaks.etechmavens@gmail.com",
//        "mobile": "0123456789",
//        "isVerified": true,
//        "loginType": "app",
//        "image": "",
//        "location": null,
//        "accessToken": "60d7e5434fc1ad92bae7098b69df5ff6"
//    }
//}
//}