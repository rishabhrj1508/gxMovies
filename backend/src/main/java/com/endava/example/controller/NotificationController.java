package com.endava.example.controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * NotificationController provides real-time communication with clients using
 * Server-Sent Events(SSE). This allows the server to push updates (such as
 * notifications) to clients over a single HTTP connection. The class manages
 * the list of connected clients and allows notifications to be sent to all
 * connected clients.
 */
@RestController
public class NotificationController {

	// list to keep track of connected clients using SSE.
	// The CopyOnWriteArrayList is thread-safe.
	private final List<SseEmitter> clients = new CopyOnWriteArrayList<>();

	/**
	 * EndPoint that clients use to subscribe to notifications. The client will
	 * receive events in the form of SSE from the server.
	 *
	 * @return An SseEmitter object that represents the event stream for the client.
	 */
	@GetMapping(value = "/notifications", produces = "text/event-stream")
	public SseEmitter subscribeToNotifications() {
		// Create a new emitter with a long timeout (5 hours)
		SseEmitter emitter = new SseEmitter(5 * 60 * 60 * 1000L); // 5 hours in milliseconds
		// Add the emitter to the list of connected clients
		clients.add(emitter);

		// Clean up when the emitter completes the connection
		emitter.onCompletion(() -> clients.remove(emitter)); // Remove the client from the list when the connection is
																// completed.

		// Clean up when the emitter times out (due to inactivity)
		emitter.onTimeout(() -> {
			clients.remove(emitter); // Remove client if timeout occurs
			emitter.complete(); // Complete the emitter to indicate end of stream
		});

		// Return the emitter to start the SSE stream for the client
		return emitter;
	}

	/**
	 * Method to send a notification message to all connected clients. This method
	 * could be called when a new notification needs to be broadcasted.
	 *
	 * @param message The notification message to send to all clients.
	 */
	public void sendNotificationToAllClients(String message) {
		// Loop through all connected clients and send the message
		for (SseEmitter client : clients) {
			try {
				// Sending the message as an SSE event to each client
				client.send(SseEmitter.event().data(message));
			} catch (Exception e) {
				// If an error occurs while sending to a client, remove that client from the
				// list.
				clients.remove(client);
			}
		}
	}
}
