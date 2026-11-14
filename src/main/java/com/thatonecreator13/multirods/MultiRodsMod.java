// PURPOSE: main Multi Rods initialization, visibility networking, hook restoration, and temporary wake command

package com.thatonecreator13.multirods;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MultiRodsMod implements ModInitializer {


	public static final String MOD_ID = "multirods";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		/*
		 * Register the client -> server visibility packet.
		 */
		PayloadTypeRegistry.serverboundPlay().register(
				VisibilityPayload.TYPE,
				VisibilityPayload.CODEC
		);

		/*
		 * Receive visibility reports from clients.
		 */
		ServerPlayNetworking.registerGlobalReceiver(
				VisibilityPayload.TYPE,
				(payload, context) -> {

					ServerVisibleRodManager.updateVisible(
							context.player(),
							Set.copyOf(
									payload.rodIds()
							)
					);
				}
		);

		/*
		 * Restore saved fishing hooks whenever
		 * a player joins the world.
		 */
		ServerPlayerEvents.JOIN.register(
				HookRestoreManager::restorePlayer
		);

		/*
		 * TEMPORARY TEST COMMAND
		 *
		 * /multirods wake
		 *
		 * Wakes every live Multi-Rods bobber owned by
		 * the command-using player, regardless of which
		 * dimension the bobber is currently in.
		 */
		CommandRegistrationCallback.EVENT.register(
				(dispatcher, registryAccess, environment) ->
						multirods$registerCommands(dispatcher)
		);

		LOGGER.info(
				"Multi Rods: Hook restoration initialized."
		);

		LOGGER.info(
				"Multi Rods: Visibility networking initialized."
		);

		LOGGER.info(
				"Multi Rods: Temporary /multirods wake command initialized."
		);
	}

	private static void multirods$registerCommands(
			CommandDispatcher<CommandSourceStack> dispatcher
	) {

		dispatcher.register(
				Commands.literal("multirods")
						.then(
								Commands.literal("wake")
										.executes(context -> {

											ServerPlayer player =
													context.getSource()
															.getPlayerOrException();

											int woken = 0;

											/*
											 * MultiRodSavedData is server-wide,
											 * so this finds the player's rods even
											 * when their bobbers are in another
											 * dimension.
											 */
											MultiRodSavedData data =
													MultiRodSavedData.get(
															player.level()
													);

											for (
													Map.Entry<String, MultiRodSavedData.RodData> entry
													: data.getRods().entrySet()
											) {

												MultiRodSavedData.RodData rodData =
														entry.getValue();

												if (!rodData.owner().equals(
														player.getUUID()
												)) {
													continue;
												}

												UUID rodId;

												try {
													rodId =
															UUID.fromString(
																	entry.getKey()
															);
												} catch (
														IllegalArgumentException ignored
												) {
													continue;
												}

												FishingHook hook =
														MultiRodData.getHook(
																rodId
														);

												if (hook == null
														|| hook.isRemoved()) {
													continue;
												}

												if (!(hook.getOwner()
														instanceof ServerPlayer owner)) {
													continue;
												}

												if (!owner.getUUID().equals(
														player.getUUID()
												)) {
													continue;
												}

												FishingHookAccess access =
														(FishingHookAccess) hook;

												if (access.multirods$isLazy()) {

													access.multirods$setLazy(
															false
													);

													woken++;
												}
											}

											final int totalWoken =
													woken;

											context.getSource().sendSuccess(
													() -> Component.literal(
															"Woke "
																	+ totalWoken
																	+ " Multi-Rods bobber"
																	+ (
																	totalWoken == 1
																			? ""
																			: "s"
															)
													),
													false
											);

											return totalWoken;
										})
						)
		);
	}

	public static Identifier id(String path) {

		return Identifier.fromNamespaceAndPath(
				MOD_ID,
				path
		);
	}


}
