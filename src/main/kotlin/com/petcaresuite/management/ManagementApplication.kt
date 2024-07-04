package com.petcaresuite.management

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MicroserviceManagementApplication

fun main(args: Array<String>) {
	runApplication<MicroserviceManagementApplication>(*args)
}
