// PURPOSE: client-side fishing bobber visibility reporting

package com.thatonecreator13.multirods.client;

import com.thatonecreator13.multirods.VisibilityPayload;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class MultiRodsClient implements ClientModInitializer {

	private static int tickCounter = 0;

	@Override
	public void onInitializeClient() {

		ClientTickEvents.START_CLIENT_TICK.register(
				client -> {

					/*
					 * Clear the render visibility list
					 * before the next frame.
					 */
					ClientVisibleRodManager.beginFrame();
				}
		);

		ClientTickEvents.END_CLIENT_TICK.register(
				client -> {

					if (client.player == null
							|| client.level == null) {

						return;
					}

					tickCounter++;

					/*
					 * Send visibility reports every
					 * 5 client ticks.
					 */
					if (tickCounter < 5) {
						return;
					}

					tickCounter = 0;

					ClientPlayNetworking.send(
							new VisibilityPayload(
									ClientVisibleRodManager
											.getVisibleRods()
											.stream()
											.toList()
							)
					);
				}
		);
	}
}