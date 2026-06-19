package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.config.ConfigLoader;
import kr.pyke.deceased_croa.handler.DonationEventHandler;
import kr.pyke.deceased_croa.manager.HordeManager;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_PlaySoundPacket;
import kr.pyke.deceased_croa.registry.sound.ModSounds;
import kr.pyke.deceased_croa.type.HORDE_TYPE;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.storage.ServerLevelData;

import java.util.Collection;
import java.util.Set;

public class HordeCommand {
    private static final SuggestionProvider<CommandSourceStack> HORDE_TYPE_SUGGEST = ((context, builder) -> SharedSuggestionProvider.suggest(Set.of("normal", "special", "server"), builder));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("호드")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("리로드").executes(HordeCommand::reloadConfig))
            .then(Commands.literal("시작")
                .then(Commands.argument("type", StringArgumentType.string()).suggests(HORDE_TYPE_SUGGEST)
                    .then(Commands.argument("targets", EntityArgument.players())
                        .executes(HordeCommand::startHorde)
                    )
                )
            )
        );
    }

    private static int startHorde(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        String type = StringArgumentType.getString(context, "type");
        HORDE_TYPE hordeType = HORDE_TYPE.fromID(type);

        for (ServerPlayer target : targets) {
            HordeManager.startHorde(target.serverLevel(), target, hordeType);
            if (hordeType == HORDE_TYPE.NORMAL) {
                DonationEventHandler.sendTitle(target, "§c[!] 경고 [!]", "일반 호드 이벤트 발생!", 20, 60, 20);
                S2C_PlaySoundPacket.send(target, ModSounds.NORMAL_HORDES, 1.f, 1.f);
            }
            else if (hordeType == HORDE_TYPE.SPECIAL) {
                DonationEventHandler.sendTitle(target, "§c[!] 경고 [!]", "특수 호드 이벤트 발생!", 20, 60, 20);
                S2C_PlaySoundPacket.send(target, SoundEvents.WITHER_DEATH, 1.f, 1.f);
            }
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 5, 0, false, true, true));
        }
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), String.format("§e%s§r명에게 호드 이벤트를 발생시켰습니다.", targets.size()));

        return 1;
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        ConfigLoader.load();
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), "CONFIG 로드가 완료되었습니다.");

        return 1;
    }
}
