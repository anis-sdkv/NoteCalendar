package com.example.presentation.tools

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

fun <T> buildPersistenceList(builderAction: MutableList<T>.() -> Unit): PersistentList<T> {
    return buildList { builderAction() }.toPersistentList()
}
