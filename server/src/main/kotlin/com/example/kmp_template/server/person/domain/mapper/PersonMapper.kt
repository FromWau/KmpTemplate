package com.example.kmp_template.server.person.domain.mapper

import com.example.kmp_template.server.person.domain.model.Person
import com.example.kmp_template.shared_rpc.person.model.PersonRpc

fun Person.toRpc(): PersonRpc = PersonRpc(id = this.id, name = this.name)

fun PersonRpc.toDomain(): Person = Person(id = this.id, name = this.name)