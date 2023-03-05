package nl.blackstardlb.sso.service.models

data class SimpleUserData(
    override val jwtUsername: String,
    override val appUsername: String,
    override val password: String
) : UserData
