package com.money

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

data class HttpResponse(
    val status: String,
    val body: String,
    val contentType: String = "text/html",
)

fun main() {
    val start = System.currentTimeMillis()
    val server = ServerSocket(8080)
    val pool = Executors.newFixedThreadPool(8)

    val elapsed = System.currentTimeMillis() - start
    log("Server started on ${server.localSocketAddress} in ${elapsed}ms")

    Runtime.getRuntime().addShutdownHook(
        Thread {
            log("Shutting down server...")
            try {
                server.close()
                pool.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
    )

    while (true) {
        try {
            val client = server.accept()
            pool.submit {
                handleClient(client)
            }
        } catch (e: Exception) {
            log("Server stopped: ${e.message}")
            break
        }
    }
}

fun handleClient(client: Socket) {
    val reader = BufferedReader(InputStreamReader(client.getInputStream()))
    val writer = PrintWriter(client.getOutputStream(), true)

    try {
        val requestLine = reader.readLine()
        if (requestLine == null || requestLine.isBlank()) {
            client.close()
            return
        }

        val parts = requestLine.split(" ")
        if (parts.size < 2) {
            writer.println("HTTP/1.1 400 Bad Request")
            writer.println("Content-Length: 0")
            writer.println()
            return
        }

        val method = parts[0]
        val path = parts[1]
        log("Request: $method $path")

        while (true) {
            val headerLine = reader.readLine() ?: break
            if (headerLine.isEmpty()) break
        }

        val start = System.currentTimeMillis()
        val response = route(method, path)
        val duration = System.currentTimeMillis() - start

        writer.println("HTTP/1.1 ${response.status}")
        writer.println("Content-Type: ${response.contentType}; charset=UTF-8")
        writer.println("Content-Length: ${response.body.toByteArray().size}")
        writer.println()
        writer.print(response.body)
        writer.flush()

        log("Request handled in ${duration}ms by ${Thread.currentThread().name}")
    } catch (e: Exception) {
        log("Error: ${e.message}")
    } finally {
        client.close()
    }
}

fun route(
    method: String,
    path: String,
): HttpResponse =
    when {
        method == "GET" && path == "/" -> HttpResponse("200 OK", "<h1>Hello World</h1>")
        method == "GET" && path == "/health" -> HttpResponse("200 OK", "OK", "text/plain")
        method == "GET" && path == "/metrics" -> HttpResponse("200 OK", """{"requests":42}""", "application/json")
        else -> HttpResponse("404 Not Found", "<h1>404 Not Found</h1>")
    }

fun log(message: String) {
    println("[${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)}] $message")
}
