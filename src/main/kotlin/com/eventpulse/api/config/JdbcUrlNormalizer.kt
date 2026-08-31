package com.eventpulse.api.config

/**
 * Railway (and some PaaS plugins) expose MYSQL_URL as `mysql://user:pass@host:port/db`.
 * The MySQL Connector/J driver only accepts `jdbc:mysql://…`.
 */
object JdbcUrlNormalizer {

    fun normalize(url: String?): String? {
        val trimmed = url?.trim().orEmpty()
        if (trimmed.isEmpty()) {
            return url
        }

        var jdbc = when {
            trimmed.startsWith("jdbc:") -> trimmed
            trimmed.startsWith("mysql://") -> "jdbc:$trimmed"
            trimmed.startsWith("mariadb://") ->
                "jdbc:mysql://${trimmed.removePrefix("mariadb://")}"
            else -> trimmed
        }

        if (jdbc.startsWith("jdbc:mysql://") &&
            !jdbc.contains("allowPublicKeyRetrieval", ignoreCase = true)
        ) {
            jdbc += if ("?" in jdbc) "&allowPublicKeyRetrieval=true"
            else "?allowPublicKeyRetrieval=true"
        }

        return jdbc
    }
}
