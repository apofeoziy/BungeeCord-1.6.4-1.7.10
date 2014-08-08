package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class ChatMessageRewriter extends PacketRewriter
{
    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        String message = Var.readString( in, true );
        Var.writeString( message, out, false );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        String message = Var.readString( in, false );
        Var.writeString( message, out, true );
    }
}
