package nl.blackstardlb.sso.service.models

data class TOTPUserData(
    override val jwtUsername: String,
    override val appUsername: String,
    override val password: String,
    val totpSecret: String
) : UserData
