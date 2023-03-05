package nl.blackstardlb.sso.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import kotlin.io.path.Path

@Service
class CSVResourceServiceImpl(
    @Value("\${nl.blackstardlb.sso.app.csv.location}") private val csvPath: String
) : CSVResourceService {
    override fun getCSV(csvName: String): File {
        return Path(csvPath).resolve("$csvName.csv").toFile()
    }
}
