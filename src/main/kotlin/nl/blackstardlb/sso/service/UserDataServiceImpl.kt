package nl.blackstardlb.sso.service

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import mu.KLoggable
import mu.KLogger
import nl.blackstardlb.sso.exceptions.UserNotFoundException
import nl.blackstardlb.sso.service.models.UserData
import java.io.File


class UserDataServiceImpl<T : UserData>(
    private val clazz: Class<T>,
    private val csvFile: File
) : UserDataService<T>, KLoggable {
    override val logger: KLogger by lazy { logger() }
    private val bootstrapSchema = CsvSchema.emptySchema().withHeader()
    private val mapper = CsvMapper().apply { registerModule(KotlinModule.Builder().build()) }
    private val users = loadUserData().associateBy { it.jwtUsername }

    private fun loadUserData(): List<T> {
        if (!csvFile.exists()) logger.warn { "No user data file found for ${csvFile.absolutePath}" }
        val readValues: MappingIterator<T> = mapper.readerFor(clazz).with(bootstrapSchema).readValues(csvFile)
        return readValues.readAll()
    }

    override fun getUserDataByJwtUsername(jwtUserName: String): T {
        if (!users.containsKey(jwtUserName)) throw UserNotFoundException(jwtUserName)
        return users[jwtUserName]!!
    }
}
