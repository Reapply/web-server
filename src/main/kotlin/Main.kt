package com.money

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

fun main() {
    val start = System.currentTimeMillis()

    val server = ServerSocket(8080)

    val elapsed = System.currentTimeMillis() - start
    println("Server started on ${server.localSocketAddress} in ${elapsed}ms")

    while (true) {
        val client = server.accept()
        val reader = BufferedReader(InputStreamReader(client.getInputStream()))
        val writer = PrintWriter(client.getOutputStream(), true)

        val requestLine = reader.readLine()
        println("Request: $requestLine")

        while (true) {
            val headerLine = reader.readLine() ?: break
            if (headerLine.isEmpty()) break
        }

        val responseBody = route(method = requestLine.split(" ")[0], path = requestLine.split(" ")[1])

        writer.println("HTTP/1.1 200 OK")
        writer.println("Content-Type: text/html; charset=UTF-8")
        writer.println("Content-Length: ${responseBody.toByteArray().size}")
        writer.println()
        writer.println(responseBody)

        client.close()

        println("Handled by " + Thread.currentThread().name)
    }
}

fun route(
    method: String,
    path: String,
): String =
    when {
        method == "GET" && path == "/" -> "<h1>Hello World</h1>"
        method == "GET" && path == "/health" -> "OK"
        method == "GET" && path == "/metrics" -> "metrics"
        else -> "<h1>404 Not Found</h1>"
    }
