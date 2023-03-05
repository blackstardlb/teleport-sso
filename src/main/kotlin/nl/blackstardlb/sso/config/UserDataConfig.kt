package nl.blackstardlb.sso.config

import nl.blackstardlb.sso.service.CSVResourceService
import nl.blackstardlb.sso.service.UserDataService
import nl.blackstardlb.sso.service.UserDataServiceImpl
import nl.blackstardlb.sso.service.models.SimpleUserData
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserDataConfig {
    @Bean
    @Qualifier("portainer")
    fun portainerUserDataService(csvResourceService: CSVResourceService): UserDataService<SimpleUserData> {
        return UserDataServiceImpl(SimpleUserData::class.java, csvResourceService.getCSV("portainer"))
    }
}
