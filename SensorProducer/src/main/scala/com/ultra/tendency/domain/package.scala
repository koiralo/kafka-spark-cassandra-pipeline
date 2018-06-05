package com.ultra.tendency

import java.sql.Timestamp
import java.util.UUID

package object domain {


  case class Location(
                       latitude: Double,
                       longitude: Double
                     )

  case class IotData(
                      deviceId: UUID,
                      temperature: Int,
                      location: Location,
                      time: Long
                    ) {
    def toJSON(): String = {
      "{\"data\": {" +
        "\"deviceId\": \"" + deviceId + "\"," +
        "\"temperature\": " + temperature + "," +
        "\"location\": {" +
        "\"latitude\": " + location.latitude + "," +
        "\"longitude\": " + location.longitude + "}," +
        "\"time\": " + time +
        "}}"
    }
  }


}
