package nl.blackstardlb.sso.service

import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.File

//@Profile("!prod")
//@Service
class DefaultCSVResourceServiceImpl : CSVResourceService {
    override fun getCSV(csvName: String): File {
        return ClassPathResource("/csv/$csvName.csv").file
    }
}
