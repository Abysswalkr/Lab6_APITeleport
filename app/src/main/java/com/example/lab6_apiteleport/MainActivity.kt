package com.example.lab6_apiteleport

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import khttp.get

data class City(val name: String, val slug: String)

fun main() {
    val cityListUrl = "http://api.teleport.org/api/urban_areas/"

    try {
        // Realizar una solicitud para obtener la lista de ciudades
        val response = get(cityListUrl)

        if (response.statusCode == 200) {
            val cities = jacksonObjectMapper().readValue<List<City>>(response.text)

            println("Lista de Ciudades:")
            cities.forEachIndexed { index, city ->
                println("${index + 1}. ${city.name}")
            }

            // Pedir al usuario que seleccione una ciudad
            println("Seleccione una ciudad (Ingrese el número): ")
            val selectedCityIndex = readLine()?.toIntOrNull()

            if (selectedCityIndex != null && selectedCityIndex > 0 && selectedCityIndex <= cities.size) {
                val selectedCity = cities[selectedCityIndex - 1]
                val cityInfoUrl = "http://api.teleport.org/api/urban_areas/slug:${selectedCity.slug}/"

                // Realizar una solicitud para obtener información detallada de la ciudad
                val cityInfoResponse = get(cityInfoUrl)

                if (cityInfoResponse.statusCode == 200) {
                    val cityInfo = jacksonObjectMapper().readValue<Map<String, Any>>(cityInfoResponse.text)
                    val imageUrl = cityInfo["city_images"] as? String

                    if (imageUrl != null) {
                        println("Imagen de la ciudad de ${selectedCity.name}: $imageUrl")
                    } else {
                        println("No se encontró imagen para la ciudad de ${selectedCity.name}")
                    }
                } else {
                    println("No se pudo obtener información detallada de la ciudad.")
                }
            } else {
                println("Selección de ciudad no válida.")
            }
        } else {
            println("No se pudo obtener la lista de ciudades.")
        }
    } catch (e: Exception) {
        println("Ocurrió un error: ${e.message}")
    }
}