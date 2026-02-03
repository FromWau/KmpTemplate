package com.example.kmp_template.server.person.data.database.mapper

import com.example.kmp_template.server.person.data.database.model.PersonEntity
import com.example.kmp_template.server.person.domain.model.Person

fun Person.toEntity(): PersonEntity = PersonEntity(id = this.id, name = this.name)

fun PersonEntity.toDomain(): Person = Person(id = this.id, name = this.name)