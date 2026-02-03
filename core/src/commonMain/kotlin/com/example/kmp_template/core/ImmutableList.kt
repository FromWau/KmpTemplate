package com.example.kmp_template.core

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

operator fun <T> ImmutableList<T>.plus(other: ImmutableList<T>): ImmutableList<T> {
    return this.toPersistentList().addAll(other).toImmutableList()
}

operator fun <T> ImmutableList<T>.plus(other: Collection<T>): ImmutableList<T> {
    return this.toPersistentList().addAll(other).toImmutableList()
}

operator fun <T> ImmutableList<T>.plus(other: Array<out T>): ImmutableList<T> {
    return this + other.asList()
}

operator fun <T> ImmutableList<T>.plus(other: T): ImmutableList<T> {
    return this.toPersistentList().add(other).toImmutableList()
}

operator fun <T> ImmutableList<T>.minus(other: ImmutableList<T>): ImmutableList<T> {
    return this.toPersistentList().removeAll(other).toImmutableList()
}

operator fun <T> ImmutableList<T>.minus(other: Collection<T>): ImmutableList<T> {
    return this.toPersistentList().removeAll(other).toImmutableList()
}

operator fun <T> ImmutableList<T>.minus(other: Array<out T>): ImmutableList<T> {
    return this - other.asList().toSet()
}

operator fun <T> ImmutableList<T>.minus(other: T): ImmutableList<T> {
    return this.toPersistentList().remove(other).toImmutableList()
}

