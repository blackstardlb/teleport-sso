package nl.blackstardlb.sso.service

import java.io.File
import java.nio.file.Path

interface CSVResourceService {
    fun getCSV(csvName: String): File
}
