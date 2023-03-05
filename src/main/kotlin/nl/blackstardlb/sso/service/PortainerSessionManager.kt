package nl.blackstardlb.sso.service

import nl.blackstardlb.sso.service.models.SimpleUserData

interface PortainerSessionManager {
    fun fetchJWT(userData: SimpleUserData): String
    fun isJWTValid(jwt: String): Boolean
}
