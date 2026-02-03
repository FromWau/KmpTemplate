package com.example.kmp_template.shared_client.person.data.mapper

import com.example.kmp_template.shared_client.person.data.database.PersonEntity
import com.example.kmp_template.shared_client.person.domain.model.Person
import com.example.kmp_template.shared_rpc.person.model.PersonRpc

fun Person.toRpc(): PersonRpc = PersonRpc(id = this.id, name = this.name)

fun PersonRpc.toDomain(): Person = Person(id = this.id, name = this.name, local = false)

fun Person.toEntity(): PersonEntity = PersonEntity(id = this.id, name = this.name)

fun PersonEntity.toDomain(): Person = Person(id = this.id, name = this.name, local = true)