package nl.blackstardlb.sso.service.models

interface UserData {
    val jwtUsername: String
    val appUsername: String
    val password: String
}

