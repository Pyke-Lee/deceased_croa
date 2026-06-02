package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.deceased_croa.manager.RandomBoxManager;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SyncRandomBoxPacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class RandomBoxCommand {
    private RandomBoxCommand() { }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("randombox")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("reload").executes(RandomBoxCommand::reload))
            .then(Commands.literal("create")
                .then(Commands.argument("box_id", StringArgumentType.word())
                    .then(Commands.argument("display_name", StringArgumentType.greedyString())
                        .executes(RandomBoxCommand::create)
                    )
                )
            )
        );
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        int count = RandomBoxManager.reload();

        syncAll(context.getSource().getServer());

        context.getSource().sendSuccess(() -> Component.literal("§6[SYSTEM]§r 랜덤 상자 " + count + "개를 불러왔습니다."), true);

        return 1;
    }

    private static int create(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String boxID = StringArgumentType.getString(context, "box_id");
        String displayName = StringArgumentType.getString(context, "display_name");

        List<ItemStack> contents = lookingAtContainerContents(player);
        if (contents == null) {
            context.getSource().sendFailure(Component.literal("§c바라보고 있는 상자가 없습니다."));
            return 0;
        }

        RandomBoxManager.CreateResult result = RandomBoxManager.createFromContainer(boxID, displayName, contents);

        switch (result) {
            case ALREADY_EXISTS: {
                context.getSource().sendFailure(Component.literal("§c이미 존재하는 id 입니다: " + boxID));
                return 0;
            }
            case EMPTY: {
                context.getSource().sendFailure(Component.literal("§c상자가 비어 있습니다."));
                return 0;
            }
            case ERROR: {
                context.getSource().sendFailure(Component.literal("§c파일을 생성하지 못했습니다."));
                return 0;
            }
            default: {
                break;
            }
        }

        int count = RandomBoxManager.reload();
        syncAll(context.getSource().getServer());

        context.getSource().sendSuccess(() -> Component.literal("§6[SYSTEM]§r 랜덤 상자 '" + boxID + "' 를 생성했습니다. (총 " + count + "개)"), true);

        return 1;
    }

    private static List<ItemStack> lookingAtContainerContents(ServerPlayer player) {
        HitResult hitResult = player.pick(5.0, 1.f, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) { return null; }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockEntity blockEntity = player.level().getBlockEntity(blockHit.getBlockPos());
        if (!(blockEntity instanceof Container container)) { return null; }

        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }

        return contents;
    }

    private static void syncAll(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            S2C_SyncRandomBoxPacket.send(player);
        }
    }
}