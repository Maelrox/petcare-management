package com.petcaresuite.management

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.boot.runApplication

@EnableDiscoveryClient
@SpringBootApplication
class MicroserviceManagementApplication

fun main(args: Array<String>) {
	runApplication<MicroserviceManagementApplication>(*args)
}
