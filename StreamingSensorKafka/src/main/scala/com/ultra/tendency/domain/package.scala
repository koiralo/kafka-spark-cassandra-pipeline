package com.ultra.tendency


package object domain {

  case class Location(
                       latitude: Double,
                       longitude: Double
                     )

  case class SensorDataRaw(
                      data: Data
                    )

  case class SensorDataFlat (

                              deviceId: String,
                              temperature: Long,
                              latitude: Double,
                              longitude: Double,
                              time: Long
                            )


  case class Data (
                    deviceId: String,
                    temperature: Long,
                    location: Location,
                    time: Long
                  )

}
