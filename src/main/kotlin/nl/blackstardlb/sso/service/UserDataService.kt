package nl.blackstardlb.sso.service

import nl.blackstardlb.sso.service.models.UserData

interface UserDataService<T : UserData> {
    fun getUserDataByJwtUsername(jwtUserName: String): T
}
