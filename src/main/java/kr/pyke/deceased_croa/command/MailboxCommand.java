package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.util.MailboxOpener;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;

public class MailboxCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("우편함").executes(MailboxCommand::openMailbox));
        dispatcher.register(Commands.literal("우편보내기")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("target", EntityArgument.players())
                .executes(MailboxCommand::sendMailbox)
            )
        );
        dispatcher.register(Commands.literal("우편테스트")
            .requires(source -> source.hasPermission(2))
            .executes(MailboxCommand::debug)
        );
    }

    public static int openMailbox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        MailboxOpener.open(player);

        return 1;
    }

    public static int debug(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        for (int i = 0; i < 45; ++i) {
            MailboxData mailboxData = MailboxData.create(new ItemStack(Items.STONE, 64));
            ModComponents.MAILBOX.get(player).addMail(mailboxData, true);
        }

        return 1;
    }

    public static int sendMailbox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");

        ItemStack heldItem = player.getMainHandItem();

        for (ServerPlayer target : players) {
            MailboxData mailboxData = MailboxData.create(heldItem.copy());
            ModComponents.MAILBOX.get(target).addMail(mailboxData, true);
        }
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), "메일 전송 완료!");

        return 1;
    }
}
