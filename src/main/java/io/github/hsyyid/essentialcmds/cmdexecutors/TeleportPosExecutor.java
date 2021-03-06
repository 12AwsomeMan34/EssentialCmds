/*
 * This file is part of EssentialCmds, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 - 2015 HassanS6000
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.hsyyid.essentialcmds.cmdexecutors;

import io.github.hsyyid.essentialcmds.internal.CommandExecutorBase;
import io.github.hsyyid.essentialcmds.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import javax.annotation.Nonnull;

public class TeleportPosExecutor extends CommandExecutorBase
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		Optional<Player> p = ctx.<Player> getOne("player");
		Optional<String> optionalWorld = ctx.<String> getOne("world");
		int x = ctx.<Integer> getOne("x").get();
		int y = ctx.<Integer> getOne("y").get();
		int z = ctx.<Integer> getOne("z").get();
		World world = Sponge.getServer().getWorld(optionalWorld.get()).orElse(null);

		if (p.isPresent())
		{
			if (src.hasPermission("teleport.pos.others"))
			{
				Utils.setLastTeleportOrDeathLocation(p.get().getUniqueId(), p.get().getLocation());

				if(world != null)
					p.get().setLocation(new Location<>(world, x, y, z));
				else
					p.get().setLocation(new Location<>(p.get().getWorld(), x, y, z));

				src.sendMessage(Text.of(TextColors.GREEN, "Success! ", TextColors.YELLOW, "Teleported player " + p.get().getName() + " to " + x + "," + y + "," + z + "."));
				p.get().sendMessage(Text.of(TextColors.GOLD, "You have been teleported by " + src.getName()));
			}
		}
		else
		{
			if (src instanceof Player)
			{
				Player player = (Player) src;
				Utils.setLastTeleportOrDeathLocation(player.getUniqueId(), player.getLocation());

				if(world != null)
					player.setLocation(new Location<>(world, x, y, z));
				else
					player.setLocation(new Location<>(player.getWorld(), x, y, z));

				src.sendMessage(Text.of(TextColors.GREEN, "Success! ", TextColors.YELLOW, "Teleported to coords."));
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot teleport, you are not a player!"));
			}
		}

		return CommandResult.success();
	}

	@Nonnull
	@Override
	public String[] getAliases() {
		return new String[] { "tppos", "teleportpos", "teleportposition" };
	}

	@Nonnull
	@Override
	public CommandSpec getSpec() {
		return CommandSpec
			.builder()
			.description(Text.of("Teleport Position Command"))
			.permission("essentialcmds.teleport.pos.use")
			.arguments(GenericArguments.seq(
				GenericArguments.onlyOne(GenericArguments.integer(Text.of("x"))),
				GenericArguments.onlyOne(GenericArguments.integer(Text.of("y"))),
				GenericArguments.onlyOne(GenericArguments.integer(Text.of("z"))),
				GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))),
				GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.string(Text.of("world")))))
			.executor(this).build();
	}
}
