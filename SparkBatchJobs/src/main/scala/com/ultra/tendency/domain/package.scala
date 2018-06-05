package com.ultra.tendency

import java.sql.Timestamp

package object domain {

  case class SensorData(

                         deviceId: String,
                         temperature: Int,
                         latitude: Double,
                         longitude: Double,
                         time: Timestamp
                       )

}
