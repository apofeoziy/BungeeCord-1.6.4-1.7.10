package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class PlayerListItemRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        String playerName = Var.readString( in, false );
        Var.writeString( playerName, out, true );
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }

}
