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
    @Column(unique = true)
    var systemName:String,

    @NotNull
    @Column
    val isSystem :Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentSystemId" )
    var parentSystem: SystemEntity? = null,
)

fun SystemDto.asEntity() = SystemEntity(
    systemName = this.systemName,
    isSystem = this.isSystem
)
