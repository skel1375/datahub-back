package com.knusolution.datahub.system.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "db_system")
data class SystemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val systemId:Long = 0,

    @NotNull
    @Column
    var systemName:String,
)

fun SystemDto.asEntity() = SystemEntity(systemName = this.systemName)
