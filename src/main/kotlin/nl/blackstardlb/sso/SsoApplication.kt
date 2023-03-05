package nl.blackstardlb.sso

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SsoApplication

fun main(args: Array<String>) {
    runApplication<SsoApplication>(*args)
}
