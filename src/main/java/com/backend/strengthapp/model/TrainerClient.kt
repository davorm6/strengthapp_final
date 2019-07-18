package com.backend.strengthapp.model

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "trainer_client")
data class TrainerClient (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        val trainer_id: Long = 0L,
        val client_id: Long = 0L,
        val request_sent_ts: Timestamp = Timestamp(System.currentTimeMillis()),
        val request_response_ts: Timestamp? = null,
        val end_time_ts: Timestamp? = null,
        val response: Int = 0,
        val sent_by: Int = 0,

        @OneToOne
        @JoinColumn(name = "trainer_id", referencedColumnName = "id", insertable = false, updatable = false)
        var trainer: Trainer = Trainer(),

        @OneToOne
        @JoinColumn(name = "client_id", referencedColumnName = "id", insertable = false, updatable = false)
        var client: User? = null
)

data class TrainerClientList (
        val trainer_clients: List<TrainerClient>
)